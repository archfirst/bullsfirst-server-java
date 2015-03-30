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
package org.archfirst.bfexch.infra.jsontrading.converters;

import org.archfirst.common.money.Money;

/**
 * MoneyConverter
 *
 * @author Naresh Bhatia
 */
public class MoneyConverter {

    public static org.archfirst.bfcommon.jsontrading.Money toJson(Money money) {
        return (money == null) ? null : new org.archfirst.bfcommon.jsontrading.Money(
                money.getAmount(), money.getCurrency().toString());
    }
    
    public static Money toDomain(org.archfirst.bfcommon.jsontrading.Money money) {
        return (money == null) ? null : new Money(
                money.getAmount(),
                java.util.Currency.getInstance(money.getCurrency()));
    }
}