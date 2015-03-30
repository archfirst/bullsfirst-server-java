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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.DataDictionary;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.FieldNotFound;
import quickfix.FixVersions;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.field.BeginString;

/**
 * FixUtil
 *
 * @author Naresh Bhatia
 */
public class FixUtil {
    
    private static final Logger logger =
        LoggerFactory.getLogger(FixUtil.class);
    
    public static String getBeginString(Message message) {
        String beginString;
        try {
            beginString = message.getHeader().getString(BeginString.FIELD);
        }
        catch (FieldNotFound e) {
            logger.warn("BeginString not found in message: {}", message);
            beginString = FixVersions.BEGINSTRING_FIX44;  // use a default value
        }
        return beginString;
    }
    
    public static DataDictionary getDefaultDataDictionary() {
        return getDataDictionary(FixVersions.BEGINSTRING_FIX44);
    }

    public static DataDictionary getDataDictionary(String beginString) {
        return new DefaultDataDictionaryProvider()
            .getSessionDataDictionary(beginString);
    }
    
    public static MessageFactory getDefaultMessageFactory() {
        return new quickfix.fix44.MessageFactory();
    }
}