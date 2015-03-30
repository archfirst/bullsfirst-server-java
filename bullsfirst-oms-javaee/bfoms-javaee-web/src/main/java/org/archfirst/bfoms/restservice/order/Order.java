/**
 * Copyright 2012 Archfirst
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
package org.archfirst.bfoms.restservice.order;

import java.util.HashSet;
import java.util.Set;

import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderStatus;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderTerm;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderType;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;

/**
 * Order
 *
 * @author Naresh Bhatia
 */
public class Order {
    // ----- Attributes -----
    private Long id;
    private DateTime creationTime;
    private OrderSide side;
    private String symbol;
    private DecimalQuantity quantity;
    private DecimalQuantity cumQty;
    private OrderType type;
    private Money limitPrice;
    private OrderTerm term;
    private boolean allOrNone;
    private OrderStatus status;
    private Long accountId;
    private String accountName;
    private Set<Execution> executions = new HashSet<Execution>();
    
    // ----- Getters and Setters -----
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DateTime getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }
    public OrderSide getSide() {
        return side;
    }
    public void setSide(OrderSide side) {
        this.side = side;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public DecimalQuantity getQuantity() {
        return quantity;
    }
    public void setQuantity(DecimalQuantity quantity) {
        this.quantity = quantity;
    }
    public DecimalQuantity getCumQty() {
        return cumQty;
    }
    public void setCumQty(DecimalQuantity cumQty) {
        this.cumQty = cumQty;
    }
    public OrderType getType() {
        return type;
    }
    public void setType(OrderType type) {
        this.type = type;
    }
    public Money getLimitPrice() {
        return limitPrice;
    }
    public void setLimitPrice(Money limitPrice) {
        this.limitPrice = limitPrice;
    }
    public OrderTerm getTerm() {
        return term;
    }
    public void setTerm(OrderTerm term) {
        this.term = term;
    }
    public boolean isAllOrNone() {
        return allOrNone;
    }
    public void setAllOrNone(boolean allOrNone) {
        this.allOrNone = allOrNone;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public Set<Execution> getExecutions() {
        return executions;
    }
    public void setExecutions(Set<Execution> executions) {
        this.executions = executions;
    }
}