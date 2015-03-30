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

import javax.persistence.Column;
import javax.persistence.Entity;

import org.archfirst.bfoms.domain.security.AccessControlEntry;
import org.archfirst.bfoms.domain.security.User;
import org.archfirst.bfoms.domain.util.Constants;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * ExternalAccountAce
 * 
 * @author Naresh Bhatia
 */
@Entity
public class ExternalAccountAce extends AccessControlEntry<ExternalAccount, ExternalAccountPermission> {
    private static final long serialVersionUID = 1L;

    private ExternalAccountAce() {
    }

    // Allow access only from AccountFactory
    ExternalAccountAce(
            User recipient, ExternalAccount target, ExternalAccountPermission permission) {
        this.recipient = recipient;
        this.target = target;
        this.permission = permission;
    }

    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.external.ExternalAccountPermission")
            }
    )
    @Column(length=Constants.ENUM_COLUMN_LENGTH)
    public ExternalAccountPermission getPermission() {
        return permission;
    }
}