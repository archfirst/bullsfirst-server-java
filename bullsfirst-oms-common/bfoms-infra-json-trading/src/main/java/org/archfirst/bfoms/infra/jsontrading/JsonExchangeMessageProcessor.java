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
package org.archfirst.bfoms.infra.jsontrading;

import javax.inject.Inject;

import org.archfirst.bfcommon.jsontrading.ExecutionReport;
import org.archfirst.bfcommon.jsontrading.JsonMessage;
import org.archfirst.bfcommon.jsontrading.JsonMessageMapper;
import org.archfirst.bfcommon.jsontrading.OrderCancelReject;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountService;
import org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReportType;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderStatus;
import org.archfirst.bfoms.domain.exchange.ExchangeMessageProcessor;
import org.archfirst.bfoms.infra.jsontrading.converters.ClOrdIDConverter;
import org.archfirst.bfoms.infra.jsontrading.converters.MoneyConverter;
import org.archfirst.bfoms.infra.jsontrading.converters.QuantityConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JsonExchangeMessageProcessor
 *
 * @author Naresh Bhatia
 */
public class JsonExchangeMessageProcessor implements ExchangeMessageProcessor {
    private static final Logger logger =
        LoggerFactory.getLogger(JsonExchangeMessageProcessor.class);

    @Inject private BrokerageAccountService brokerageAccountService;

    @Override
    public void processMessage(String messageText) {

        // Parse the message
        JsonMessage jsonMessage = JsonMessageMapper.fromString(messageText);
        logger.debug("Received message:\n{}", JsonMessageMapper.toFormattedString(jsonMessage));
        
        // Dispatch to the correct handler
        if (jsonMessage.getClass().equals(ExecutionReport.class)) {
            this.onMessage((ExecutionReport)jsonMessage);
        }
        else if (jsonMessage.getClass().equals(OrderCancelReject.class)) {
            this.onMessage((OrderCancelReject)jsonMessage);
        }
    }
    
    private void onMessage(ExecutionReport jsonEr) {

        // Convert to domain ExecutionReport
        org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReport er =
            new org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReport(
                    ExecutionReportType.valueOf(jsonEr.getType().toString()),
                    jsonEr.getOrderId(),
                    jsonEr.getExecutionId(),
                    ClOrdIDConverter.toDomain(jsonEr.getClientOrderId()),
                    OrderStatus.valueOf(jsonEr.getOrderStatus().toString()),
                    OrderSide.valueOf(jsonEr.getSide().toString()),
                    jsonEr.getSymbol(),
                    QuantityConverter.toDomain(jsonEr.getLastQty()),
                    QuantityConverter.toDomain(jsonEr.getLeavesQty()),
                    QuantityConverter.toDomain(jsonEr.getCumQty()),
                    MoneyConverter.toDomain(jsonEr.getLastPrice()),
                    MoneyConverter.toDomain(jsonEr.getWeightedAvgPrice()));

        // Send to brokerageAccountService for processing
        brokerageAccountService.processExecutionReport(er);
    }

    private void onMessage(OrderCancelReject orderCancelReject) {
        
        brokerageAccountService.processOrderCancelReject(
                ClOrdIDConverter.toDomain(orderCancelReject.getClientOrderId()),
                OrderStatus.valueOf(orderCancelReject.getOrderStatus().toString()));
    }
}