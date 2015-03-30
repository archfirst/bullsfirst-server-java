/**
 * Copyright 2010 Archfirst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.archfirst.bfoms.domain.account.brokerage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.archfirst.bfoms.domain.account.AccountStatus;
import org.archfirst.bfoms.domain.account.BaseAccount;
import org.archfirst.bfoms.domain.account.CashTransfer;
import org.archfirst.bfoms.domain.account.OwnershipType;
import org.archfirst.bfoms.domain.account.SecuritiesTransfer;
import org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReport;
import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderCompliance;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderCreated;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderEstimate;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderEventPublisher;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderParams;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;
import org.archfirst.bfoms.domain.marketdata.MarketDataService;
import org.archfirst.bfoms.domain.referencedata.ReferenceDataService;
import org.archfirst.bfoms.domain.security.User;
import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrokerageAccount
 *
 * @author Naresh Bhatia
 */
@Entity
public class BrokerageAccount extends BaseAccount {
    private static final long serialVersionUID = 1L;
    private static final Logger logger =
        LoggerFactory.getLogger(BrokerageAccountFactory.class);
    
    // ----- Constructors -----
    private BrokerageAccount() {
    }

    // Allow access only from AccountService
    BrokerageAccount(String name, AccountStatus status,
            OwnershipType ownershipType, Money cashPosition) {
        super(name, status);
        this.ownershipType = ownershipType;
        this.cashPosition = cashPosition;
    }

    // ----- Commands -----
    @Override
    public void transferCash(CashTransfer transfer) {
        cashPosition = cashPosition.plus(transfer.getAmount());
        this.addTransaction(transfer);
    }
    
    @Override
    public void transferSecurities(SecuritiesTransfer transfer) {
        if (transfer.getQuantity().isPlus()) {
            depositSecurities(
                    transfer.getSymbol(),
                    transfer.getQuantity(),
                    transfer.getPricePaidPerShare(),
                    new SecuritiesTransferAllocationFactory(brokerageAccountRepository, transfer));
        }
        else {
            withdrawSecurities(    
                transfer.getSymbol(),
                transfer.getQuantity().negate(),
                new SecuritiesTransferAllocationFactory(brokerageAccountRepository, transfer));
        }
        this.addTransaction(transfer);
    }

    private void depositSecurities(
            String symbol,
            DecimalQuantity quantity,
            Money pricePaidPerShare,
            AllocationFactory factory) {

        // Find lots for the specified instrument
        List<Lot> lots =
            brokerageAccountRepository.findActiveLots(this, symbol);

        // First deposit to lots that have negative quantity (unusual case)
        DecimalQuantity quantityLeftToDeposit = quantity;
        for (Lot lot : lots) {
            DecimalQuantity lotQuantity = lot.getQuantity();
            if (lotQuantity.isMinus()) {
                DecimalQuantity depositQuantity = lotQuantity.negate();
                lot.buy(depositQuantity, pricePaidPerShare);
                lot.addAllocation(factory.createAllocation(depositQuantity));
                quantityLeftToDeposit = quantityLeftToDeposit.minus(depositQuantity);
                if (quantityLeftToDeposit.isZero()) {
                    break;
                }
            }
        }

        // Put the remaining quantity in a new lot
        if (quantityLeftToDeposit.isPlus()) {
            Lot lot = new Lot(
                new DateTime(), symbol, quantityLeftToDeposit, pricePaidPerShare);
            brokerageAccountRepository.persistAndFlush(lot);
            this.addLot(lot);
            lot.addAllocation(factory.createAllocation(quantityLeftToDeposit));
        }
    }

