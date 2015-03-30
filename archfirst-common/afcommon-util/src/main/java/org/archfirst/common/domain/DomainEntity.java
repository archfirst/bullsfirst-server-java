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
package org.archfirst.common.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * An abstract class to provide base functionality for all domain entities.
 * 
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainEntity")
@MappedSuperclass
public abstract class DomainEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // ----- Commands -----    
    
    // ----- Queries -----

    /**
     * Entities are uniquely identified by their id. Do not rely on object
     * references to identify them. This means that you must get an id
     * before putting an entity in to a hash set.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        if (this.id == null) {
            return false;
        }
        final DomainEntity that = (DomainEntity)other;
        return this.id.equals(that.getId());
    }

    /**
     * Entities are uniquely identified by their id. Do not rely on object
     * references to identify them. This means that you must get an id
     * before putting an entity in to a hash set.
     */
    @Override
    public int hashCode() {
        // Make sure we don't cause a NullPointerException
        return (this.id != null) ? this.id.hashCode() : super.hashCode();
    }

    // ----- Attributes -----
    @XmlElement(name = "Id")
    protected Long id;

    @XmlTransient
    protected int version;

    // ----- Getters and Setters -----
    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }
    private void setId(Long id) {
        this.id = id;
    }

    @Version
    public int getVersion() {
        return version;
    }
    private void setVersion(int version) {
        this.version = version;
    }
}