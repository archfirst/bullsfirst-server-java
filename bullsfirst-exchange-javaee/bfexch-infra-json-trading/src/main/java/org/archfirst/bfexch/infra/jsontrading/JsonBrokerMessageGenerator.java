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

import org.archfirst.bfcommon.jsontrading.ExecutionReportType;
import org.archfirst.bfcommon.jsontrading.JsonMessageMapper;
import org.archfirst.bfcommon.jsontrading.OrderCancelReject;
import org.archfirst.bfcommon.jsontrading.OrderSide;
import org.archfirst.bfcommon.jsontrading.OrderStatus;
import org.archfirst.bfexch.domain.broker.BrokerMessageGenerator;
import org.archfirst.bfexch.domain.trading.order.ExecutionReport;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.infra.jsontrading.converters.MoneyConverter;
import org.archfirst.bfexch.infra.jsontrading.converters.QuantityConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JsonBrokerMessageGenerator
 *
 * @author Naresh Bhatia
 */
public class JsonBrokerMessageGenerator implements BrokerMessageGenerator {
    private static final Logger logger =
        LoggerFactory.getLogger(JsonBrokerMessageGenerator.class);

    @Override
    public String generateExecutionReport(ExecutionReport executionReport) {

        org.archfirst.bfcommon.jsontrading.ExecutionReport jsonExecutionReport =
            new org.archfirst.bfcommon.jsontrading.ExecutionReport(
                    ExecutionReportType.valueOf(executionReport.getType().toString()),
                    executionReport.getOrderId(),
                    executionReport.getExecutionId(),
                    executionReport.getClientOrderId(),
                    OrderStatus.valueOf(executionReport.getOrderStatus().toString()),
                    OrderSide.valueOf(executionReport.getSide().toString()),
                    executionReport.getSymbol(),
                    QuantityConverter.toJson(executionReport.getLastQty()),
                    QuantityConverter.toJson(executionReport.getLeavesQty()),
                    QuantityConverter.toJson(executionReport.getCumQty()),
                    MoneyConverter.toJson(executionReport.getLastPrice()),
                    MoneyConverter.toJson(executionReport.getWeightedAvgPrice()));

        logger.debug("Sending message:\n{}", JsonMessageMapper.toFormattedString(jsonExecutionReport));
        return JsonMessageMapper.toString(jsonExecutionReport);
    }

    @Override
    public String generateOrderCancelReject(Order order) {

        OrderCancelReject orderCancelReject = new OrderCancelReject(
                order.getClientOrderId(),
                OrderStatus.valueOf(order.getStatus().toString()));

        logger.debug("Sending message:\n{}", JsonMessageMapper.toFormattedString(orderCancelReject));
        return JsonMessageMapper.toString(orderCancelReject);
    }
}