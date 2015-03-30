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
import java.util.List;

import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderCriteria;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderParams;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderTerm;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderType;
import org.archfirst.bfoms.domain.marketdata.MarketPrice;
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;
import org.testng.Assert;

/**
 * Search Orders By SymbolTest
 *
 * @author Naresh Bhatia
 */
public class SearchOrdersBySymbolTest extends BaseAccountsTest {
    
    public void setup() throws Exception {
        this.createUser1();
        this.createBrokerageAccount1();
        this.createExternalAccount1();

        this.baseAccountService.transferCash(
                USERNAME1, new Money("10000"),
                externalAccount1Id, brokerageAccount1Id);
    }
    
    public void initOrders(String symbol, int numOrders) {

        // Set a low market price so we have enough funds for buying shares
        marketDataService.updateMarketPrice(
                new MarketPrice(symbol, new Money("1.00"), new DateTime()));

        // Place orders
        for (int orderNumber=0; orderNumber < numOrders; orderNumber++) {
            OrderParams orderParams = new OrderParams(
                    OrderSide.Buy,
                    symbol,
                    new BigDecimal(10),
                    OrderType.Market,
                    null,
                    OrderTerm.GoodTilCanceled,
                    false);
            this.brokerageAccountService.placeOrder(
                    USERNAME1, brokerageAccount1Id, orderParams);
        }
    }
    
    public int findOrders(String symbol) {
        
        // Find orders for symbol
        OrderCriteria criteria = new OrderCriteria();
        criteria.setAccountId(brokerageAccount1Id);
        criteria.setSymbol(symbol);
        List<Order> orders =
            brokerageAccountService.getOrders(USERNAME1, criteria);

        // Make sure all orders are for the symbol
        for (Order order : orders) {
            Assert.assertEquals(order.getSymbol(), symbol);
        }
        
        return orders.size();
    }
}