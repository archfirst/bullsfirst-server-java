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
package org.archfirst.bfoms.domain.account.brokerage;

import javax.inject.Inject;

import org.archfirst.bfoms.domain.account.AccountParty;
import org.archfirst.bfoms.domain.account.AccountRole;
import org.archfirst.bfoms.domain.account.AccountStatus;
import org.archfirst.bfoms.domain.account.OwnershipType;
import org.archfirst.bfoms.domain.security.Person;
import org.archfirst.bfoms.domain.security.User;
import org.archfirst.common.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrokerageAccountFactory
 *
 * @author Naresh Bhatia
 */
public class BrokerageAccountFactory {
    private static final Logger logger =
        LoggerFactory.getLogger(BrokerageAccountFactory.class);

    @Inject private BrokerageAccountRepository brokerageAccountRepository;
    
    /**
     * Creates a new individual account for the specified person and
     * grants full access to the specified user.
     * 
     * @param accountName name of the account to create
     * @param primaryOwner person for whom account needs to be created
     * @param permissionRecipient
     * @return
     */
    public BrokerageAccount createIndividualAccountWithFullAccess(
            String accountName, Person primaryOwner, User permissionRecipient) {
        BrokerageAccount account = createIndividualAccount(accountName, primaryOwner);
        grantFullAccess(permissionRecipient, account);
        return account;
    }

    /**
     * Creates a new individual account for the specified person
     * 
     * @param accountName name of the account to create
     * @param primaryOwner person for whom account needs to be created
     */
    private BrokerageAccount createIndividualAccount(String accountName, Person primaryOwner) {
        
        // Create the account
        BrokerageAccount account = new BrokerageAccount(
                accountName,
                AccountStatus.Active,
                OwnershipType.Individual,
                new Money("0.00"));
        brokerageAccountRepository.injectDependencies(account);
        brokerageAccountRepository.persistAndFlush(account);
        
        // Add AccountParty
        AccountParty accountParty =
            new AccountParty(primaryOwner, AccountRole.PrimaryOwner);
        brokerageAccountRepository.persistAndFlush(accountParty);
        account.addAccountParty(accountParty);
        
        logger.debug("{} created", account.getName());
        return account;
    }

    /**
     * Grants full access to a user for the specified account.
     * 
     * @param recipient
     * @param account
     */
    private void grantFullAccess(User recipient, BrokerageAccount account) {
        brokerageAccountRepository.persist(
            new BrokerageAccountAce(recipient, account, BrokerageAccountPermission.View)); 
        brokerageAccountRepository.persist(
            new BrokerageAccountAce(recipient, account, BrokerageAccountPermission.Edit)); 
        brokerageAccountRepository.persist(
            new BrokerageAccountAce(recipient, account, BrokerageAccountPermission.Trade)); 
        brokerageAccountRepository.persist(
            new BrokerageAccountAce(recipient, account, BrokerageAccountPermission.Transfer)); 
    }
}