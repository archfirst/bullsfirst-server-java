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
package org.archfirst.bfoms.domain.security;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.archfirst.common.domain.DomainEntity;

/**
 * AccessControlEntry
 * 
 * @param <T> The target of the permission (extends DomainEntity)
 * @param <P> Enumeration representing the permissions 
 * 
 * @author Naresh Bhatia
 */
@MappedSuperclass
public class AccessControlEntry<T extends DomainEntity, P extends Enum<P>>
    extends DomainEntity {
    
    private static final long serialVersionUID = 1L;
    
    // ----- Attributes -----
    protected User recipient;
    protected T target;
    protected P permission;
    
    // ----- Getters and Setters -----
    @ManyToOne
    public User getRecipient() {
        return recipient;
    }
    private void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    @ManyToOne
    public T getTarget() {
        return target;
    }
    private void setTarget(T target) {
        this.target = target;
    }

    @Transient
    public P getPermission() {
        return permission;
    }
    private void setPermission(P permission) {
        this.permission = permission;
    }
}