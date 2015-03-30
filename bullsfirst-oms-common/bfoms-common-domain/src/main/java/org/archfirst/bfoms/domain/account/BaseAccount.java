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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.archfirst.bfoms.domain.marketdata.MarketDataService;
import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.domain.DomainEntity;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * BaseAccount
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseAccount")
@Entity
@Inheritance(strategy=JOINED)
public abstract class BaseAccount extends DomainEntity {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    protected BaseAccount() {
    }

    protected BaseAccount(String name, AccountStatus status) {
        this.name = name;
        this.status = status;
    }

    // ----- Commands -----
    public void changeName(String newName) {
        this.name = newName;
    }
    
    /**
     * Transfer cash from this account. Direction of transfer is determined
     * by the sign of transfer.amount.
     */
    public abstract void transferCash(CashTransfer transfer);

    /**
     * Transfer securities from this account. Direction of transfer is determined
     * by the sign of transfer.quantity.
     */
    public abstract void transferSecurities(SecuritiesTransfer transfer);

    // Needed by BrokerageAccountFactory
    public void addAccountParty(AccountParty accountParty) {
        accountParties.add(accountParty);
        accountParty.setAccount(this);
    }

    // Needed by BrokerageAccount and ExternalAccount
    protected void addTransaction(Transaction transaction) {
        transaction.setAccount(this);
        transactions.add(transaction);
    }

    // ----- Queries -----
    public abstract boolean isCashAvailable(
            Money amount,
            MarketDataService marketDataService);

    public abstract boolean isSecurityAvailable(
            String symbol,
            DecimalQuantity quantity);

    // ----- Attributes -----
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    protected String name;
    protected AccountStatus status = AccountStatus.Active;
    protected Set<AccountParty> accountParties = new HashSet<AccountParty>();
    protected Set<Transaction> transactions = new HashSet<Transaction>();

    // ----- Getters and Setters -----
    @NotNull
    @Size(min = MIN_LENGTH, max = MAX_LENGTH)
    @Column(nullable = false, length=50)
    public String getName() {
        return name;
    }
    private void setName(String name) {
        this.name = name;
    }

    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.AccountStatus")
            }
    )
    @Column(length=Constants.ENUM_COLUMN_LENGTH)
    public AccountStatus getStatus() {
        return status;
    }
    private void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    // CollectionOfElements does not cascade - this is a hibernate bug
    // (see http://opensource.atlassian.com/projects/hibernate/browse/ANN-755)
    // So we have to treat AccountParties as entities instead of value objects
    // @CollectionOfElements(targetElement = AccountParty.class)
    // @JoinTable(name = "Account_AccountParties",
    //    joinColumns = @JoinColumn(name = "account_id"))
    @OneToMany(mappedBy="account",  cascade=CascadeType.ALL)
    @OptimisticLock(excluded = true)
    public Set<AccountParty> getAccountParties() {
        return accountParties;
    }
    private void setAccountParties(Set<AccountParty> accountParties) {
        this.accountParties = accountParties;
    }

    @OneToMany(mappedBy="account",  cascade=CascadeType.ALL)
    @OptimisticLock(excluded = true)
    public Set<Transaction> getTransactions() {
        return transactions;
    }
    private void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
}