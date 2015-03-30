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

import org.archfirst.bfoms.domain.account.CashTransfer;
import org.archfirst.bfoms.domain.account.Transaction;
import org.archfirst.bfoms.domain.account.TransactionCriteria;
import org.archfirst.bfoms.spec.accounts.BaseAccountsTest;
import org.archfirst.common.money.Money;

/**
 * CashTransfersTest
 *
 * @author Naresh Bhatia
 */
public class CashTransfersTest extends BaseAccountsTest {
    
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
                if (transaction.getClass().equals(CashTransfer.class)) {
                    CashTransfer cashTransfer = (CashTransfer)transaction;
                    Transfer transfer = new Transfer(
                            cashTransfer.getId(),
                            cashTransfer.getAccount().getName(),
                            cashTransfer.getAmount());
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
        public Money amount;

        public Transfer(Long id, String accountName, Money amount) {
            this.id = id;
            this.accountName = accountName;
            this.amount = amount;
        }

        @Override
        public int compareTo(Transfer other) {
            return this.id.compareTo(other.id);
        }
    }
}