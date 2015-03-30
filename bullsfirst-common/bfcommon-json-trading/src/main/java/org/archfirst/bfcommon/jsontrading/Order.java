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
package org.archfirst.bfcommon.jsontrading;

/**
 * Order
 *
 * @author Naresh Bhatia
 */
public class Order {

    private String creationTime;
    private String clientOrderId;
    private OrderSide side;
    private String symbol;
    private int quantity;
    private OrderType type;
    private Money limitPrice;
    private OrderTerm term;
    private boolean allOrNone;
    private OrderStatus status;

    // ----- Constructor -----
    private Order() {
    }

    public Order(
            String creationTime,
            String clientOrderId,
            OrderSide side,
            String symbol,
            int quantity,
            OrderType type,
            Money limitPrice,
            OrderTerm term,
            boolean allOrNone,
            OrderStatus status) {
        this.creationTime = creationTime;
        this.clientOrderId = clientOrderId;
        this.side = side;
        this.symbol = symbol;
        this.quantity = quantity;
        this.type = type;
        this.limitPrice = limitPrice;
        this.term = term;
        this.allOrNone = allOrNone;
        this.status = status;
    }

    // ----- Getters and Setters -----
    public String getCreationTime() {
        return creationTime;
    }
    private void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }
    private void setClientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public OrderSide getSide() {
        return side;
    }
    private void setSide(OrderSide side) {
        this.side = side;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }
    private void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderType getType() {
        return type;
    }
    private void setType(OrderType type) {
        this.type = type;
    }

    public Money getLimitPrice() {
        return limitPrice;
    }
    private void setLimitPrice(Money limitPrice) {
        this.limitPrice = limitPrice;
    }

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

    public OrderStatus getStatus() {
        return status;
    }
    private void setStatus(OrderStatus status) {
        this.status = status;
    }
}