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
package org.archfirst.bfoms.domain.account;

import java.util.List;

import org.joda.time.LocalDate;

/**
 * Same as TransactionCriteria but ability to specify multiple accounts. For\
 * security reasons, always limit to a set of accounts viewable by the user.
 *
 * @author Naresh Bhatia
 */
public class TransactionCriteriaInternal {
    // ----- Constructors -----
    public TransactionCriteriaInternal(
            TransactionCriteria criteria, List<Long> accountIds) {
        this.accountIds = accountIds;
        this.fromDate = criteria.getFromDate();
        this.toDate = criteria.getToDate();
    }

    // ----- Attributes -----
    private List<Long> accountIds;
    private LocalDate fromDate;
    private LocalDate toDate;

    // ----- Getters and Setters -----
    public List<Long> getAccountIds() {
        return accountIds;
    }
    public LocalDate getFromDate() {
        return fromDate;
    }
    public LocalDate getToDate() {
        return toDate;
    }
}