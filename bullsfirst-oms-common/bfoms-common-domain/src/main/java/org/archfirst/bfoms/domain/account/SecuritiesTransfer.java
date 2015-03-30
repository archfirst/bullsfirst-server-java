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
package org.archfirst.bfoms.domain.account;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;

/**
 * SecuritiesTransfer
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SecuritiesTransfer")
@Entity
public class SecuritiesTransfer extends Transaction {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    protected SecuritiesTransfer() {
    }

    public SecuritiesTransfer(
            DateTime creationTime,
            String symbol,
            DecimalQuantity quantity,
            Money pricePaidPerShare,
            BaseAccount otherAccount) {
        super(creationTime);
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePaidPerShare = pricePaidPerShare;
        this.otherAccount = otherAccount;
    }

    // ----- Commands -----

    // ----- Queries -----
    @Override
    @Transient
    public String getType() {
        return TYPE;
    }

    @Override
    @Transient
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("Transfer ");
        builder.append(quantity.abs()).append(" shares of ").append(symbol);
        builder.append(quantity.isPlus() ? " from " : " to ");
        builder.append(otherAccount.getName());
        return builder.toString();
    }

    @Override
    @Transient
    public Money getAmount() {
        // A securities transfer does not involve money
        return new Money("0.00");
    }

    // ----- Attributes -----
    private static final String TYPE = "Transfer";

    @XmlElement(name = "Symbol", required = true)
    private String symbol;

    @XmlElement(name = "Quantity", required = true)
    private DecimalQuantity quantity;

    @XmlElement(name = "PricePaidPerShare", required = true)
    private Money pricePaidPerShare;

    @XmlTransient
    private BaseAccount otherAccount;

    // ----- Getters and Setters -----
    @NotNull
    @Column(nullable = false, length=10)
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @NotNull
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

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="amount",
            column = @Column(
                    name="price_paid_amount",
                    precision=Constants.MONEY_PRECISION,
                    scale=Constants.MONEY_SCALE)),
        @AttributeOverride(name="currency",
            column = @Column(
                    name="price_paid_currency",
                    length=Money.CURRENCY_LENGTH))
     })
    public Money getPricePaidPerShare() {
        return pricePaidPerShare;
    }
    public void setPricePaidPerShare(Money pricePaidPerShare) {
        this.pricePaidPerShare = pricePaidPerShare;
    }

    @NotNull
    @ManyToOne
    public BaseAccount getOtherAccount() {
        return otherAccount;
    }
    private void setOtherAccount(BaseAccount otherAccount) {
        this.otherAccount = otherAccount;
    }
}