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
package org.archfirst.bfoms.domain.account.external;

import javax.inject.Inject;

import org.archfirst.bfoms.domain.account.AccountStatus;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountFactory;
import org.archfirst.bfoms.domain.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExternalAccountFactory
 *
 * @author Naresh Bhatia
 */
public class ExternalAccountFactory {
    private static final Logger logger =
        LoggerFactory.getLogger(BrokerageAccountFactory.class);

    @Inject
    private ExternalAccountRepository externalAccountRepository;
    
    /**
     * Creates a new external account with full access to the specified user.
     * 
     * @param account
     * @param permissionRecipient
     */
    public ExternalAccount createExternalAccountWithFullAccess(
            ExternalAccountParams params, User permissionRecipient) {
        ExternalAccount account = new ExternalAccount(
                params.getName(),
                AccountStatus.Active,
                params.getRoutingNumber(),
                params.getAccountNumber());
        externalAccountRepository.persist(account);
        externalAccountRepository.persist(new ExternalAccountAce(
                permissionRecipient, account, ExternalAccountPermission.FullAccess)); 
        logger.debug("{} created", account.getName());
        return account;
    }
}