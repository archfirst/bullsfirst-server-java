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
package org.archfirst.bfoms.domain.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.common.datetime.DateTimeAdapter;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;

/**
 * TransactionSummary
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSummary")
public class TransactionSummary {

    // ----- Constructors -----
    public TransactionSummary() {
    }
    
    public TransactionSummary(Transaction transaction) {
        this.accountId = transaction.getAccount().getId();
        this.transactionId = transaction.getId();
        this.creationTime = transaction.getCreationTime();
        this.type = transaction.getType();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
    }

    // ----- Attributes -----
    @XmlElement(name = "AccountId", required = true)
    private Long accountId;

    @XmlElement(name = "TransactionId", required = true)
    private Long transactionId;

    @XmlElement(name = "CreationTime", required = true)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name="dateTime")
    private DateTime creationTime;
    
    @XmlElement(name = "Type", required = true)
    private String type;

    @XmlElement(name = "Description", required = true)
    private String description;
    
    @XmlElement(name = "Amount", required = true)
    private Money amount;

    // ----- Getters and Setters -----
    public Long getAccountId() {
        return accountId;
    }
    public Long getTransactionId() {
        return transactionId;
    }
    public DateTime getCreationTime() {
        return creationTime;
    }
    public String getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }
    public Money getAmount() {
        return amount;
    }
}