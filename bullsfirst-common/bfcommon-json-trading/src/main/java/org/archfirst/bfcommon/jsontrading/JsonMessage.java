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

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

/**
 * JsonMessage
 *
 * @author Naresh Bhatia
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="messageType")
@JsonSubTypes({
    @Type(value = NewOrderSingle.class, name = "NewOrderSingle"),
    @Type(value = ExecutionReport.class, name = "ExecutionReport"),
    @Type(value = OrderCancelReject.class, name = "OrderCancelReject"),
    @Type(value = OrderCancelRequest.class, name = "OrderCancelRequest")})
public abstract class JsonMessage {
}