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
package org.archfirst.bfoms.infra.events;

import javax.enterprise.event.Observes;

import org.archfirst.bfoms.domain.account.brokerage.order.OrderCreated;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderStatusChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DefaultOrderEventSubscriber
 *
 * @author Naresh Bhatia
 */
public class DefaultOrderEventSubscriber {
    private static final Logger logger =
        LoggerFactory.getLogger(DefaultOrderEventSubscriber.class);
    
    public void onOrderCreated(@Observes OrderCreated event) {
        logger.debug("Order #{}: status={}",
                event.getOrder().getId(), event.getOrder().getStatus());
    }
    
    public void onOrderCreated(@Observes OrderStatusChanged event) {
        logger.debug("Order #{}: status={}",
                event.getOrder().getId(), event.getOrder().getStatus());
    }
}