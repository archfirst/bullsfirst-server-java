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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * FixFormatter
 *
 * @author Naresh Bhatia
 */
public class FixFormatter {
    
    private static final Logger logger =
        LoggerFactory.getLogger(FixFormatter.class);
    
    private final DataDictionary dd;
    private final StringBuffer sb = new StringBuffer();
    
    private FixFormatter(DataDictionary dd) {
        this.dd = dd;
    }

    /** Formats the specified message */
    private String formatMessage(Message message) {
        
        // Format the header, message and trailer
        try {
            String msgType = message.getHeader().getString(MsgType.FIELD);
            formatFieldMap("", msgType, message.getHeader());
            formatFieldMap("", msgType, message);
            formatFieldMap("", msgType, message.getTrailer());
        }
        catch (FieldNotFound fnf) {
            logger.error("FixFormatter.formatMessage failed", fnf);
        }
        
        return sb.toString();
    }

    /**
     * Formats a FieldMap
     * @param prefix
     * @param msgType
     * @param fieldMap
     * @throws FieldNotFound
     */
    private void formatFieldMap(String prefix, String msgType, FieldMap fieldMap) throws FieldNotFound {

        Iterator<Field<?>> fieldIterator = fieldMap.iterator();
        while (fieldIterator.hasNext()) {
            Field<?> field = fieldIterator.next();
            if (!isGroupCountField(field)) {
                String value = fieldMap.getString(field.getTag());
                if (dd.hasFieldValue(field.getTag())) {
                    value = dd.getValueName(field.getTag(), fieldMap
                            .getString(field.getTag()))
                            + " (" + value + ")";
                }
                sb.append(prefix + dd.getFieldName(field.getTag())
                    + ": " + value + "\n");
            }
        }

        Iterator<Integer> groupsKeys = fieldMap.groupKeyIterator();
        while (groupsKeys.hasNext()) {
            int groupCountTag = ((Integer) groupsKeys.next()).intValue();
            sb.append(prefix + dd.getFieldName(groupCountTag)
                + ": count = " + fieldMap.getInt(groupCountTag) + "\n");
            Group g = new Group(groupCountTag, 0);
            int i = 1;
            while (fieldMap.hasGroup(i, groupCountTag)) {
                if (i > 1) {
                    sb.append(prefix + "  ----\n");
                }
                fieldMap.getGroup(i, g);
                formatFieldMap(prefix + "  ", msgType, g);
                i++;
            }
        }
    }

    /** Returns true if the supplied field is a group count field */
    private boolean isGroupCountField(Field<?> field) {
        return dd.getFieldTypeEnum(field.getTag()) == FieldType.NumInGroup;
    }
    
    public static final String format(Message message) {
        DataDictionary dd = FixUtil.getDataDictionary(
                FixUtil.getBeginString(message));
        return new FixFormatter(dd).formatMessage(message);
    }
}