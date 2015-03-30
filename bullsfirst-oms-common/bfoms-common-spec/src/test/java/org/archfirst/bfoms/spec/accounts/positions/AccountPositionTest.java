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

import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountSummary;
import org.archfirst.bfoms.domain.account.brokerage.Position;
import org.archfirst.bfoms.domain.marketdata.MarketPrice;
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;

/**
 * AccountPositionTest
 *
 * @author Naresh Bhatia
 */
public class AccountPositionTest extends BaseAccountsTest {
    
    private BrokerageAccountSummary accountSummary;
    
    public void setup() throws Exception {
        this.createUser1();
        this.createBrokerageAccount1();
        this.createExternalAccount1();
    }
    
    public void transferCash(BigDecimal amount) {
        this.baseAccountService.transferCash(
                USERNAME1, new Money(amount),
                externalAccount1Id, brokerageAccount1Id);
    }

    public void transferSecurities(String symbol, BigDecimal quantity, BigDecimal price) {
        this.baseAccountService.transferSecurities(
                USERNAME1, symbol,
                new DecimalQuantity(quantity), new Money(price),
                externalAccount1Id, brokerageAccount1Id);
    }
    
    public void setMarketPrice(String symbol, BigDecimal price) {
        marketDataService.updateMarketPrice(
                new MarketPrice(symbol, new Money(price), new DateTime()));
    }
    
    public List<Position> getPositions() {
        accountSummary = this.brokerageAccountService.getAccountSummary(
                USERNAME1, brokerageAccount1Id);
        List<Position> positions = accountSummary.getPositions(); 
        Collections.sort(positions, new Position.LotCreationTimeComparator());
        return positions;
    }
    
    public Money getMarketValue() {
        return accountSummary.getMarketValue();
    }
}