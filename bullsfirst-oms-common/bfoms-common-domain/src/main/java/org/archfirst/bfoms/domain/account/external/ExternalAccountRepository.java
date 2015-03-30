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

import java.util.List;

import org.archfirst.bfoms.domain.security.User;
import org.archfirst.common.domain.BaseRepository;

/**
 * ExternalAccountRepository
 *
 * @author Naresh Bhatia
 */
public class ExternalAccountRepository extends BaseRepository {

    public ExternalAccount findAccount(Long id) {
        return entityManager.find(ExternalAccount.class, id);
    }

    public List<ExternalAccount> findAccountsWithPermission(
            User user,
            ExternalAccountPermission permission) {

        @SuppressWarnings("unchecked")
        List<ExternalAccount> accounts = entityManager.createQuery(
                "select ace.target from ExternalAccountAce ace " +
                "where ace.recipient = :recipient " +
                "and ace.permission = :permission")
            .setParameter("recipient", user)
            .setParameter("permission", permission)
            .getResultList();

        return accounts;
    }
}