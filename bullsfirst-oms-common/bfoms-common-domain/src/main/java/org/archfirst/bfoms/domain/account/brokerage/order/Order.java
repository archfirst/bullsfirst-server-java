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
package org.archfirst.bfoms.domain.account.brokerage.order;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccount;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountRepository;
import org.archfirst.bfoms.domain.account.brokerage.Trade;
import org.archfirst.bfoms.domain.marketdata.MarketDataService;
import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.datetime.DateTimeAdapter;
import org.archfirst.common.datetime.DateTimeUtil;
import org.archfirst.common.domain.DomainEntity;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.quantity.DecimalQuantityMin;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Order
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order")
@Entity
@Table(name="Orders") // Oracle gets confused with table named "Order"
public class Order extends DomainEntity implements Comparable<Order> {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    private Order() {
    }
    
    public Order(OrderParams params) {
        this.setCreationTime(new DateTime());
        this.side = params.getSide();
        this.symbol = params.getSymbol();
        this.quantity = new DecimalQuantity(params.getQuantity());
        this.type = params.getType();
        if (this.type == OrderType.Limit) {
            this.limitPrice = params.getLimitPrice();
        }
        this.term = params.getTerm();
        this.allOrNone = params.isAllOrNone();
    }

    // ----- Commands -----
    /**
     * Processes the specified execution report and returns a trade if the
     * order is closed and all of the execution reports have been received.
     * Note that because of parallel processing of incoming messages it is
     * possible to receive execution reports in a random order. To work around
     * this issue, this method "closes" the order only if it is in a closed
     * state (Filled, Canceled or DoneForDay) and the filled quantity recorded
     * at close time is equal to the sum of quantities of the executions.
     * 
     * @param executionReport
     * @return a trade if the order is closed, otherwise null
     */
    public Trade processExecutionReport(
            ExecutionReport executionReport,
            BrokerageAccountRepository accountRepository,
            OrderEventPublisher orderEventPublisher) {
        
        // Record status if it is moving forward
        OrderStatus newStatus = executionReport.getOrderStatus();
        if (isStatusChangeForward(newStatus)) {
            this.status = executionReport.getOrderStatus();
            orderEventPublisher.publish(new OrderStatusChanged(this));
        }

        // Record CumQty if it is higher
        if (executionReport.getCumQty().gt(this.cumQty)) {
            this.cumQty = executionReport.getCumQty();
        }

        // If ExecutionReportType is Trade, then add an execution to this order
        if (executionReport.getType() == ExecutionReportType.Trade) {
            this.addExecution(
                    new Execution(
                            new DateTime(),
                            executionReport.getLastQty(),
                            executionReport.getLastPrice()));
        }
        
        // If order is closed and all executions received and something was traded,
        // then return a trade
        Trade trade = null;
        if (isClosed() &&
            (this.cumQty.eq(getCumQtyOfExecutions())) &&
            (this.cumQty.gt(DecimalQuantity.ZERO))) {
            
            trade = new Trade(
                    new DateTime(),
                    this.side,
                    this.symbol,
                    this.getCumQty(),
                    this.getTotalPriceOfExecutions(),
                    this.getFees(),
                    this);
        }
        return trade;
    }
    
    public void pendingCancel(OrderEventPublisher orderEventPublisher) {
        OrderStatus newStatus = OrderStatus.PendingCancel;
        if (isStatusChangeValid(newStatus)) {
            this.status = newStatus;
            orderEventPublisher.publish(new OrderStatusChanged(this));
        }
        else {
            throw new IllegalArgumentException(
                    "Can't change status from " + this.status + " to " + newStatus);
        }
    }

    public void cancelRequestRejected(
            OrderStatus newStatus,
            OrderEventPublisher orderEventPublisher) {
        this.status = newStatus;
        orderEventPublisher.publish(new OrderStatusChanged(this));
    }

    private void addExecution(Execution execution) {
        executions.add(execution);
        execution.setOrder(this);
    }

    // ----- Queries -----
    @Transient
    public DecimalQuantity getLeavesQty() {
        return quantity.minus(getCumQty());
    }
    
    @Transient
    public DecimalQuantity getCumQtyOfExecutions() {
        DecimalQuantity total = new DecimalQuantity();
        for (Execution execution : executions) {
            total = total.plus(execution.getQuantity());
        }
        return total; 
    }
    
    @Transient
    public Money getTotalPriceOfExecutions() {
        Money totalPrice = new Money("0.00");
        for (Execution execution : executions) {
            totalPrice = totalPrice.plus(
                    execution.getPrice().times(execution.getQuantity()));
        }
        return totalPrice.scaleToCurrency();
    }
    
