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
package org.archfirst.bfoms.interfaceout.exchange.trading;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.exchange.ExchangeMessageGenerator;
import org.archfirst.bfoms.domain.exchange.ExchangeTradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JmsExchangeTradingService
 *
 * @author Naresh Bhatia
 */
public class JmsExchangeTradingService implements ExchangeTradingService {
    private static final Logger logger =
        LoggerFactory.getLogger(JmsExchangeTradingService.class);
    
    @Resource(mappedName="jms/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName="jms/OmsToExchangeQueue")
    private Destination destination;

    @Inject private ExchangeMessageGenerator exchangeMessageGenerator;

    @Override
    public void placeOrder(Order order) {
        sendJmsMessage(
                exchangeMessageGenerator.generateNewOrderSingleMessage(order));
    }

    @Override
    public void cancelOrder(Order order) {
        sendJmsMessage(
                exchangeMessageGenerator.generateOrderCancelRequest(order));
    }
    
    private void sendJmsMessage(String messageText) {
        
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            producer.send(session.createTextMessage(messageText));
        }
        catch (JMSException e) {
            throw new RuntimeException("Failed to send message to exchange", e);
        }
        finally {
            if (connection != null)
                try {connection.close();} catch (Exception e) {}
        }
    }
}