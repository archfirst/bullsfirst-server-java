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
package org.archfirst.bfexch.infra.fixtrading;

import org.archfirst.bfexch.domain.broker.BrokerMessageGenerator;
import org.archfirst.bfexch.domain.trading.order.ExecutionReport;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.infra.fixtrading.converters.AvgPriceConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.CumQtyConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.ExecutionTypeConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.FixFormatter;
import org.archfirst.bfexch.infra.fixtrading.converters.InstrumentConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.LastPriceConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.LastQtyConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.LeavesQtyConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.OrderSideConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.OrderStatusConverter;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.field.ClOrdID;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExecID;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;

/**
 * FixBrokerMessageGenerator
 *
 * @author Naresh Bhatia
 */
public class FixBrokerMessageGenerator implements BrokerMessageGenerator {
    private static final Logger logger =
        LoggerFactory.getLogger(FixBrokerMessageGenerator.class);

    @Override
    public String generateExecutionReport(ExecutionReport executionReport) {

        // Initialize executionId - it is a required FIX field
        String executionId =  executionReport.getExecutionId();
        if (executionId == null) {
            executionId = "0";
        }

        // Initialize lastQty - it is a required FIX field
        DecimalQuantity lastQty = executionReport.getLastQty();
        if (lastQty == null) {
            lastQty = new DecimalQuantity("0");
        }

        // Initialize lastPrice - it is a required FIX field
        Money lastPrice = executionReport.getLastPrice();
        if (lastPrice == null) {
            lastPrice = new Money("0.00");
        }

        // Now create the FIX message
        quickfix.fix44.ExecutionReport fixMessage =
            new quickfix.fix44.ExecutionReport(
                    new OrderID(executionReport.getOrderId().toString()),
                    new ExecID(executionId),
                    ExecutionTypeConverter.toFix(executionReport.getType()),
                    OrderStatusConverter.toFix(executionReport.getOrderStatus()),
                    OrderSideConverter.toFix(executionReport.getSide()),
                    LeavesQtyConverter.toFix(executionReport.getLeavesQty()),
                    CumQtyConverter.toFix(executionReport.getCumQty()),
                    AvgPriceConverter.toFix(executionReport.getWeightedAvgPrice()));

        fixMessage.set(new ClOrdID(executionReport.getClientOrderId()));
        fixMessage.set(InstrumentConverter.toFix(executionReport.getSymbol()));
        fixMessage.set(LastQtyConverter.toFix(lastQty));
        fixMessage.set(LastPriceConverter.toFix(lastPrice));

        logger.debug("Sending message:\n{}", FixFormatter.format(fixMessage));
        return fixMessage.toString();
    }

    @Override
    public String generateOrderCancelReject(Order order) {
        quickfix.fix44.OrderCancelReject fixMessage =
            new quickfix.fix44.OrderCancelReject(
                    new OrderID(order.getId().toString()),
                    new ClOrdID(order.getClientOrderId()),
                    new OrigClOrdID(order.getClientOrderId()),
                    OrderStatusConverter.toFix(order.getStatus()),
                    new CxlRejResponseTo(CxlRejResponseTo.ORDER_CANCEL_REQUEST));

        logger.debug("Sending message:\n{}", FixFormatter.format(fixMessage));
        return fixMessage.toString();
    }
}