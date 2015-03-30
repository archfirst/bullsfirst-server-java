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
package org.archfirst.bfcommon.jsontrading;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * JsonMessageMapper
 *
 * @author Naresh Bhatia
 */
public final class JsonMessageMapper {
    
    // Shared instances of mapper for performance
    // (mappers are thread-safe after configuration)
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper prettyMapper = new ObjectMapper();
    
    static {
        mapper.configure(
                SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.getSerializationConfig().setSerializationInclusion(
                JsonSerialize.Inclusion.NON_NULL);

        prettyMapper.configure(
                SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        prettyMapper.getSerializationConfig().setSerializationInclusion(
                JsonSerialize.Inclusion.NON_NULL);
        prettyMapper.configure(
                SerializationConfig.Feature.INDENT_OUTPUT, true);
    }
    
    public static final String toString(JsonMessage jsonMessage) {
        try {
            return mapper.writeValueAsString(jsonMessage);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to convert to string", e);
        }
    }
    
    public static final String toFormattedString(JsonMessage jsonMessage) {
        try {
            return prettyMapper.writeValueAsString(jsonMessage);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to convert to string", e);
        }
    }
    
    public static final JsonMessage fromString(String messageText) {

        try {
            return mapper.readValue(messageText, JsonMessage.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to convert to string", e);
        }
    }
}