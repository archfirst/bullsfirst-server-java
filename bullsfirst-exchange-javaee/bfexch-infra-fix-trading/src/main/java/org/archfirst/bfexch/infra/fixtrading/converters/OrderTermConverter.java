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
package org.archfirst.bfexch.infra.fixtrading.converters;

import org.archfirst.bfexch.domain.trading.order.OrderTerm;

import quickfix.field.TimeInForce;

/**
 * OrderTermConverter
 *
 * @author Naresh Bhatia
 */
public class OrderTermConverter {

    private static FixConverter<OrderTerm, TimeInForce> fixConverter =
        new FixConverter<OrderTerm, TimeInForce>();

    static {
        fixConverter.put(OrderTerm.GoodForTheDay, new TimeInForce(TimeInForce.DAY));
        fixConverter.put(OrderTerm.GoodTilCanceled, new TimeInForce(TimeInForce.GOOD_TILL_CANCEL));
    }

    public static TimeInForce toFix(OrderTerm term) {
        return fixConverter.toFix(term);
    }

    public static OrderTerm toDomain(TimeInForce timeInForce) {
        return fixConverter.toDomain(timeInForce);
    }
}