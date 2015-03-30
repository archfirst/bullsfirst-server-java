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
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.archfirst.bfexch.domain.marketdata.MarketPrice;
import org.archfirst.bfexch.domain.marketdata.MarketPriceChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MarketPricePublisher
 *
 * @author Naresh Bhatia
 */
public class MarketPricePublisher {
    private static final Logger logger =
        LoggerFactory.getLogger(MarketPricePublisher.class);

    @Resource(mappedName="jms/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName="jms/ExchangeMarketPriceTopic")
    private Destination destination;

    public void onMarketPriceChanged(@Observes MarketPriceChanged event) {

        MarketPrice marketPrice = event.getMarketPrice();
        logger.debug("Publishing market price:\n{}", marketPrice);

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            producer.send(session.createTextMessage(marketPrice.toProperties()));
        }
        catch (JMSException e) {
            throw new RuntimeException("Failed to publish market price", e);
        }
        finally {
            if (connection != null)
                try {connection.close();} catch (Exception e) {}
        }
    }
}