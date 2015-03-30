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
package org.archfirst.bfexch.infra.jsontrading;

import javax.inject.Inject;

import org.archfirst.bfcommon.jsontrading.JsonMessage;
import org.archfirst.bfcommon.jsontrading.JsonMessageMapper;
import org.archfirst.bfcommon.jsontrading.NewOrderSingle;
import org.archfirst.bfcommon.jsontrading.OrderCancelRequest;
import org.archfirst.bfexch.domain.broker.BrokerMessageProcessor;
import org.archfirst.bfexch.domain.trading.TradingService;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.domain.trading.order.OrderSide;
import org.archfirst.bfexch.domain.trading.order.OrderTerm;
import org.archfirst.bfexch.domain.trading.order.OrderType;
import org.archfirst.bfexch.infra.jsontrading.converters.MoneyConverter;
import org.archfirst.bfexch.infra.jsontrading.converters.QuantityConverter;
import org.archfirst.common.datetime.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JsonBrokerMessageProcessor
 *
 * @author Naresh Bhatia
 */
public class JsonBrokerMessageProcessor implements BrokerMessageProcessor {
    private static final Logger logger =
        LoggerFactory.getLogger(JsonBrokerMessageProcessor.class);

    @Inject private TradingService tradingService;

    @Override
    public void processMessage(String messageText) {

        // Parse the message
        JsonMessage jsonMessage = JsonMessageMapper.fromString(messageText);
        logger.debug("Received message:\n{}", JsonMessageMapper.toFormattedString(jsonMessage));
        
        // Dispatch to the correct handler
        if (jsonMessage.getClass().equals(NewOrderSingle.class)) {
            this.onMessage((NewOrderSingle)jsonMessage);
        }
        else if (jsonMessage.getClass().equals(OrderCancelRequest.class)) {
            this.onMessage((OrderCancelRequest)jsonMessage);
        }
    }
    
    private void onMessage(NewOrderSingle newOrderSingle) {

        // Extract Order
        org.archfirst.bfcommon.jsontrading.Order jsonOrder = newOrderSingle.getOrder();
        Order order = new Order(
                DateTimeUtil.parseISODateTime(jsonOrder.getCreationTime()),
                jsonOrder.getClientOrderId(),
                OrderSide.valueOf(jsonOrder.getSide().toString()),
                jsonOrder.getSymbol(),
                QuantityConverter.toDomain(jsonOrder.getQuantity()),
                OrderType.valueOf(jsonOrder.getType().toString()),
                MoneyConverter.toDomain(jsonOrder.getLimitPrice()),
                OrderTerm.valueOf(jsonOrder.getTerm().toString()),
                jsonOrder.isAllOrNone());

        // Place order
        tradingService.processNewOrderSingle(order);
    }

    private void onMessage(OrderCancelRequest orderCancelRequest) {
        tradingService.processOrderCancelRequest(
                orderCancelRequest.getClientOrderId());
    }
}