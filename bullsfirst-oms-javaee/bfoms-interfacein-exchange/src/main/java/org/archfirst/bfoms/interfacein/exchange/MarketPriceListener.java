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

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Properties;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.archfirst.bfoms.domain.marketdata.MarketDataService;
import org.archfirst.bfoms.domain.marketdata.MarketPrice;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MarketPriceListener
 *
 * @author Naresh Bhatia
 */
@MessageDriven(mappedName="jms/ExchangeMarketPriceTopic")
public class MarketPriceListener implements MessageListener {
    private static final Logger logger =
        LoggerFactory.getLogger(MarketPriceListener.class);

    @Inject private MarketDataService marketDataService;

    public MarketPriceListener() {
        logger.debug("{}: MarketPriceListener created",
                Thread.currentThread().getName());
    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof TextMessage) {
            try {
                String messageText = ((TextMessage)message).getText();
                MarketPrice marketPrice = toMarketPrice(messageText);
                logger.debug("Received market price:\n{}", marketPrice);
                marketDataService.updateMarketPrice(marketPrice);
            }
            catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private MarketPrice toMarketPrice(String marketPriceString) {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(marketPriceString));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(marketPriceString, e);
        }

        DateTime effective = ISODateTimeFormat.dateTimeParser().parseDateTime(
                properties.getProperty("effective"));
        String symbol = properties.getProperty("symbol");
        BigDecimal price = new BigDecimal(
                properties.getProperty("price"));
        Currency currency = Currency.getInstance(
                properties.getProperty("currency"));

        return new MarketPrice(
                symbol, new Money(price, currency), effective);
    }
}