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

import org.apache.commons.lang.StringUtils;

import quickfix.field.OrigClOrdID;

/**
 * OrigClOrdIDConverter
 *
 * @author Naresh Bhatia
 */
public class OrigClOrdIDConverter {
    
    /** Returns a OrigClOrdID in the format brokerId-orderId, e.g. SPNG-300. */
    public static OrigClOrdID toFix(String brokerId, Long orderId) {
        return new OrigClOrdID(
                brokerId +
                "-" +
                orderId.toString());
    }
    
    public static Long toDomain(OrigClOrdID origClOrdID) {
        return new Long(StringUtils.substring(origClOrdID.getValue(), 5));
    }
}