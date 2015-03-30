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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.archfirst.bfoms.domain.marketdata.MarketDataService;
import org.archfirst.bfoms.domain.referencedata.ReferenceDataService;
import org.archfirst.bfoms.domain.security.AuthorizationException;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;

/**
 * BaseAccountService
 *
 * @author Naresh Bhatia
 */
public class BaseAccountService {
    
    // ----- Commands -----
    public void changeAccountName(Long accountId, String newName) {
        this.findAccount(accountId).changeName(newName);
    }
    
    /**
     * Transfer cash from the specified account to the specified account.
     * Amount should always be positive.
     */
    public void transferCash(
            String username,
            Money amount,
            Long fromAccountId,
            Long toAccountId) {

        this.transferCash(
                username,
                amount,
                this.findAccount(fromAccountId),
                this.findAccount(toAccountId));
    }
    
    private void transferCash(
            String username,
            Money amount,
            BaseAccount fromAccount,
            BaseAccount toAccount) {
        
        // Check authorization on from account
        // TODO: This needs to be done
        
        // Check if cash is available
        if (!fromAccount.isCashAvailable(amount, marketDataService)) {
            throw new InsufficientFundsException();
        }

        // Transfer the cash
        DateTime now = new DateTime();
        CashTransfer fromTransfer = new CashTransfer(now, amount.negate(), toAccount);
        CashTransfer toTransfer = new CashTransfer(now, amount, fromAccount);
        baseAccountRepository.persist(fromTransfer);
        baseAccountRepository.persist(toTransfer);
        baseAccountRepository.flush(); // get object ids before adding to set
        fromAccount.transferCash(fromTransfer);
        toAccount.transferCash(toTransfer);
    }

    /**
     * Transfer securities from the specified account to the specified account.
     * Quantity should always be positive.
     */
    public void transferSecurities(
            String username,
            String symbol,
            DecimalQuantity quantity,
            Money pricePaidPerShare,
            Long fromAccountId,
            Long toAccountId) {
        
        this.transferSecurities(
                username,
                symbol,
                quantity,
                pricePaidPerShare,
                this.findAccount(fromAccountId),
                this.findAccount(toAccountId));
    }
        
    private void transferSecurities(
            String username,
            String symbol,
            DecimalQuantity quantity,
            Money pricePaidPerShare,
            BaseAccount fromAccount,
            BaseAccount toAccount) {
        
        // Check authorization on from account
        // TODO: This needs to be done
        
        // Check if the symbol is valid
        if (this.referenceDataService.lookup(symbol) == null) {
            throw new InvalidSymbolException();
        }
        
        // Check if security is available
        if (!fromAccount.isSecurityAvailable(symbol, quantity)) {
            throw new InsufficientQuantityException();
        }

        // Transfer the security
        DateTime now = new DateTime();
        SecuritiesTransfer fromTransfer = new SecuritiesTransfer(
                now, symbol, quantity.negate(), pricePaidPerShare, toAccount);
        SecuritiesTransfer toTransfer = new SecuritiesTransfer(
                now, symbol, quantity, pricePaidPerShare, fromAccount);
        baseAccountRepository.persist(fromTransfer);
        baseAccountRepository.persist(toTransfer);
        baseAccountRepository.flush(); // get object ids before adding to set
        fromAccount.transferSecurities(fromTransfer);
        toAccount.transferSecurities(toTransfer);
    }

    // ----- Queries -----
    public BaseAccount findAccount(Long id) {
        return baseAccountRepository.findAccount(id);
    }
    
    public List<Transaction> findTransactions(TransactionCriteria criteria) {
        return baseAccountRepository.findTransactions(
                toTransactionCriteriaInternal(criteria));
    }

    /**
     * This version returns transactions for a single account, and does not
     * implement security checks (difficult to set up security checks for
     * mixed brokerage and external accounts). For a better implementation
     * look at BrokerageAccountService.getTransactionSummaries().
     */
    public List<TransactionSummary> getTransactionSummaries(
            String username, TransactionCriteria criteria) {
        
        // Check authorization on account
        // TODO: This needs to be done
        // Currently we are only checking if accountId is specified
        if (criteria.getAccountId() == null)
            throw new AuthorizationException();
        
        List<Transaction> transactions = baseAccountRepository.findTransactions(
                toTransactionCriteriaInternal(criteria));
        
        List<TransactionSummary> summaries =
            new ArrayList<TransactionSummary>();
        for (Transaction transaction : transactions) {
            summaries.add(new TransactionSummary(transaction));
        }
        
        return summaries;
    }
    
    private TransactionCriteriaInternal toTransactionCriteriaInternal(
            TransactionCriteria criteria) {
        List<Long> accountIds = new ArrayList<Long>();
        if (criteria.getAccountId() != null) {
            accountIds.add(criteria.getAccountId());
        }
        return new TransactionCriteriaInternal(criteria, accountIds);
    }

    // ----- Attributes -----
    @Inject private BaseAccountRepository baseAccountRepository;
    @Inject private ReferenceDataService referenceDataService;
    @Inject private MarketDataService marketDataService;
}