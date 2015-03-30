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
package org.archfirst.bfoms.domain.account.brokerage.order;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.archfirst.common.money.Money;

/**
 * Contains order estimate and compliance information
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderEstimate")
public class OrderEstimate {

    // ----- Constructors -----
    public OrderEstimate() {
    }
    
    public OrderEstimate(OrderCompliance compliance) {
        this.compliance = compliance;
    }
    
    public OrderEstimate(
            Money estimatedValue,
            Money fees,
            Money estimatedValueInclFees) {
        this.estimatedValue = estimatedValue;
        this.fees = fees;
        this.estimatedValueInclFees = estimatedValueInclFees;
    }

    // ----- Attributes -----
    @XmlElement(name = "Compliance", required = true)
    private OrderCompliance compliance;
    @XmlElement(name = "EstimatedValue", required = true)
    private Money estimatedValue;
    @XmlElement(name = "Fees", required = true)
    private Money fees;
    @XmlElement(name = "EstimatedValueInclFees", required = true)
    private Money estimatedValueInclFees;

    // ----- Getters and Setters -----
    public OrderCompliance getCompliance() {
        return compliance;
    }
    // Allows Account to set this value
    public void setCompliance(OrderCompliance compliance) {
        this.compliance = compliance;
    }

    public Money getEstimatedValue() {
        return estimatedValue;
    }

    public Money getFees() {
        return fees;
    }

    public Money getEstimatedValueInclFees() {
        return estimatedValueInclFees;
    }
}