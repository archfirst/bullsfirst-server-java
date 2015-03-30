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
package org.archfirst.bfexch.webservice.marketdata;

import java.util.List;

import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.archfirst.bfexch.domain.marketdata.MarketDataService;
import org.archfirst.bfexch.domain.marketdata.MarketPrice;

/**
 * MarketDataWebService
 *
 * @author Naresh Bhatia
 */
@WebService(targetNamespace = "http://archfirst.org/bfexch/marketdataservice.wsdl", serviceName = "MarketDataService")
public class MarketDataWebService {

    // ----- Queries -----
    @WebMethod(operationName = "GetMarketPrices", action = "GetMarketPrices")
    @WebResult(name = "MarketPrice")
    public List<MarketPrice> getMarketPrices() {
        return marketDataService.getMarketPrices();
    }

    @WebMethod(operationName = "GetMarketPrice", action = "GetMarketPrice")
    @WebResult(name = "MarketPrice")
    public MarketPrice getMarketPrice(
            @WebParam(name = "Symbol")
            String symbol) {
        return marketDataService.getMarketPrice(symbol);
    }
    
    // ----- Attributes -----
    @Inject
    private MarketDataService marketDataService;
}