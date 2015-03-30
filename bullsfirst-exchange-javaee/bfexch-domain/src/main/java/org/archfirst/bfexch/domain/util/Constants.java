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
package org.archfirst.bfexch.domain.util;

/**
 * Constants
 *
 * @author Naresh Bhatia
 */
public class Constants {
    
    private Constants() {
    }

    public static final int ENUM_COLUMN_LENGTH = 8;

    // Money can fit this format: 123,456,789,012,345.67 (2 fractional digits)
    public static final int MONEY_PRECISION = 17;
    public static final int MONEY_SCALE = 2;

    // Price can fit this format: 123,456,789,012,345.6789 (4 fractional digits)
    public static final int PRICE_PRECISION = 19;
    public static final int PRICE_SCALE = 4;

    // Quantity can fit this format: 1,234,567,890,123 (Whole numbers)
    public static final int QUANTITY_PRECISION = 13;
    public static final int QUANTITY_SCALE = 0;

    // Gain can fit this format: xxxx.12 (2 fractional digits)
    public static final int GAIN_SCALE = 2;

    // Cash instrument constants
    public static final String CASH_INSTRUMENT_SYMBOL = "CASH";
    public static final String CASH_INSTRUMENT_NAME = "Cash";
}