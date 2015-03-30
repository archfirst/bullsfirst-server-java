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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ExternalAccountParams
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalAccountParams")
@XmlRootElement(name = "ExternalAccountParams")
public class ExternalAccountParams {

    // ----- Constructors -----
    public ExternalAccountParams() {
    }

    public ExternalAccountParams(
            String name,
            String routingNumber,
            String accountNumber) {
        this.name = name;
        this.routingNumber = routingNumber;
        this.accountNumber = accountNumber;
    }

    // ----- Attributes -----
    @XmlElement(name = "Name", required = true)
    private String name;

    @XmlElement(name = "RoutingNumber", required = true)
    private String routingNumber;

    @XmlElement(name = "AccountNumber", required = true)
    private String accountNumber;

    // ----- Getters -----
    public String getName() {
        return name;
    }
    public String getRoutingNumber() {
        return routingNumber;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
}