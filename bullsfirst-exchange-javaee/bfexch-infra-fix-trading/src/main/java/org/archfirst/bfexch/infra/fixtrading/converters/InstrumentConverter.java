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

import quickfix.field.Symbol;

/**
 * InstrumentConverter
 *
 * @author Naresh Bhatia
 */
public class InstrumentConverter {

    public static quickfix.fix44.component.Instrument toFix(String symbol) {
        return new quickfix.fix44.component.Instrument(
                new Symbol(symbol));
    }
}