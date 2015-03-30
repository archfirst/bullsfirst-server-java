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
package org.archfirst.bfoms.interfacein.exchange;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.archfirst.bfoms.domain.exchange.ExchangeMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExchangeMessageListener
 *
 * @author Naresh Bhatia
 */
@MessageDriven(mappedName="jms/ExchangeToOmsJavaeeQueue")
public class ExchangeMessageListener implements MessageListener {
    private static final Logger logger =
        LoggerFactory.getLogger(ExchangeMessageListener.class);

    @Inject private ExchangeMessageProcessor exchangeMessageProcessor;

    public ExchangeMessageListener() {
        logger.debug("{}: ExchangeMessageListener created",
                Thread.currentThread().getName());
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String messageText = null;
            try {
                messageText = ((TextMessage)message).getText();
                exchangeMessageProcessor.processMessage(messageText);
            }
            catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }
}