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
package org.archfirst.bfexch.interfaceout.oms;

import javax.annotation.Resource;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.archfirst.bfexch.domain.broker.BrokerMessageGenerator;
import org.archfirst.bfexch.domain.trading.order.ClOrdIDParser;
import org.archfirst.bfexch.domain.trading.order.ExecutionReport;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.domain.trading.order.OrderAccepted;
import org.archfirst.bfexch.domain.trading.order.OrderCancelRejected;
import org.archfirst.bfexch.domain.trading.order.OrderCanceled;
import org.archfirst.bfexch.domain.trading.order.OrderDoneForDay;
import org.archfirst.bfexch.domain.trading.order.OrderExecuted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrokerAdapter
 *
 * @author Naresh Bhatia
 */
public class BrokerAdapter {
    private static final Logger logger =
        LoggerFactory.getLogger(BrokerAdapter.class);

    @Resource(mappedName="jms/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject private DestinationDictionary destinationDictionary;
    @Inject BrokerMessageGenerator brokerMessageGenerator;

    public void onOrderAccepted(@Observes OrderAccepted event) {
        sendExecutionReport(ExecutionReport.createNewType(event.getOrder()));
    }

    public void onOrderExecuted(@Observes OrderExecuted event) {
        sendExecutionReport(ExecutionReport.createTradeType(event.getExecution()));
    }

    public void onOrderCanceled(@Observes OrderCanceled event) {
        sendExecutionReport(ExecutionReport.createCanceledType(event.getOrder()));
    }

    public void onOrderCancelRejected(@Observes OrderCancelRejected event) {
        sendOrderCancelRejected(event.getOrder());
    }

    public void onOrderDoneForDay(@Observes OrderDoneForDay event) {
        sendExecutionReport(ExecutionReport.createDoneForDayType(event.getOrder()));
    }

    private void sendExecutionReport(ExecutionReport executionReport) {
        sendJmsMessage(
                ClOrdIDParser.getBrokerId(executionReport.getClientOrderId()),
                brokerMessageGenerator.generateExecutionReport(executionReport));
    }

    private void sendOrderCancelRejected(Order order) {

        sendJmsMessage(
                ClOrdIDParser.getBrokerId(order.getClientOrderId()),
                brokerMessageGenerator.generateOrderCancelReject(order));
    }

    private void sendJmsMessage(final String brokerId, String messageText) {
        
        logger.debug("Sending message to {}", brokerId);
        Destination destination =
            destinationDictionary.getBrokerDestination(brokerId);

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            producer.send(session.createTextMessage(messageText));
        }
        catch (JMSException e) {
            throw new RuntimeException("Failed to send message to broker", e);
        }
        finally {
            if (connection != null)
                try {connection.close();} catch (Exception e) {}
        }
    }
}