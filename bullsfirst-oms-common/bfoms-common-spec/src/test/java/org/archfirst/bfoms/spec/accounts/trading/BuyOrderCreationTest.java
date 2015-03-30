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
package org.archfirst.bfoms.spec.accounts.trading;

import java.math.BigDecimal;

import org.archfirst.bfoms.domain.account.brokerage.order.OrderCompliance;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderEstimate;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderParams;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderTerm;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderType;
import org.archfirst.bfoms.domain.marketdata.MarketPrice;
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;

/**
 * Buy Order Creation Test
 *
 * @author Naresh Bhatia
 */
public class BuyOrderCreationTest extends BaseAccountsTest {
    
    public void setup() throws Exception {
        this.createUser1();
        this.createBrokerageAccount1();
        this.createExternalAccount1();
    }
    
    public void initCashPosition(BigDecimal amount) throws Exception {
        this.baseAccountService.transferCash(
                USERNAME1, new Money(amount),
                externalAccount1Id, brokerageAccount1Id);
    }
    
    public void setMarketPrice(String symbol, BigDecimal price) {
        marketDataService.updateMarketPrice(
                new MarketPrice(symbol, new Money(price), new DateTime()));
    }
    
    public String buy(String symbol, BigDecimal quantity) {

        // Get order estimate
        OrderParams orderParams = new OrderParams(
                OrderSide.Buy,
                symbol,
                quantity,
                OrderType.Market,
                null,
                OrderTerm.GoodTilCanceled,
                false);
        OrderEstimate estimate = this.brokerageAccountService.getOrderEstimate(
                USERNAME1, brokerageAccount1Id, orderParams);
        
        return (estimate.getCompliance() == OrderCompliance.Compliant) ?
                "accepted" : "rejected";
    }
}