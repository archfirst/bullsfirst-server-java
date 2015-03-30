/**
 * Copyright 2010 Archfirst
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
package org.archfirst.bfoms.domain.marketdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.archfirst.common.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides market prices for all instruments. Keeps prices in a memory
 * cache. This cache is initialized on startup using an external market data
 * service (via MarketDataAdapter). Prices can be later updated by calling
 * updateMarketPrice().
 * 
 * @author Naresh Bhatia
 */
@Singleton
public class MarketDataService {
    private static final Logger logger =
        LoggerFactory.getLogger(MarketDataService.class);

    // ----- Constructors -----
    public MarketDataService() {
        logger.debug("MarketDataService.MarketDataService()");
    }

    // ----- Commands -----
    public void updateMarketPrice(MarketPrice marketPrice) {
        getPriceMap().put(marketPrice.getSymbol(), marketPrice);
    }

    // ----- Queries -----
    public Money getMarketPrice(String symbol) {
        return getPriceMap().get(symbol).getPrice();
    }

    // ----- Attributes -----
    @Inject private MarketDataAdapter marketDataAdapter;

    /**
     * Map from symbol to MarketPrice. We will not synchronize this map
     * because no structural changes (additions or deletions of mappings)
     * are performed after initialization. Also this map is lazily initialized
     * using <a href="http://en.wikipedia.org/wiki/Double-checked_locking">
     * double-checked locking</a>.
     */
    private volatile Map<String, MarketPrice> priceMap = null;

    // ----- Getters -----
    private Map<String, MarketPrice> getPriceMap() {
        if (priceMap == null) {
            synchronized(this) {
                if (priceMap == null) {
                    initPriceMap();
                }
            }
        }
        return priceMap;
    }

    private void initPriceMap() {
        priceMap = new HashMap<String, MarketPrice>();
        List<MarketPrice> marketPrices = marketDataAdapter.getMarketPrices();
        for (MarketPrice marketPrice : marketPrices) {
            priceMap.put(marketPrice.getSymbol(), marketPrice);
        }
    }
}