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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.archfirst.bfoms.domain.security.Party;
import org.archfirst.bfoms.domain.util.Constants;
import org.archfirst.common.domain.DomainEntity;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * AccountParty
 *
 * @author Naresh Bhatia
 */
//Treat AccountParty as an entity instead of value object due to a Hibernate
//bug (see description at Account.getAccountParties())
//@Embeddable
@Entity
public class AccountParty extends DomainEntity {
    private static final long serialVersionUID = 1L; 

    // ----- Constructors -----
    private AccountParty() {
    }

    public AccountParty(Party party, AccountRole role) {
        this.party = party;
        this.role = role;
    }

    // ----- Commands -----

    // ----- Queries -----
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AccountParty)) {
            return false;
        }
        final AccountParty that = (AccountParty)object;
        return this.party.equals(that.getParty())
                && this.role.equals(that.getRole());
    }

    @Override
    public int hashCode() {
        return party.hashCode() + role.hashCode();
    }

    // ----- Attributes -----
    private BaseAccount account;
    private Party party;
    private AccountRole role;

    // ----- Getters and Setters -----
    @ManyToOne
    public BaseAccount getAccount() {
        return account;
    }
    // Allow access from account
    void setAccount(BaseAccount account) {
        this.account = account;
    }

    @ManyToOne
    public Party getParty() {
        return party;
    }
    private void setParty(Party party) {
        this.party = party;
    }

    @Type(
        type = "org.archfirst.common.hibernate.GenericEnumUserType",
        parameters = {
            @Parameter (
                name  = "enumClass",
                value = "org.archfirst.bfoms.domain.account.AccountRole")
            }
    )
    @Column(length=Constants.ENUM_COLUMN_LENGTH)
    public AccountRole getRole() {
        return role;
    }
    private void setRole(AccountRole role) {
        this.role = role;
    }
}