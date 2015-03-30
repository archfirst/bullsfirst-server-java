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

import java.math.BigDecimal;

import org.archfirst.common.money.Money;

import quickfix.FieldNotFound;
import quickfix.field.AvgPx;

/**
 * AvgPriceConverter
 *
 * @author Naresh Bhatia
 */
public class AvgPriceConverter {

    public static AvgPx toFix(Money money) {
        return new AvgPx(money.getAmount().doubleValue());
    }
    
    public static Money toDomain(AvgPx avgPx)
            throws FieldNotFound {
        return new Money(new BigDecimal(Double.toString(avgPx.getValue())));
    }
}