    private void withdrawSecurities(
            String symbol,
            DecimalQuantity quantity,
            AllocationFactory factory) {

        // Find lots for the specified instrument
        List<Lot> lots =
            brokerageAccountRepository.findActiveLots(this, symbol);

        // Withdraw specified quantity from available lots
        DecimalQuantity quantityLeftToWithdraw = quantity;
        for (Lot lot : lots) {
            DecimalQuantity lotQuantity = lot.getQuantity();
            if (lotQuantity.isMinus()) {
                continue;
            }
            DecimalQuantity withdrawQuantity =
                (lotQuantity.gteq(quantityLeftToWithdraw)) ?
                        quantityLeftToWithdraw : lotQuantity;
            lot.sell(withdrawQuantity);
            lot.addAllocation(factory.createAllocation(withdrawQuantity.negate()));
            quantityLeftToWithdraw = quantityLeftToWithdraw.minus(withdrawQuantity);
            if (quantityLeftToWithdraw.isZero()) {
                break;
            }
        }

        // If some quantity remains (unusual case), just create a lot with
        // negative quantity. This case can happen if a long standing sell
        // order gets executed.
        if (quantityLeftToWithdraw.isPlus()) {
            Lot lot = new Lot(
                    new DateTime(),
                    symbol,
                    quantityLeftToWithdraw.negate(),
                    new Money("0.00"));
                brokerageAccountRepository.persistAndFlush(lot);
                this.addLot(lot);
                lot.addAllocation(factory.createAllocation(quantityLeftToWithdraw));
        }
    }
    
    public Order placeOrder(OrderParams params, MarketDataService marketDataService) {
        // Check compliance
        OrderEstimate orderEstimate =
            this.calculateOrderEstimate(params, marketDataService);
        if (orderEstimate.getCompliance() != OrderCompliance.Compliant) {
            throw new IllegalArgumentException("Order is not compliant: " + orderEstimate.getCompliance());
        }

        // Place order
        Order order = new Order(params);
        brokerageAccountRepository.persistAndFlush(order);
        this.addOrder(order);
        orderEventPublisher.publish(new OrderCreated(order));
        return order;
    }
    
    public void processExecutionReport(ExecutionReport executionReport) {
        logger.debug("Processing ExecutionReport: {}", executionReport);
        Order order =
            brokerageAccountRepository.findOrder(executionReport.getClientOrderId());
        Trade trade = order.processExecutionReport(
                executionReport, brokerageAccountRepository, orderEventPublisher);
        if (trade != null) {
            this.addTransaction(trade);
            if (trade.getSide() == OrderSide.Buy) {
                cashPosition = cashPosition.plus(
                        trade.getAmount()); // trade.amount is negative
                depositSecurities(
                        trade.getSymbol(),
                        trade.getQuantity(),
                        trade.getPricePerShare(),
                        new TradeAllocationFactory(brokerageAccountRepository, trade));
            }
            else {
                cashPosition = cashPosition.plus(
                        trade.getAmount()); // trade.amount is positive
                withdrawSecurities(    
                        trade.getSymbol(),
                        trade.getQuantity(),
                        new TradeAllocationFactory(brokerageAccountRepository, trade));
            }
        }
    }

    private void addOrder(Order order) {
        orders.add(order);
        order.setAccount(this);
    }

    private void addLot(Lot lot) {
        lots.add(lot);
        lot.setAccount(this);
    }

    // ----- Queries -----
    @Transient
    public BrokerageAccountSummary getAccountSummary(
            User user,
            ReferenceDataService referenceDataService,
            MarketDataService marketDataService) {
        
        List<Position> positions =
            this.getPositions(referenceDataService, marketDataService);
        Money marketValue = new Money();
        for (Position position : positions) {
            marketValue = marketValue.plus(position.getMarketValue());
        }
        
        List<BrokerageAccountPermission> permissions =
            brokerageAccountRepository.findPermissionsForAccount(user, this);
        
        return new BrokerageAccountSummary(
                this.id,
                this.name,
                this.cashPosition,
                marketValue,
                permissions.contains(BrokerageAccountPermission.Edit),
                permissions.contains(BrokerageAccountPermission.Trade),
                permissions.contains(BrokerageAccountPermission.Transfer),
                positions);
    }

