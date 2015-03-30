/**
 * Copyright 2012 Archfirst
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
package org.archfirst.bfexch.restservice.util;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.archfirst.bfcommon.restutils.DateTimeSerializer;
import org.archfirst.bfcommon.restutils.DecimalQuantitySerializer;
import org.archfirst.bfcommon.restutils.MoneySerializer;
import org.archfirst.bfcommon.restutils.PercentageDeserializer;
import org.archfirst.bfcommon.restutils.PercentageSerializer;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.quantity.Percentage;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.module.SimpleModule;
import org.joda.time.DateTime;

/**
 * Provider for Object to JSON conversion. Required for ignoring JAXB
 * annotations on objects. Without this provider JAX-RS picks up annotations
 * meant for XML conversion and throws exceptions when casing on properties
 * does not match. 
 *
 * @author Naresh Bhatia
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
    
    private final ObjectMapper mapper;

    public ObjectMapperProvider(){
        mapper = new ObjectMapper();
        mapper.configure(
                SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.getSerializationConfig().setSerializationInclusion(
                JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(
                SerializationConfig.Feature.INDENT_OUTPUT, true);

        SimpleModule conversionModule = new SimpleModule("ConversionsModule", new Version(1, 0, 0, null))
            .addSerializer(Money.class, new MoneySerializer())
            .addSerializer(DecimalQuantity.class, new DecimalQuantitySerializer())
            .addSerializer(Percentage.class, new PercentageSerializer())
            .addDeserializer(Percentage.class, new PercentageDeserializer())
            .addSerializer(DateTime.class, new DateTimeSerializer());
        mapper.registerModule(conversionModule);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}