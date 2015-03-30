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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * OrderSide
 *
 * @author Naresh Bhatia
 */
@XmlType(name = "OrderSide")
@XmlEnum
public enum OrderSide {
    @XmlEnumValue("Buy")
    Buy("BUY", "Buy"),
    @XmlEnumValue("Sell")
    Sell("SELL", "Sell");
    
    private final String identifier;
    private final String displayString;
    
    private static Map<String, OrderSide> identifiers =
        new HashMap<String, OrderSide>();
    
    static {
        for (OrderSide type : EnumSet.allOf(OrderSide.class)) {
            identifiers.put(type.toIdentifier(), type);
        }
    }
    
    private OrderSide(String identifier, String displayString) {
        this.identifier = identifier;
        this.displayString = displayString;
    }
    
    public String toIdentifier() {
        return this.identifier;
    }
    
    public static final OrderSide fromIdentifier(String identifier) {
        return identifiers.get(identifier);
    }

    public String getDisplayString() {
        return displayString;
    }
}