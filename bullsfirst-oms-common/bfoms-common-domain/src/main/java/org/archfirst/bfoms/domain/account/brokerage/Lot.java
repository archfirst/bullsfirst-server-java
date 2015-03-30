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

import java.util.Comparator;
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
import javax.validation.constraints.NotNull;

import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.datetime.DateTimeUtil;
import org.archfirst.common.domain.DomainEntity;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Lot
 *
 * @author Naresh Bhatia
 */
@Entity
public class Lot extends DomainEntity {
    private static final long serialVersionUID = 1L;
    
    // ----- Constructors -----
    private Lot() {
    }
    
    public Lot(
            DateTime creationTime,
            String symbol,
            DecimalQuantity quantity,
            Money pricePaidPerShare) {
        this.creationTime = creationTime;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePaidPerShare = pricePaidPerShare;
    }

    // ----- Commands -----
    // Allow access only from BrokerageAccount
    void buy(DecimalQuantity quantityToBuy, Money pricePaidPerShare) {
        quantity = quantity.plus(quantityToBuy);
        this.pricePaidPerShare = pricePaidPerShare;
    }

    // Allow access only from BrokerageAccount
    void sell(DecimalQuantity quantityToSell) {
        quantity = quantity.minus(quantityToSell);
    }

    // Allow access only from BrokerageAccount
    void addAllocation(Allocation allocation) {
        this.allocations.add(allocation);
        allocation.setLot(this);
    }

    // ----- Queries -----
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(DateTimeUtil.toStringTimestamp(creationTime));
        builder.append(" - Lot ").append(id);
        builder.append("] ");
        builder.append(quantity).append(" shares of ");
        builder.append(symbol).append(" @ ");
        builder.append(pricePaidPerShare);
        return builder.toString();
    }

    // ----- Attributes -----
    private DateTime creationTime;
    private String symbol;
    private DecimalQuantity quantity;
    private Money pricePaidPerShare;
    private BrokerageAccount account;
    private Set<Allocation> allocations = new HashSet<Allocation>();

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
    @Column(nullable = false, length=10)
    public String getSymbol() {
        return symbol;
    }
    private void setSymbol(String symbol) {
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
                    precision=Constants.PRICE_PRECISION,
                    scale=Constants.PRICE_SCALE)),
        @AttributeOverride(name="currency",
            column = @Column(
                    name="price_paid_currency",
                    length=Money.CURRENCY_LENGTH))
     })
    public Money getPricePaidPerShare() {
        return pricePaidPerShare;
    }
    private void setPricePaidPerShare(Money pricePaidPerShare) {
        this.pricePaidPerShare = pricePaidPerShare;
    }

    @ManyToOne
    public BrokerageAccount getAccount() {
        return account;
    }
    // Allow access only from BrokerageAccountFactory
    void setAccount(BrokerageAccount account) {
        this.account = account;
    }

    @OneToMany(mappedBy="lot",  cascade=CascadeType.ALL)
    @OptimisticLock(excluded = true)
    public Set<Allocation> getAllocations() {
        return allocations;
    }
    private void setAllocations(Set<Allocation> allocations) {
        this.allocations = allocations;
    }
    
    // ----- Comparators -----
    public static class CreationTimeComparator implements Comparator<Lot> {

        @Override
        public int compare(Lot lot1, Lot lot2) {

            if (lot1 == null) {
                return -1;
            }
            else if (lot2 == null) {
                return 1;
            }
            
            return lot1.getCreationTime().compareTo(lot2.getCreationTime());
        }
    }
}