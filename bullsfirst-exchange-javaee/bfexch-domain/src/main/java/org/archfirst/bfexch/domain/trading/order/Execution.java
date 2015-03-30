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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.bfexch.domain.util.Constants;
import org.archfirst.common.datetime.DateTimeAdapter;
import org.archfirst.common.domain.DomainEntity;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.quantity.DecimalQuantityMin;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Execution
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Execution")
@Entity
public class Execution extends DomainEntity {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    private Execution() {
    }

    // Allow access only from Order
    Execution(
            DateTime creationTime,
            DecimalQuantity quantity,
            Money price) {
        this.creationTime = creationTime;
        this.quantity = quantity;
        this.price = price;
    }
    
    // ----- Attributes -----
    @XmlElement(name = "CreationTime", required = true)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name="dateTime")
    private DateTime creationTime;

    @XmlElement(name = "Quantity", required = true)
    private DecimalQuantity quantity;

    @XmlElement(name = "Price", required = true)
    private Money price;

    @XmlTransient
    private Order order;
    
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
        @AttributeOverride(name="amount",
            column = @Column(
                    name="price_amount",
                    precision=Constants.PRICE_PRECISION,
                    scale=Constants.PRICE_SCALE)),
        @AttributeOverride(name="currency",
            column = @Column(
                    name="price_currency",
                    length=Money.CURRENCY_LENGTH))
     })
    public Money getPrice() {
        return price;
    }
    private void setPrice(Money price) {
        this.price = price;
    }

    @ManyToOne
    public Order getOrder() {
        return order;
    }
    // Package scope to allow access from Order
    void setOrder(Order order) {
        this.order = order;
    }
}