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
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.datetime.DateTimeAdapter;
import org.archfirst.common.datetime.DateTimeUtil;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.quantity.Percentage;
import org.joda.time.DateTime;

/**
 * Position
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Position")
public class Position {

    // ----- Commands -----
    public void setInstrumentPosition(
            Long accountId,
            String accountName,
            String instrumentSymbol,
            String instrumentName,
            Money lastTrade) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.instrumentSymbol = instrumentSymbol;
        this.instrumentName = instrumentName;
        this.lastTrade = lastTrade;
    }
    
    public void setLotPosition(
            Long accountId,
            String accountName,
            String instrumentSymbol,
            String instrumentName,
            Long lotId,
            DateTime lotCreationTime,
            DecimalQuantity quantity,
            Money lastTrade,
            Money pricePaid) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.instrumentSymbol = instrumentSymbol;
        this.instrumentName = instrumentName;
        this.lotId = lotId;
        this.lotCreationTime = lotCreationTime;
        this.quantity = quantity;
        this.lastTrade = lastTrade;
        this.pricePaid = pricePaid;
        
        // Calculate values
        this.marketValue = lastTrade.times(quantity).scaleToCurrency();
        this.totalCost = pricePaid.times(quantity).scaleToCurrency();
        this.gain = marketValue.minus(totalCost);
        this.gainPercent = gain.getPercentage(totalCost, Constants.GAIN_SCALE);
    }
    
    public void setCashPosition(
            Long accountId,
            String accountName,
            Money marketValue) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.instrumentSymbol = Constants.CASH_INSTRUMENT_SYMBOL;
        this.instrumentName = Constants.CASH_INSTRUMENT_NAME;
        this.marketValue = marketValue;
    }
    
    public void calculateInstrumentPosition() {
        quantity = new DecimalQuantity();
        totalCost = new Money("0.00");
        
        for (Position child : children) {
            quantity = quantity.plus(child.getQuantity());
            totalCost = totalCost.plus(child.getTotalCost());
        }
        
        marketValue = lastTrade.times(quantity).scaleToCurrency();
        pricePaid = totalCost.div(quantity, Constants.PRICE_SCALE);

        gain = marketValue.minus(totalCost);
        gainPercent = gain.getPercentage(totalCost, Constants.GAIN_SCALE);
    }

    public void addChild(Position position) {
        if (this.children == null) {
            this.children = new ArrayList<Position>();
        }
        this.children.add(position);
    }
    
    // ----- Queries -----
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("accountId=").append(accountId);
        builder.append("accountName=").append(accountName);
        builder.append(", instrumentSymbol=").append(instrumentSymbol);
        builder.append(", instrumentName=").append(instrumentName);
        builder.append(", lotId=").append(lotId);
        if (lotCreationTime != null) {
            builder.append(", lotCreationTime=").append(DateTimeUtil.toStringTimestamp(lotCreationTime));
        }
        builder.append(", quantity=").append(quantity);
        builder.append(", lastTrade=").append(lastTrade);
        builder.append(", marketValue=").append(marketValue);
        builder.append(", pricePaid=").append(pricePaid);
        builder.append(", totalCost=").append(totalCost);
        builder.append(", gain=").append(gain);
        builder.append(", gainPercent=").append(gainPercent);
        return builder.toString();
    }
    
    public String toStringDeep() {
        return toStringDeep(0);
    }

    private String toStringDeep(int indent) {
        // Print self
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<indent; i++) {
            builder.append(" ");
        }
        builder.append(this);

        // Print children
        if (children != null) {
            for (Position child : children) {
                builder.append("\n").append(child.toStringDeep(indent+2));
            }
        }

        return builder.toString();
    }

    // ----- Object Method Overrides -----
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(accountId)
            .append(accountName)
            .append(instrumentSymbol)
            .append(instrumentName)
            .append(lotId)
            // .append(lotCreationTime)
            .append(quantity)
            .append(lastTrade)
            .append(marketValue)
            .append(pricePaid)
            .append(totalCost)
            .append(gain)
            // .append(gainPercent)
            .append(children)
            .hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (other == this) { return true; }
        if (other.getClass() != getClass()) {
            return false;
        }
        final Position that = (Position)other;
        return new EqualsBuilder()
            .append(accountId, that.accountId)
            .append(accountName, that.accountName)
            .append(instrumentSymbol, that.instrumentSymbol)
            .append(instrumentName, that.instrumentName)
            .append(lotId, that.lotId)
            // Causes issues with serialization/deserialization
            // .append(lotCreationTime, that.lotCreationTime)
            .append(quantity, that.quantity)
            .append(lastTrade, that.lastTrade)
            .append(marketValue, that.marketValue)
            .append(pricePaid, that.pricePaid)
            .append(totalCost, that.totalCost)
            .append(gain, that.gain)
            // Causes issues with serialization/deserialization
            // .append(gainPercent, that.gainPercent)
            .append(children, that.children)
            .isEquals();
    }
    
    // ----- Attributes -----
    @XmlElement(name = "AccountId", required = true)
    private Long accountId;

    @XmlElement(name = "AccountName", required = true)
    private String accountName;

    @XmlElement(name = "InstrumentSymbol", required = true)
    private String instrumentSymbol;

    @XmlElement(name = "InstrumentName", required = true)
    private String instrumentName;

    @XmlElement(name = "LotId", required = true)
    private Long lotId;

    @XmlElement(name = "LotCreationTime", required = true)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name="dateTime")
    private DateTime lotCreationTime;

    @XmlElement(name = "Quantity", required = true)
    private DecimalQuantity quantity;

    @XmlElement(name = "LastTrade", required = true)
    private Money lastTrade;

    @XmlElement(name = "MarketValue", required = true)
    private Money marketValue;

    @XmlElement(name = "PricePaid", required = true)
    private Money pricePaid;

    @XmlElement(name = "TotalCost", required = true)
    private Money totalCost;

    @XmlElement(name = "Gain", required = true)
    private Money gain;

    @XmlElement(name = "GainPercent", required = true)
    private Percentage gainPercent;

    @XmlElement(name = "Child", required = true)
    private List<Position> children;

    // ----- Getters -----
    public Long getAccountId() {
        return accountId;
    }
    public String getAccountName() {
        return accountName;
    }
    public String getInstrumentSymbol() {
        return instrumentSymbol;
    }
    public String getInstrumentName() {
        return instrumentName;
    }
    public Long getLotId() {
        return lotId;
    }
    public DateTime getLotCreationTime() {
        return lotCreationTime;
    }
    public DecimalQuantity getQuantity() {
        return quantity;
    }
    public Money getLastTrade() {
        return lastTrade;
    }
    public Money getMarketValue() {
        return marketValue;
    }
    public Money getPricePaid() {
        return pricePaid;
    }
    public Money getTotalCost() {
        return totalCost;
    }
    public Money getGain() {
        return gain;
    }
    public Percentage getGainPercent() {
        return gainPercent;
    }
    public List<Position> getChildren() {
        return children;
    }
    
    // ----- Private setters to keep Jackson happy -----
    private void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    private void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    private void setInstrumentSymbol(String instrumentSymbol) {
        this.instrumentSymbol = instrumentSymbol;
    }
    private void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
    private void setLotId(Long lotId) {
        this.lotId = lotId;
    }
    private void setLotCreationTime(DateTime lotCreationTime) {
        this.lotCreationTime = lotCreationTime;
    }
    private void setQuantity(DecimalQuantity quantity) {
        this.quantity = quantity;
    }
    private void setLastTrade(Money lastTrade) {
        this.lastTrade = lastTrade;
    }
    private void setMarketValue(Money marketValue) {
        this.marketValue = marketValue;
    }
    private void setPricePaid(Money pricePaid) {
        this.pricePaid = pricePaid;
    }
    private void setTotalCost(Money totalCost) {
        this.totalCost = totalCost;
    }
    private void setGain(Money gain) {
        this.gain = gain;
    }
    private void setGainPercent(Percentage gainPercent) {
        this.gainPercent = gainPercent;
    }
    private void setChildren(List<Position> children) {
        this.children = children;
    }

    // ----- Comparators -----
    public static class LotCreationTimeComparator implements Comparator<Position> {

        @Override
        public int compare(Position position1, Position position2) {

            if (position1 == null) {
                return -1;
            }
            else if (position2 == null) {
                return 1;
            }
            else if (position1.getLotCreationTime() == null) {
                return -1;
            }
            else if (position2.getLotCreationTime() == null) {
                return 1;
            }
            
            return position1.getLotCreationTime().compareTo(position2.getLotCreationTime());
        }
    }
}