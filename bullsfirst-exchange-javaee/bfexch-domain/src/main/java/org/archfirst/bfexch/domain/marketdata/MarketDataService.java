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
package org.archfirst.bfexch.domain.marketdata;

import java.util.List;

import javax.inject.Inject;

import org.archfirst.common.money.Money;
import org.joda.time.DateTime;

/**
 * MarketDataService
 *
 * @author Naresh Bhatia
 */
public class MarketDataService {
    
    // ----- Commands -----
    public void createMarketPrice(String symbol, Money price) {
        marketDataRepository.persist(
                new MarketPrice(symbol, price, new DateTime()));
    }

    public void changeMarketPrice(String symbol, Money price) {
        MarketPrice marketPrice = marketDataRepository.findMarketPrice(symbol);
        marketPrice.change(price);
    }

    // ----- Queries -----
    public List<MarketPrice> getMarketPrices() {
        return marketDataRepository.findAllMarketPrices();
    }

    public MarketPrice getMarketPrice(String symbol) {
        return marketDataRepository.findMarketPrice(symbol);
    }
    
    // ----- Attributes -----
    @Inject private MarketDataRepository marketDataRepository;
}