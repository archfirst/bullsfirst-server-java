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
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;

/**
 * LotCreationTransferTest
 *
 * @author Naresh Bhatia
 */
public class LotCreationTransferTest extends BaseAccountsTest {
    
    public void setup() throws Exception {
        this.createUser1();
        this.createBrokerageAccount1();
        this.createExternalAccount1();
    }
    
    public List<Lot> transfer(String symbol, BigDecimal quantity) {
        this.baseAccountService.transferSecurities(
                USERNAME1, symbol, new DecimalQuantity(quantity), new Money(),
                externalAccount1Id, brokerageAccount1Id);
        
        List<Lot> lots =
            this.brokerageAccountService.findActiveLots(brokerageAccount1Id);
        Collections.sort(lots, new Lot.CreationTimeComparator());
        
        return lots;
    }
}