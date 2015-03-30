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
package org.archfirst.bfoms.spec.accounts.transfers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archfirst.bfoms.domain.account.SecuritiesTransfer;
import org.archfirst.bfoms.domain.account.Transaction;
import org.archfirst.bfoms.domain.account.TransactionCriteria;
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.quantity.DecimalQuantity;

/**
 * SecuritiesTransfersTest
 *
 * @author Naresh Bhatia
 */
public class SecuritiesTransfersTest extends BaseAccountsTest {
    
    private Map<Long, Transfer> transferHistory = new HashMap<Long, Transfer>(); 
    
    public void setUp() throws Exception {
        this.createUser1();
        this.createExternalAccount1();
    }
    
    /**
     * @return transfers since the last time this method was called
     */
    public List<Transfer> recordedTransfers() {
        
        List<Transaction> transactions =
            baseAccountService.findTransactions(new TransactionCriteria());

        // Find new transfers
        List<Transfer> newTransfers = new ArrayList<Transfer>();
        for (Transaction transaction : transactions) {
            if (transferHistory.get(transaction.getId()) == null) {
                if (transaction.getClass().equals(SecuritiesTransfer.class)) {
                    SecuritiesTransfer securitiesTransfer = (SecuritiesTransfer)transaction;
                    Transfer transfer = new Transfer(
                            securitiesTransfer.getId(),
                            securitiesTransfer.getAccount().getName(),
                            securitiesTransfer.getSymbol(),
                            securitiesTransfer.getQuantity());
                    transferHistory.put(transfer.id, transfer);
                    newTransfers.add(transfer);
                }
            }
        }
        
        Collections.sort(newTransfers);
        return newTransfers;
    }
    
    public class Transfer implements Comparable<Transfer> {
        public Long id;
        public String accountName;
        public String symbol;
        public DecimalQuantity quantity;

        public Transfer(Long id, String accountName, String symbol, DecimalQuantity quantity) {
            this.id = id;
            this.accountName = accountName;
            this.symbol = symbol;
            this.quantity = quantity;
        }

        @Override
        public int compareTo(Transfer other) {
            return this.id.compareTo(other.id);
        }
    }
}