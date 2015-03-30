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
package org.archfirst.bfexch.events;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.archfirst.bfexch.domain.trading.order.OrderAccepted;
import org.archfirst.bfexch.domain.trading.order.OrderCancelRejected;
import org.archfirst.bfexch.domain.trading.order.OrderCanceled;
import org.archfirst.bfexch.domain.trading.order.OrderDoneForDay;
import org.archfirst.bfexch.domain.trading.order.OrderEventPublisher;
import org.archfirst.bfexch.domain.trading.order.OrderExecuted;

/**
 * DefaultOrderEventPublisher
 *
 * @author Naresh Bhatia
 */
public class DefaultOrderEventPublisher implements OrderEventPublisher {

    // ----- Events -----
    @Inject private Event<OrderAccepted> orderAcceptedEvent;
    @Inject private Event<OrderExecuted> orderExecutedEvent;
    @Inject private Event<OrderCanceled> orderCanceledEvent;
    @Inject private Event<OrderCancelRejected> orderCancelRejectedEvent;
    @Inject private Event<OrderDoneForDay> orderDoneForDayEvent;

    // ----- Publisher Implementation -----
    @Override
    public void publish(OrderAccepted event) {
        orderAcceptedEvent.fire(event);
    }

    @Override
    public void publish(OrderExecuted event) {
        orderExecutedEvent.fire(event);
    }

    @Override
    public void publish(OrderCanceled event) {
        orderCanceledEvent.fire(event);
    }

    @Override
    public void publish(OrderCancelRejected event) {
        orderCancelRejectedEvent.fire(event);
    }

    @Override
    public void publish(OrderDoneForDay event) {
        orderDoneForDayEvent.fire(event);
    }
}