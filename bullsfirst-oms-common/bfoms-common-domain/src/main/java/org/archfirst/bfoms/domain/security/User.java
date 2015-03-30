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
package org.archfirst.bfoms.domain.security;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.archfirst.common.domain.DomainEntity;

/**
 * Represents an application user. 
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "User")
@Entity
@Table(name="Users",  uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User extends DomainEntity {
    private static final long serialVersionUID = 1L; 

    // ----- Constructors -----
    private User() {
    }
    
    public User(String username, String clearPassword, Person person) {

        this.username = username;

        // Removed password salting because GlassFish does not support it
        // See http://flexiblejdbcrealm.wamblee.org for a possible solution
        this.passwordHash = PasswordHashGenerator.generateHash(clearPassword);

        this.person = person;
    }

    // ----- Queries -----
    @Transient
    public boolean isPasswordValid(String clearPassword) {
        return this.passwordHash.equals(
                PasswordHashGenerator.generateHash(clearPassword));
    }
    
    // ----- Attributes -----
    @XmlElement(name = "Username", required = true)
    private String username;

    @XmlElement(name = "PasswordHash", required = true)
    private String passwordHash;

    @XmlElement(name = "Person", required = true)
    private Person person;

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
    @Column(nullable = false, length=50)
    public String getPasswordHash() {
        return passwordHash;
    }
    private void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "person_id", nullable=false)
    public Person getPerson() {
        return person;
    }
    private void setPerson(Person person) {
        this.person = person;
    }
}