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
package org.archfirst.bfexch.domain.trading;

import java.util.List;

import javax.inject.Inject;

import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.domain.trading.order.OrderCancelRejected;
import org.archfirst.bfexch.domain.trading.order.OrderCanceled;
import org.archfirst.bfexch.domain.trading.order.OrderDoneForDay;
import org.archfirst.bfexch.domain.trading.order.OrderEventPublisher;
import org.archfirst.bfexch.domain.trading.order.OrderRepository;
import org.archfirst.bfexch.domain.trading.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TradingService
 *
 * @author Naresh Bhatia
 */
public class TradingService {
    private static final Logger logger =
        LoggerFactory.getLogger(TradingService.class);

    // ----- Commands -----
    public void processNewOrderSingle(Order order) {
        matchingEngine.placeOrder(order);
    }
    
    /**
     * Process OrderCancelRequest. Cancels the order if the status change is valid.
     */
    public void processOrderCancelRequest(String clOrdID) {
        Order order = orderRepository.findOrderByClientOrderId(clOrdID);
        if (order == null) {
            logger.error("OrderCancelRequest: clOrdID {} not found", clOrdID);
            return;
        }

        order.cancel();
        if (order.getStatus() == OrderStatus.Canceled) {
            orderEventPublisher.publish(new OrderCanceled(order));
        }
        else {
            orderEventPublisher.publish(new OrderCancelRejected(order));
        }
    }
    
    public void handleEndOfDay() {
        logger.info("Processing end of day event...");
        List<Order> orders = orderRepository.findActiveGfdOrders();
        logger.info("Marking {} orders as DoneForDay...", orders.size());
        for (Order order : orders) {
            order.doneForDay();
            orderEventPublisher.publish(new OrderDoneForDay(order));
        }
        logger.info("Marked {} orders as DoneForDay", orders.size());
    }

    // ----- Queries -----

    // ----- Attributes -----
    @Inject private OrderRepository orderRepository;
    @Inject private OrderEventPublisher orderEventPublisher;
    @Inject MatchingEngine matchingEngine;
}