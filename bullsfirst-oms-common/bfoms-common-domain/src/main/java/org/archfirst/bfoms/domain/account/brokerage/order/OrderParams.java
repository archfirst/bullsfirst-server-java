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
package org.archfirst.bfoms.domain.account.brokerage.order;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.archfirst.common.money.Money;

/**
 * OrderParams
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderParams")
public class OrderParams {

    // ----- Constructors -----
    public OrderParams() {
    }

    public OrderParams(
            OrderSide side,
            String symbol,
            BigDecimal quantity,
            OrderType type,
            Money limitPrice,
            OrderTerm term,
            boolean allOrNone) {
        this.side = side;
        this.symbol = symbol;
        this.quantity = quantity;
        this.type = type;
        this.limitPrice = limitPrice;
        this.term = term;
        this.allOrNone = allOrNone;
    }

    // ----- Queries -----
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("side=").append(side);
        builder.append(", symbol=").append(symbol);
        builder.append(", quantity=").append(quantity);
        builder.append(", type=").append(type);
        builder.append(", limitPrice=").append(limitPrice);
        builder.append(", term=").append(term);
        builder.append(", allOrNone=").append(allOrNone);
        return builder.toString();
    }

    // ----- Attributes -----
    @XmlElement(name = "Side", required = true)
    protected OrderSide side;
    @XmlElement(name = "Symbol", required = true)
    protected String symbol;
    @XmlElement(name = "Quantity", required = true)
    protected BigDecimal quantity;
    @XmlElement(name = "Type", required = true)
    protected OrderType type;
    @XmlElement(name = "LimitPrice", required = true, nillable = true)
    protected Money limitPrice;
    @XmlElement(name = "Term", required = true)
    protected OrderTerm term;
    @XmlElement(name = "AllOrNone")
    protected boolean allOrNone;

    // ----- Getters and Setters -----
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
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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
}