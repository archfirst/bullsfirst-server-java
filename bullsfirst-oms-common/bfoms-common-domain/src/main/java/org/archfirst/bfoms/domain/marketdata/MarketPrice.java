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
package org.archfirst.bfoms.domain.marketdata;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.archfirst.common.datetime.DateTimeUtil;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;

/**
 * MarketPrice
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MarketPrice")
public class MarketPrice {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    private MarketPrice() {
    }

    public MarketPrice(String symbol, Money price, DateTime effective) {
        this.symbol = symbol;
        this.price = price;
        this.effective = effective;
    }

    // ----- Commands -----
    /**
     * Business method to change market price. Also updates the effective date
     * @param price
     */
    public void change(Money price) {
        this.price = price;
        this.effective = new DateTime();
    }

    // ----- Queries -----
    /**
     * Returns this object as a set of properties. For example:
     * <code>
     *     instrument=AAPL 
     *     price=1.0645
     *     currency=USD
     *     effective=2009-01-02T09:00:00.000-04:00
     * </code>
     * 
     * @return this object as a set of properties
     */
    public String toProperties() {
        StringBuilder builder = new StringBuilder();
        builder.append("symbol=").append(symbol).append("\n");
        builder.append("price=").append(price.getAmount()).append("\n");
        builder.append("currency=").append(price.getCurrency()).append("\n");
        builder.append("effective=").append(effective.toString()).append("\n");
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(DateTimeUtil.toStringTimestamp(effective)).append("] ");
        builder.append(symbol).append(": ");
        builder.append(price);
        return builder.toString();
    }

    // ----- Attributes -----
    @XmlElement(name = "Symbol", required = true)
    private String symbol;

    @XmlElement(name = "Price", required = true)
    private Money price;

    @XmlElement(name = "Effective", required = true)
    private DateTime effective;

    // ----- Getters -----
    @NotNull
    public String getSymbol() {
        return symbol;
    }
    public Money getPrice() {
        return price;
    }
    public DateTime getEffective() {
        return effective;
    }
}