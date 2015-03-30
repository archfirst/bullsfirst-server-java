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
package org.archfirst.bfoms.infra.fixtrading.converters;

import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;

import quickfix.field.Side;

/**
 * OrderSideConverter
 *
 * @author Naresh Bhatia
 */
public class OrderSideConverter {
    
    private static FixConverter<OrderSide, Side> fixConverter =
        new FixConverter<OrderSide, Side>();
    
    static {
        fixConverter.put(OrderSide.Buy, new Side(Side.BUY));
        fixConverter.put(OrderSide.Sell, new Side(Side.SELL));
    }

    public static Side toFix(OrderSide side) {
        return fixConverter.toFix(side);
    }

    public static OrderSide toDomain(Side side) {
        return fixConverter.toDomain(side);
    }
}