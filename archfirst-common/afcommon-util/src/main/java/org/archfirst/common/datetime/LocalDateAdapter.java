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
package org.archfirst.common.datetime;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.LocalDate;

/**
 * LocalDateAdapter
 *
 * @author Naresh Bhatia
 */
public class LocalDateAdapter extends XmlAdapter<XMLGregorianCalendar, LocalDate> {
    
    public LocalDate unmarshal(XMLGregorianCalendar val) throws Exception {
        return new LocalDate(val.toGregorianCalendar());
    }
    
    public XMLGregorianCalendar marshal(LocalDate val) throws Exception {
        try {
            GregorianCalendar calendar = val.toDateTimeAtStartOfDay().toGregorianCalendar();
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        }
        catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException();
        }
    }
}