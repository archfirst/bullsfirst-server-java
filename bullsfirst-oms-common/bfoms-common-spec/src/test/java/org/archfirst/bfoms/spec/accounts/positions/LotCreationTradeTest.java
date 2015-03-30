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
package org.archfirst.bfoms.spec.accounts.positions;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.archfirst.bfoms.domain.account.brokerage.Lot;
import org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReport;
import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderParams;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderSide;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderStatus;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderTerm;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderType;
import org.archfirst.bfoms.domain.marketdata.MarketPrice;
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;

/**
 * LotCreationTradeTest
 *
 * @author Naresh Bhatia
 */
public class LotCreationTradeTest extends BaseAccountsTest {
    
    public void setup() throws Exception {
        this.createUser1();
        this.createBrokerageAccount1();
        this.createExternalAccount1();
        
        // Add cash to brokerage account so we can buy securities
        this.baseAccountService.transferCash(
                USERNAME1, new Money("10000"),
                externalAccount1Id, brokerageAccount1Id);
    }
    
    public List<Lot> buy(String symbol, BigDecimal quantity, BigDecimal pricePaidPerShare) {
        
        // Make sure placeOrder() can get a market price for the symbol 
        marketDataService.updateMarketPrice(
                new MarketPrice(symbol, new Money("10"), new DateTime()));

        // Place the order
        OrderParams orderParams = new OrderParams(
                OrderSide.Buy,
                symbol,
                quantity,
                OrderType.Market,
                null,
                OrderTerm.GoodTilCanceled,
                false);
        Long orderId = this.brokerageAccountService.placeOrder(
                USERNAME1, brokerageAccount1Id, orderParams);
        Order order = this.brokerageAccountService.findOrder(orderId);
        
        // Acknowledge the order
        ExecutionReport executionReport = ExecutionReport.createNewType(order);
        this.brokerageAccountService.processExecutionReport(executionReport);
        
        // Execute the trade
        executionReport = ExecutionReport.createTradeType(
                order,
                OrderStatus.Filled,
                new DecimalQuantity(quantity),
                new Money(pricePaidPerShare));
        this.brokerageAccountService.processExecutionReport(executionReport);
        
        List<Lot> lots =
            this.brokerageAccountService.findActiveLots(brokerageAccount1Id);
        Collections.sort(lots, new Lot.CreationTimeComparator());
        
        return lots;
    }
}