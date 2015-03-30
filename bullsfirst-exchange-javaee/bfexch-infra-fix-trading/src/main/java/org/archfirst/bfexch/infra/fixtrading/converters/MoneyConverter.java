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

import java.math.BigDecimal;

import org.archfirst.common.money.Money;

import quickfix.field.Currency;
import quickfix.field.Price;

/**
 * MoneyConverter
 *
 */
public class MoneyConverter {

    public static Price toFixPrice(Money money) {
        return new Price(money.getAmount().doubleValue());
    }

    public static Currency toFixCurrency(Money money) {
        return new Currency(money.getCurrency().getCurrencyCode());
    }

    public static Money toDomain(Price price, Currency currency) {
        // Convert using string representation of double
        return new Money(
                new BigDecimal(Double.toString(price.getValue())),
                java.util.Currency.getInstance(currency.getValue()));
    }
}