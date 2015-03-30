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

import org.archfirst.bfoms.domain.account.brokerage.order.OrderStatus;

import quickfix.field.OrdStatus;

/**
 * OrderStatusConverter
 *
 * @author Naresh Bhatia
 */
public class OrderStatusConverter {
    
    private static FixConverter<OrderStatus, OrdStatus> fixConverter =
        new FixConverter<OrderStatus, OrdStatus>();
    
    static {
        fixConverter.put(OrderStatus.PendingNew, new OrdStatus(OrdStatus.PENDING_NEW));
        fixConverter.put(OrderStatus.New, new OrdStatus(OrdStatus.NEW));
        fixConverter.put(OrderStatus.PartiallyFilled, new OrdStatus(OrdStatus.PARTIALLY_FILLED));
        fixConverter.put(OrderStatus.Filled, new OrdStatus(OrdStatus.FILLED));
        fixConverter.put(OrderStatus.PendingCancel, new OrdStatus(OrdStatus.PENDING_CANCEL));
        fixConverter.put(OrderStatus.Canceled, new OrdStatus(OrdStatus.CANCELED));
        fixConverter.put(OrderStatus.DoneForDay, new OrdStatus(OrdStatus.DONE_FOR_DAY));
    }

    public static OrdStatus toFix(OrderStatus Status) {
        return fixConverter.toFix(Status);
    }

    public static OrderStatus toDomain(OrdStatus status) {
        return fixConverter.toDomain(status);
    }
}