    @Transient
    public Money getWeightedAveragePriceOfExecutions() {
        if (executions.size() == 0) {
            return new Money("0.00");
        }

        // Calculate weighted average
        Money totalPrice = new Money("0.00");
        DecimalQuantity totalQuantity = new DecimalQuantity();
        for (Execution execution : executions) {
            totalPrice = totalPrice.plus(
                    execution.getPrice().times(execution.getQuantity()));
            totalQuantity = totalQuantity.plus(execution.getQuantity());
        }
        totalPrice = totalPrice.scaleToCurrency();
        return totalPrice.div(totalQuantity, Constants.PRICE_SCALE);
    }
    
    @Transient
    private Money getFees() {
        return FIXED_FEE;
    }
    
    /**
     * Returns estimated price and compliance of the order.
     * 
     * @param marketDataService
     * @return
     */
    public OrderEstimate calculateOrderEstimate(MarketDataService marketDataService) {

        // Perform order level compliance
        if (type == OrderType.Limit && limitPrice == null) {
            return new OrderEstimate(OrderCompliance.LimitOrderWithNoLimitPrice);
        }
        
        // Calculate estimated values
        Money unitPrice = (type == OrderType.Market) ?
                marketDataService.getMarketPrice(symbol) : limitPrice;
        Money estimatedValue = unitPrice.times(quantity).scaleToCurrency();
        Money fees = getFees();
        Money estimatedValueInclFees = (side==OrderSide.Buy) ?
                estimatedValue.plus(fees) : estimatedValue.minus(fees);
        return new OrderEstimate(estimatedValue, fees, estimatedValueInclFees);
    }
    
    /** Returns true if this order is closed (Filled, Canceled or DoneForDay) */
    @Transient
    public boolean isClosed() {
        return
            (status==OrderStatus.Filled) ||
            (status==OrderStatus.Canceled) ||
            (status==OrderStatus.DoneForDay);
    }

    @Transient
    private boolean isStatusChangeValid(OrderStatus newStatus) {
        boolean result = false;
        switch (this.status) {
            case PendingNew:
                if (newStatus==OrderStatus.New)
                    result=true;
                break;
            case New:
            case PartiallyFilled:
                if (newStatus==OrderStatus.PartiallyFilled ||
                    newStatus==OrderStatus.Filled ||
                    newStatus==OrderStatus.PendingCancel ||
                    newStatus==OrderStatus.Canceled ||
                    newStatus==OrderStatus.DoneForDay)
                    result=true;
                break;
            case PendingCancel:
                if (newStatus==OrderStatus.Canceled)
                    result=true;
                break;
            case Filled:
            case Canceled:
            case DoneForDay:
            default:
                break;
        }
        
        return result;
    }
    
    @Transient
    private boolean isStatusChangeForward(OrderStatus newStatus) {
        boolean result = false;
        switch (this.status) {
            case PendingNew:
                if (newStatus==OrderStatus.New ||
                    newStatus==OrderStatus.PartiallyFilled ||
                    newStatus==OrderStatus.Filled ||
                    newStatus==OrderStatus.PendingCancel ||
                    newStatus==OrderStatus.Canceled ||
                    newStatus==OrderStatus.DoneForDay)
                    result=true;
                break;
            case New:
            case PartiallyFilled:
                if (newStatus==OrderStatus.PartiallyFilled ||
                    newStatus==OrderStatus.Filled ||
                    newStatus==OrderStatus.PendingCancel ||
                    newStatus==OrderStatus.Canceled ||
                    newStatus==OrderStatus.DoneForDay)
                    result=true;
                break;
            case PendingCancel:
                if (newStatus==OrderStatus.New ||
                    newStatus==OrderStatus.PartiallyFilled ||
                    newStatus==OrderStatus.Filled ||
                    newStatus==OrderStatus.Canceled ||
                    newStatus==OrderStatus.DoneForDay)
                    result=true;
                break;
            case Filled:
            case Canceled:
            case DoneForDay:
            default:
                break;
        }
        
        return result;
    }
    
    @Override
    public int compareTo(Order that) {
        return this.id.compareTo(that.getId());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(DateTimeUtil.toStringTimestamp(creationTime)).append(" ");
        builder.append(id).append(": ");
        builder.append(side).append(" ");
        builder.append(symbol);
        builder.append(", quantity=").append(quantity);
        builder.append(", cumQy=").append(this.getCumQty());
        builder.append(", orderType=").append(type);
        builder.append(", limitPrice=").append(limitPrice);
        builder.append(", term=").append(term);
        builder.append(", allOrNone=").append(allOrNone);
        builder.append(", status=").append(status);
        builder.append(", accountId=").append(account.getId());
        return builder.toString();
    }

