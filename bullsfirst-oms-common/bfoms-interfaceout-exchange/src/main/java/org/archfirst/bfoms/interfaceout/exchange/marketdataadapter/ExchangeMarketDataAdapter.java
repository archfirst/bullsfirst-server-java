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
package org.archfirst.bfoms.interfaceout.exchange.marketdataadapter;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.archfirst.bfoms.domain.marketdata.MarketDataAdapter;
import org.archfirst.bfoms.domain.marketdata.MarketPrice;
import org.archfirst.bfoms.interfaceout.exchange.marketdataadapter.client.MarketDataService;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExchangeMarketDataAdapter
 *
 * @author Naresh Bhatia
 */
public class ExchangeMarketDataAdapter implements MarketDataAdapter {
    private static final Logger logger =
        LoggerFactory.getLogger(ExchangeMarketDataAdapter.class);

    @Override
    public List<MarketPrice> getMarketPrices() {
        logger.debug("---> ExchangeMarketDataAdapter.getMarketPrices()");
        
        MarketDataService service = new MarketDataService();
        List<org.archfirst.bfoms.interfaceout.exchange.marketdataadapter.client.MarketPrice> wsMarketPrices =
            service.getMarketDataWebServicePort().getMarketPrices();

        List<MarketPrice> marketPrices = new ArrayList<MarketPrice>();
        for (org.archfirst.bfoms.interfaceout.exchange.marketdataadapter.client.MarketPrice wsMarketPrice : wsMarketPrices) {
            marketPrices.add(toDomainMarketPrice(wsMarketPrice));
        }

        logger.debug("<--- ExchangeMarketDataAdapter.getMarketPrices()");
        return marketPrices;
    }

    private static final MarketPrice toDomainMarketPrice(
            org.archfirst.bfoms.interfaceout.exchange.marketdataadapter.client.MarketPrice wsMarketPrice) {

        return new MarketPrice(
                wsMarketPrice.getSymbol(),
                new Money(
                        wsMarketPrice.getPrice().getAmount(),
                        Currency.getInstance(wsMarketPrice.getPrice().getCurrency())),
                new DateTime(wsMarketPrice.getEffective().toGregorianCalendar()));
    }
}