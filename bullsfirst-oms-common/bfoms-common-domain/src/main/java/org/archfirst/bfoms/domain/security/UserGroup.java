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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.archfirst.common.domain.DomainEntity;

/**
 * Associates user with a group. We are using username as the primary key
 * just because GlassFish requires it.
 *
 * @author Naresh Bhatia
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class UserGroup extends DomainEntity {
    private static final long serialVersionUID = 1L; 

    // ----- Constructors -----
    private UserGroup() {
    }

    public UserGroup(String username, String groupname) {
        this.username = username;
        this.groupname = groupname;
    }

    // ----- Attributes -----
    private String username;
    private String groupname;

    // ----- Getters and Setters -----
    @NotNull
    @Column(nullable = false, length=50)
    public String getUsername() {
        return username;
    }
    private void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    @Column(nullable = false, length=30)
    private String getGroupname() {
        return groupname;
    }
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }
}