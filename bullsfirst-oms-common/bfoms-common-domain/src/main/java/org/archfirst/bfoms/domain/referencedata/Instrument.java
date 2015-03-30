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
package org.archfirst.bfoms.domain.referencedata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Instrument
 *
 * @author Naresh Bhatia
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Instrument")
public class Instrument implements Comparable <Instrument> {
    private static final long serialVersionUID = 1L;

    // ----- Constructors -----
    public Instrument() {
    }
    
    public Instrument(String symbol, String name, String exchange) {
        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
    }

    // ----- Queries -----
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Instrument)) {
            return false;
        }
        final Instrument that = (Instrument)object;
        return this.symbol.equals(that.getSymbol());
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public int compareTo(Instrument other) {
        return this.symbol.compareTo(other.getSymbol());
    }
    
    @Override
    public String toString() {
        return symbol;
    }

    // ----- Attributes -----
    @XmlElement(name = "Symbol", required = true)
    private String symbol;

    @XmlElement(name = "Name", required = true)
    private String name;

    @XmlElement(name = "Exchange", required = true)
    private String exchange;

    // ----- Getters and Setters -----
    public String getSymbol() {
        return symbol;
    }
    private void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }
    private void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }
    private void setExchange(String exchange) {
        this.exchange = exchange;
    }
}