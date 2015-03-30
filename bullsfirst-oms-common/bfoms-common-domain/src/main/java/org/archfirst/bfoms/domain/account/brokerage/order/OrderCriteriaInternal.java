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
package org.archfirst.bfoms.domain.account.brokerage.order;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

/**
 * Same as OrderCriteria but ability to specify multiple accounts. For security
 * reasons, always limit to a set of accounts viewable by the user.
 *
 * @author Naresh Bhatia
 */
public class OrderCriteriaInternal {
    // ----- Constructors -----
    public OrderCriteriaInternal(OrderCriteria criteria, List<Long> accountIds) {
        this.accountIds = accountIds;
        this.symbol = criteria.getSymbol();
        this.orderId = criteria.getOrderId();
        this.fromDate = criteria.getFromDate();
        this.toDate = criteria.getToDate();
        this.sides = criteria.getSides();
        this.statuses = criteria.getStatuses();
    }

    // ----- Attributes -----
    private List<Long> accountIds;
    private String symbol;
    private Long orderId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<OrderSide> sides = new ArrayList<OrderSide>();
    private List<OrderStatus> statuses = new ArrayList<OrderStatus>();

    // ----- Getters and Setters -----
    public List<Long> getAccountIds() {
        return accountIds;
    }
    public String getSymbol() {
        return symbol;
    }
    public Long getOrderId() {
        return orderId;
    }
    public LocalDate getFromDate() {
        return fromDate;
    }
    public LocalDate getToDate() {
        return toDate;
    }
    public List<OrderSide> getSides() {
        return sides;
    }
    public List<OrderStatus> getStatuses() {
        return statuses;
    }
}