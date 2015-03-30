/**
 * Copyright 2011 Archfirst
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
package org.archfirst.bfexch.domain.trading.order;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.bfexch.domain.util.Constants;
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
    
    public Order(
            DateTime creationTime,
            String clientOrderId,
            OrderSide side,
            String symbol,
            DecimalQuantity quantity,
            OrderType type,
            Money limitPrice,
            OrderTerm term,
            boolean allOrNone) {
        this.creationTime = creationTime;
        this.clientOrderId = clientOrderId;
        this.side = side;
        this.symbol = symbol;
        this.quantity = quantity;
        this.type = type;
        this.limitPrice = limitPrice;
        this.term = term;
        this.allOrNone = allOrNone;
    }
    
    // ----- Commands -----
    /**
     * Sets order status to New and persists it.
     * @param orderRepository
     */
    public void accept(OrderRepository orderRepository) {
        this.status = OrderStatus.New;
        orderRepository.persist(this);
        orderRepository.flush();
    }
    
    /**
     * Executes the order and adds an execution to it.
     */
    public Execution execute(
            OrderRepository orderRepository,
            DateTime executionTime,
            DecimalQuantity executionQty,
            Money price) {
        Execution execution = new Execution(executionTime, executionQty, price);
        this.addExecution(execution, orderRepository);
        this.status = quantity.eq(getCumQty()) ?
                OrderStatus.Filled : OrderStatus.PartiallyFilled;
        return execution;
    }

    /**
     * Cancels the order if the status change is valid
     */
    public void cancel() {
        if (isStatusChangeValid(OrderStatus.Canceled)) {
            this.status = OrderStatus.Canceled;
        }
    }
    
    public void doneForDay() {
        this.status = OrderStatus.DoneForDay;
    }

    private void addExecution(Execution execution, OrderRepository orderRepository) {
        execution.setOrder(this);
        orderRepository.persist(execution);
        orderRepository.flush(); // get execution id before adding to set
        executions.add(execution);
    }
    
    // ----- Queries -----
    @Transient
    public DecimalQuantity getLeavesQty() {
        return quantity.minus(getCumQty());
    }
    
    @Transient
    public DecimalQuantity getCumQty() {
        DecimalQuantity cumQty = new DecimalQuantity();
        for (Execution execution : executions) {
            cumQty = cumQty.plus(execution.getQuantity());
        }
        return cumQty; 
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
    
    /** Returns true if this order is filled */
    @Transient
    public boolean isFilled() {
        return (status==OrderStatus.Filled);
    }

    /** Returns true if this order is active (New or PartiallyFilled) */
    @Transient
    public boolean isActive() {
        return
            (status==OrderStatus.New) ||
            (status==OrderStatus.PartiallyFilled);
    }

    /**
     * Compares two orders. Note that we need a reverse sort, i.e. lower indexes
     * should be considered first for matching. So higher priority order should
     * return -1, lower  priority order should return +1.
     */
    @Override
    public int compareTo(Order that) {
        if (this == that) {
            return 0;
        }
        
        // Buy orders can't really be compared to sell orders
        // In such cases give higher priority to buy orders!
        if (this.side != that.side) {
            return (this.side == OrderSide.Buy) ? -1 : 1;
        }
        
        // If types are not the same, then market order takes priority
        if (this.type != that.type) {
            return (this.type == OrderType.Market) ? -1 : 1;
        }

        // *** At this point types are the same ***
        
        // For limit orders, higher price takes priority for buy orders
        // and lower price takes priority for sell orders
        if (this.type == OrderType.Limit &&
            !this.getLimitPrice().eq(that.getLimitPrice())) {
            if (this.side == OrderSide.Buy) {
                return that.getLimitPrice().compareTo(this.getLimitPrice());
            }
            else {
                return this.getLimitPrice().compareTo(that.getLimitPrice());
            }
        }
        
        // *** All criteria are the same, use creation time as the tie breaker ***
        // Note that lower creation time takes priority
        return this.creationTime.compareTo(that.creationTime);
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
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(DateTimeUtil.toStringTimestamp(creationTime)).append(" ");
        builder.append(id).append("(").append(clientOrderId).append("): ");
        builder.append(side).append(" ");
        builder.append(symbol);
        builder.append(", quantity=").append(quantity);
        builder.append(", cumQy=").append(this.getCumQty());
        builder.append(", orderType=").append(type);
        builder.append(", limitPrice=").append(limitPrice);
        builder.append(", term=").append(term);
        builder.append(", allOrNone=").append(allOrNone);
        builder.append(", status=").append(status);
        return builder.toString();
    }
    
    // ----- Attributes -----
    @XmlElement(name = "CreationTime", required = true)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name="dateTime")
    private DateTime creationTime;

    @XmlElement(name = "ClientOrderId", required = true)
    private String clientOrderId;

    @XmlElement(name = "Side", required = true)
    private OrderSide side;

    @XmlElement(name = "Symbol", required = true)
    private String symbol;

    @XmlElement(name = "Quantity", required = true)
    private DecimalQuantity quantity;

    @XmlElement(name = "OrderType", required = true)
    private OrderType type;

    @XmlElement(name = "LimitPrice", required = true)
    private Money limitPrice;

    @XmlElement(name = "Term", required = true)
    private OrderTerm term;

    @XmlElement(name = "AllOrNone", required = true)
    private boolean allOrNone;

    @XmlElement(name = "OrderStatus", required = true)
    private OrderStatus status = OrderStatus.PendingNew;

    @XmlElement(name = "Execution", required = true)
    private Set<Execution> executions = new HashSet<Execution>();

    // ----- Getters and Setters -----
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(nullable = false)
    public DateTime getCreationTime() {
        return creationTime;
    }
    private void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }

    @NotNull
    @Column(nullable = false, length=50)
    public String getClientOrderId() {
        return clientOrderId;
    }
    private void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    @NotNull
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfexch.domain.trading.order.OrderSide")
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
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfexch.domain.trading.order.OrderType")
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
                value = "org.archfirst.bfexch.domain.trading.order.OrderTerm")
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
                value = "org.archfirst.bfexch.domain.trading.order.OrderStatus")
            }
        )
    @Column(nullable = false, length=Constants.ENUM_COLUMN_LENGTH)
    public OrderStatus getStatus() {
        return status;
    }
    private void setStatus(OrderStatus status) {
        this.status = status;
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