    @Transient
    public List<Position> getPositions(
            ReferenceDataService referenceDataService,
            MarketDataService marketDataService) {
        
        List<Lot> lots = brokerageAccountRepository.findActiveLots(this);
        
        // Process lots into a hierarchy of instrument and lot positions
                
        // Create instrument positions that will be returned
        List<Position> instrumentPositions = new ArrayList<Position>();
        
        // Now process lots, breaking them by instrument positions
        String symbol = null;
        Position instrumentPosition = null;
        for (Lot lot : lots) {
            // If there is a change in symbol, then create a new instumentPosition
            if (symbol==null || !symbol.equals(lot.getSymbol())) {
                symbol = lot.getSymbol();
                instrumentPosition = new Position();
                instrumentPosition.setInstrumentPosition(
                        this.id,
                        this.name,
                        lot.getSymbol(),
                        referenceDataService.lookup(lot.getSymbol()).getName(),
                        marketDataService.getMarketPrice(lot.getSymbol()));
                instrumentPositions.add(instrumentPosition);
            }
            Position lotPosition = new Position();
            lotPosition.setLotPosition(
                    this.id,
                    this.name,
                    lot.getSymbol(),
                    referenceDataService.lookup(lot.getSymbol()).getName(),
                    lot.getId(),
                    lot.getCreationTime(),
                    lot.getQuantity(),
                    marketDataService.getMarketPrice(lot.getSymbol()),
                    lot.getPricePaidPerShare());
            instrumentPosition.addChild(lotPosition);
        }
        
        // Calculate instrument position values
        for (Position position : instrumentPositions) {
            position.calculateInstrumentPosition();
        }
        
        
        // Add cash position
        Position cashPosition = new Position();
        cashPosition.setCashPosition(
                this.id,
                this.name,
                this.cashPosition);
        instrumentPositions.add(cashPosition);
        
        return instrumentPositions;
    }
    
    @Override
    public boolean isCashAvailable(
            Money amount,
            MarketDataService marketDataService) {
        return amount.lteq(calculateCashAvailable(marketDataService));
    }
    
    public Money calculateCashAvailable(MarketDataService marketDataService) {

        Money cashAvailable = cashPosition;
        
        // Reduce cash available by estimated cost of buy orders
        List<Order> orders = brokerageAccountRepository.findActiveBuyOrders(this);
        for (Order order : orders) {
            OrderEstimate orderEstimate =
                order.calculateOrderEstimate(marketDataService);
            cashAvailable =
                cashAvailable.minus(orderEstimate.getEstimatedValueInclFees());
        }
        
        return cashAvailable;
    }
    

    @Override
    public boolean isSecurityAvailable(
            String symbol,
            DecimalQuantity quantity) {
        return quantity.lteq(calculateSecurityAvailable(symbol));
    }
    
    public DecimalQuantity calculateSecurityAvailable(String symbol) {

        DecimalQuantity securityAvailable =
            brokerageAccountRepository.getNumberOfShares(this, symbol);

        // Reduce security available by estimated quantity of sell orders
        List<Order> orders =
            brokerageAccountRepository.findActiveSellOrders(this, symbol);
        for (Order order : orders) {
            securityAvailable = securityAvailable.minus(order.getQuantity());
        }
        
        return securityAvailable;
    }

    public OrderEstimate calculateOrderEstimate(
            OrderParams params,
            MarketDataService marketDataService) {

        Order order = new Order(params);
        OrderEstimate orderEstimate = order.calculateOrderEstimate(marketDataService);

        // Determine account level compliance
        if (orderEstimate.getCompliance() == null) {
            OrderCompliance compliance = (order.getSide() == OrderSide.Buy) ?
                    calculateBuyOrderCompliance(order, orderEstimate, marketDataService) :
                    calculateSellOrderCompliance(order);
            orderEstimate.setCompliance(compliance);
        }

        return orderEstimate;
    }
    
    private OrderCompliance calculateBuyOrderCompliance(
            Order order,
            OrderEstimate orderEstimate,
            MarketDataService marketDataService) {

        // Check if sufficient cash is available
        return isCashAvailable(
                orderEstimate.getEstimatedValueInclFees(),
                marketDataService) ?
                    OrderCompliance.Compliant :
                    OrderCompliance.InsufficientFunds;
    }

