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
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;

/**
 * Sell Order Creation Test
 *
 * @author Naresh Bhatia
 */
public class SellOrderCreationTest extends BaseAccountsTest {
    
    public void setup() throws Exception {
        this.createUser1();
        this.createBrokerageAccount1();
        this.createExternalAccount1();
    }
    
    public void initSecurityPosition(String symbol, BigDecimal quantity) throws Exception {
        
        Money pricePerShare = new Money("10");
        
        this.baseAccountService.transferSecurities(
                USERNAME1, symbol, new DecimalQuantity(quantity), pricePerShare,
                externalAccount1Id, brokerageAccount1Id);

        // getOrderEstimate() requires a market price to calculate sale amount
        marketDataService.updateMarketPrice(
                new MarketPrice(symbol, pricePerShare, new DateTime()));
    }
    
    public String sell(String symbol, BigDecimal quantity) {

        // Get order estimate
        OrderParams orderParams = new OrderParams(
                OrderSide.Sell,
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