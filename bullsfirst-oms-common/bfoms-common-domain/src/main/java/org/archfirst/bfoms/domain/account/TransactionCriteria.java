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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.common.datetime.LocalDateAdapter;
import org.joda.time.LocalDate;

/**
 * TransactionCriteria
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionCriteria")
public class TransactionCriteria {

    // ----- Queries -----
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAccountId: ").append(accountId);
        sb.append("\nFromDate: ").append(fromDate);
        sb.append("\nToDate: ").append(toDate);
        return sb.toString();
    }

    // ----- Attributes -----
    @XmlElement(name = "AccountId")
    private Long accountId;

    @XmlElement(name = "FromDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlSchemaType(name="date")
    private LocalDate fromDate;

    @XmlElement(name = "ToDate")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @XmlSchemaType(name="date")
    private LocalDate toDate;

    // ----- Getters and Setters -----
    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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
}