    private OrderCompliance calculateSellOrderCompliance(Order order) {

        // Check if sufficient securities are available
        return isSecurityAvailable(
                order.getSymbol(), order.getQuantity()) ?
                OrderCompliance.Compliant : OrderCompliance.InsufficientQuantity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(" - ");
        builder.append(id).append(" | ");
        builder.append(cashPosition);
        return builder.toString();
    }

    // ----- Attributes -----
    private OwnershipType ownershipType = OwnershipType.Individual;
    private Money cashPosition = new Money("0.00");
    private Set<Order> orders = new HashSet<Order>();
    private Set<Lot> lots = new HashSet<Lot>();
    
    @Transient
    private BrokerageAccountRepository brokerageAccountRepository;

    @Transient
    private OrderEventPublisher orderEventPublisher;

    // ----- Getters and Setters -----
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.OwnershipType")
            }
    )
    @Column(length=Constants.ENUM_COLUMN_LENGTH)
    public OwnershipType getOwnershipType() {
        return ownershipType;
    }
    private void setOwnershipType(OwnershipType ownershipType) {
        this.ownershipType = ownershipType;
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="amount",
            column = @Column(
                    name="cashpos_amount",
                    precision=Constants.MONEY_PRECISION,
                    scale=Constants.MONEY_SCALE)),
        @AttributeOverride(name="currency",
            column = @Column(
                    name="cashpos_currency",
                    length=Money.CURRENCY_LENGTH))
     })
    public Money getCashPosition() {
        return cashPosition;
    }
    private void setCashPosition(Money cashPosition) {
        this.cashPosition = cashPosition;
    }

    @OneToMany(mappedBy="account",  cascade=CascadeType.ALL)
    @OptimisticLock(excluded = true)
    public Set<Order> getOrders() {
        return orders;
    }
    private void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @OneToMany(mappedBy="account", cascade=CascadeType.ALL)
    @OptimisticLock(excluded = true)
    public Set<Lot> getLots() {
        return lots;
    }
    private void setLots(Set<Lot> lots) {
        this.lots = lots;
    }
    
    // Needed by BrokerageAccountRepository
    void setBrokerageAccountRepository(
            BrokerageAccountRepository brokerageAccountRepository) {
        this.brokerageAccountRepository = brokerageAccountRepository;
    }

    // Needed by BrokerageAccountRepository
    void setOrderEventPublisher(OrderEventPublisher orderEventPublisher) {
        this.orderEventPublisher = orderEventPublisher;
    }

    // ----- AllocationFactory -----

    private abstract class AllocationFactory {
        
        protected BrokerageAccountRepository accountRepository;
        
        public AllocationFactory(BrokerageAccountRepository accountRepository) {
            this.accountRepository = accountRepository;
        }
        
        public abstract Allocation createAllocation(DecimalQuantity quantity);
    }
    
    private class SecuritiesTransferAllocationFactory extends AllocationFactory {
        
        private SecuritiesTransfer transfer;
        
        public SecuritiesTransferAllocationFactory(
                BrokerageAccountRepository accountRepository,
                SecuritiesTransfer transfer) {
            super(accountRepository);
            this.transfer = transfer;
        }

        @Override
        public Allocation createAllocation(DecimalQuantity quantity) {
            SecuritiesTransferAllocation allocation =
                new SecuritiesTransferAllocation(quantity, transfer);
            accountRepository.persistAndFlush(allocation);
            return allocation;
        }
    }
    
    private class TradeAllocationFactory extends AllocationFactory {
        
        private Trade trade;
        
        public TradeAllocationFactory(
                BrokerageAccountRepository accountRepository,
                Trade trade) {
            super(accountRepository);
            this.trade = trade;
        }

        @Override
        public Allocation createAllocation(DecimalQuantity quantity) {
            TradeAllocation allocation =
                new TradeAllocation(quantity, trade);
            accountRepository.persistAndFlush(allocation);
            return allocation;
        }
    }
}