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
package org.archfirst.bfexch.domain.trading.order;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * OrderTerm
 *
 * @author Naresh Bhatia
 */
@XmlType(name = "OrderTerm")
@XmlEnum
public enum OrderTerm {
    @XmlEnumValue("GoodForTheDay")
    GoodForTheDay("GFD", "Good For The Day"),
    @XmlEnumValue("GoodTilCanceled")
    GoodTilCanceled("GTC", "Good 'til Canceled");
    
    private final String identifier;
    private final String displayString;
    
    private static Map<String, OrderTerm> identifiers =
        new HashMap<String, OrderTerm>();
    
    static {
        for (OrderTerm type : EnumSet.allOf(OrderTerm.class)) {
            identifiers.put(type.toIdentifier(), type);
        }
    }
    
    private OrderTerm(String identifier, String displayString) {
        this.identifier = identifier;
        this.displayString = displayString;
    }
    
    public String toIdentifier() {
        return this.identifier;
    }
    
    public static final OrderTerm fromIdentifier(String identifier) {
        return identifiers.get(identifier);
    }

    public String getDisplayString() {
        return displayString;
    }
}