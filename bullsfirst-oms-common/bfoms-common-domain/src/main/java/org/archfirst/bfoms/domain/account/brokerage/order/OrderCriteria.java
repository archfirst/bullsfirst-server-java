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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.common.datetime.LocalDateAdapter;
import org.joda.time.LocalDate;

/**
 * OrderCriteria
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderCriteria")
public class OrderCriteria {

    // ----- Commands -----

    // ----- Queries -----
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAccountId: ").append(accountId);
        sb.append("\nOrderId: ").append(orderId);
        sb.append("\nSymbol: ").append(symbol);
        sb.append("\nFromDate: ").append(fromDate);
        sb.append("\nToDate: ").append(toDate);

        // Sides
        sb.append("\nSides: ");
        for (OrderSide side : sides)
        {
            sb.append(side).append(" ");
        }

        // Statuses
        sb.append("\nStatuses: ");
        for (OrderStatus status : statuses)
        {
            sb.append(status).append(" ");
        }

        return sb.toString();
    }

    // ----- Attributes -----
    @XmlElement(name = "AccountId")
    private Long accountId;

    @XmlElement(name = "Symbol")
    private String symbol;

    @XmlElement(name = "OrderId")
    private Long orderId;

    @XmlElement(name = "FromDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlSchemaType(name="date")
    private LocalDate fromDate;

    @XmlElement(name = "ToDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlSchemaType(name="date")
    private LocalDate toDate;

    // selectManyCheckbox does not work with a Set, had to use List
    @XmlElement(name = "Side")
    private List<OrderSide> sides = new ArrayList<OrderSide>();

    @XmlElement(name = "Status")
    private List<OrderStatus> statuses = new ArrayList<OrderStatus>();

    // ----- Getters and Setters -----
    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public List<OrderSide> getSides() {
        return sides;
    }
    public void setSides(List<OrderSide> sides) {
        this.sides = sides;
    }

    public List<OrderStatus> getStatuses() {
        return statuses;
    }
    public void setStatuses(List<OrderStatus> statuses) {
        this.statuses = statuses;
    }
}