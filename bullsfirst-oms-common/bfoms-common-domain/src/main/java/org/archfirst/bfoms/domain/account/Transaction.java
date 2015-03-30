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

import static javax.persistence.InheritanceType.JOINED;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.archfirst.common.datetime.DateTimeAdapter;
import org.archfirst.common.domain.DomainEntity;
import org.archfirst.common.money.Money;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Transaction
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Transaction")
@Entity
@Inheritance(strategy=JOINED)
public abstract class Transaction extends DomainEntity {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    protected Transaction() {
    }

    public Transaction(DateTime creationTime) {
        this.creationTime = creationTime;
    }
    
    // ----- Commands -----
    
    // ----- Queries -----
    /**
     * @return the transaction type that can be shown to the user
     */
    @Transient
    public abstract String getType();

    /**
     * @return a description that can be shown to the user
     */
    @Transient
    public abstract String getDescription();
    
    /**
     * @return the transaction amount (positive if money is flowing into the
     *         account, negative is money is flowing out of the account.
     */
    @Transient
    public abstract Money getAmount();

    // ----- Attributes -----
    @XmlElement(name = "CreationTime", required = true)
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlSchemaType(name="dateTime")
    private DateTime creationTime;

    @XmlTransient
    private BaseAccount account;
    
    // ----- Getters and Setters -----
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    @Column(nullable = false)
    public DateTime getCreationTime() {
        return creationTime;
    }
    private void setCreationTime(DateTime creationTime) {
        this.creationTime = creationTime;
    }
    
    @ManyToOne
    public BaseAccount getAccount() {
        return account;
    }
    // Needed by BaseAccount
    public void setAccount(BaseAccount account) {
        this.account = account;
    }
}