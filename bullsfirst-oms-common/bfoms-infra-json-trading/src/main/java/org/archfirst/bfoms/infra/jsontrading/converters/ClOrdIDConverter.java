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
package org.archfirst.bfoms.infra.jsontrading.converters;

import org.apache.commons.lang.StringUtils;

/**
 * ClOrdIDConverter
 *
 * @author Naresh Bhatia
 */
public class ClOrdIDConverter {
    
    /** Returns a ClOrdID in the format brokerId-orderId, e.g. JVEE-300. */
    public static String toJson(String brokerId, Long orderId) {
        return brokerId + "-" + orderId.toString();
    }
    
    public static Long toDomain(String clOrdID) {
        return new Long(StringUtils.substring(clOrdID, 5));
    }
}