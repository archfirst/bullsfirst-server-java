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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Person
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Person")
@Entity
public class Person extends Party {
    private static final long serialVersionUID = 1L; 
    
    // ----- Constructors -----
    private Person() {
    }
    
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ----- Queries -----
    @Transient
    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Person)) {
            return false;
        }
        final Person that = (Person)object;
        return this.firstName.equals(that.getFirstName())
                && this.lastName.equals(that.getLastName());
    }

    @Override
    public int hashCode() {
        return firstName.hashCode() + lastName.hashCode();
    }

    @Override
    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("id=").append(getId());
        strBuf.append(", firstName=").append(firstName);
        strBuf.append(", lastName=").append(lastName);
        return strBuf.toString();
    }

    // ----- Attributes -----
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 40;
    
    @XmlElement(name = "FirstName")
    private String firstName;

    @XmlElement(name = "LastName")
    private String lastName;

    // ----- Getters and Setters -----
    @NotNull
    @Size(min = MIN_LENGTH, max = MAX_LENGTH)
    @Pattern(regexp="[a-zA-Z]+", message="First name must only contain letters")
    @Column(nullable=false, length=MAX_LENGTH)
    public String getFirstName() {
        return firstName;
    }
    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull
    @Size(min = MIN_LENGTH, max = MAX_LENGTH)
    @Pattern(regexp="[a-zA-Z]+", message="Last name must only contain letters")
    @Column(nullable=false, length=MAX_LENGTH)
    public String getLastName() {
        return lastName;
    }
    private void setLastName(String lastName) {
        this.lastName = lastName;
    }
}