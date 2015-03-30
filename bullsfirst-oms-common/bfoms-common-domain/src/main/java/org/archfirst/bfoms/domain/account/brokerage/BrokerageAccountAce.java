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

import javax.persistence.Column;
import javax.persistence.Entity;

import org.archfirst.bfoms.domain.security.AccessControlEntry;
import org.archfirst.bfoms.domain.security.User;
import org.archfirst.bfoms.domain.util.Constants;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * BrokerageAccountAce
 * 
 * @author Naresh Bhatia
 */
@Entity
public class BrokerageAccountAce extends AccessControlEntry<BrokerageAccount, BrokerageAccountPermission> {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    private BrokerageAccountAce() {
    }

    // Allow access only from BrokerageAccountFactory
    BrokerageAccountAce(
            User recipient, BrokerageAccount target, BrokerageAccountPermission permission) {
        this.recipient = recipient;
        this.target = target;
        this.permission = permission;
    }

    // ----- Getters and Setters -----
    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountPermission")
            }
    )
    @Column(length=Constants.ENUM_COLUMN_LENGTH)
    public BrokerageAccountPermission getPermission() {
        return permission;
    }
}