    // ----- Attributes -----
    private static final Money FIXED_FEE = new Money("10.00");
    
    @XmlElement(name = "CreationTime", required = true)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name="dateTime")
    private DateTime creationTime;

    @XmlElement(name = "Side", required = true)
    private OrderSide side;

    @XmlElement(name = "Symbol", required = true)
    private String symbol;

    @XmlElement(name = "Quantity", required = true)
    private DecimalQuantity quantity;

    @XmlElement(name = "CumQty", required = true)
    private DecimalQuantity cumQty = DecimalQuantity.ZERO;

    @XmlElement(name = "OrderType", required = true)
    private OrderType type;

    // Limit price is not a required in case of market orders
    @XmlElement(name = "LimitPrice")
    private Money limitPrice;

    @XmlElement(name = "Term", required = true)
    private OrderTerm term;

    @XmlElement(name = "AllOrNone", required = true)
    private boolean allOrNone;

    @XmlElement(name = "OrderStatus", required = true)
    private OrderStatus status = OrderStatus.PendingNew;

    @XmlTransient
    private BrokerageAccount account;

    @XmlElement(name = "Execution", required = true)
    private Set<Execution> executions = new HashSet<Execution>();
    
    // ----- Getters and Setters -----
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(nullable = false)
    public DateTime getCreationTime() {
        return creationTime;
    }
    // Allow BrokerageAccount to access this method
    private void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    @NotNull
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.brokerage.order.OrderSide")
            }
        )
    @Column(nullable = false, length=Constants.ENUM_COLUMN_LENGTH)
    public OrderSide getSide() {
        return side;
    }
    private void setSide(OrderSide side) {
        this.side = side;
    }

    @NotNull
    @Column(nullable = false, length=10)
    public String getSymbol() {
        return symbol;
    }
    private void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @NotNull
    @DecimalQuantityMin(value="1")
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="value",
            column = @Column(
                    name="quantity",
                    precision=Constants.QUANTITY_PRECISION,
                    scale=Constants.QUANTITY_SCALE))})
    public DecimalQuantity getQuantity() {
        return quantity;
    }
    private void setQuantity(DecimalQuantity quantity) {
        this.quantity = quantity;
    }

    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="value",
            column = @Column(
                    name="cum_qty",
                    precision=Constants.QUANTITY_PRECISION,
                    scale=Constants.QUANTITY_SCALE))})
    public DecimalQuantity getCumQty() {
        return cumQty; 
    }
    private void setCumQty(DecimalQuantity cumQty) {
        this.cumQty = cumQty;
    }

    @NotNull
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.brokerage.order.OrderType")
            }
        )
    @Column(nullable = false, length=Constants.ENUM_COLUMN_LENGTH)
    public OrderType getType() {
        return type;
    }
    private void setType(OrderType type) {
        this.type = type;
    }

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="amount",
            column = @Column(
                    name="limit_price_amount",
                    precision=Constants.PRICE_PRECISION,
                    scale=Constants.PRICE_SCALE)),
        @AttributeOverride(name="currency",
            column = @Column(
                    name="limit_price_currency",
                    length=Money.CURRENCY_LENGTH))
     })
    public Money getLimitPrice() {
        return limitPrice;
    }
    private void setLimitPrice(Money limitPrice) {
        this.limitPrice = limitPrice;
    }

    @NotNull
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.brokerage.order.OrderTerm")
            }
        )
    @Column(nullable = false, length=Constants.ENUM_COLUMN_LENGTH)
    public OrderTerm getTerm() {
        return term;
    }
    private void setTerm(OrderTerm term) {
        this.term = term;
    }

    public boolean isAllOrNone() {
        return allOrNone;
    }
    private void setAllOrNone(boolean allOrNone) {
        this.allOrNone = allOrNone;
    }

    @NotNull
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.brokerage.order.OrderStatus")
            }
        )
    @Column(nullable = false, length=Constants.ENUM_COLUMN_LENGTH)
    public OrderStatus getStatus() {
        return status;
    }
    private void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    @ManyToOne
    public BrokerageAccount getAccount() {
        return account;
    }
    // Allow BrokerageAccount to access this method
    public void setAccount(BrokerageAccount account) {
        this.account = account;
    }
    
    @OneToMany(mappedBy="order",  cascade=CascadeType.ALL)
    @OptimisticLock(excluded = true)
    public Set<Execution> getExecutions() {
        return executions;
    }
    private void setExecutions(Set<Execution> executions) {
        this.executions = executions;
    }
}