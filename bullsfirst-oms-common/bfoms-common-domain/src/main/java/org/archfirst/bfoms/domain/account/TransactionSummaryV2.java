/**
 * Copyright 2012 Archfirst
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
package org.archfirst.bfoms.domain.account;

/**
 * Adds account name to TransactionSummary.
 * Not adding any JAXB markup, since this is used only by the REST service
 *
 * @author Naresh Bhatia
 */
public class TransactionSummaryV2 extends TransactionSummary {

    // ----- Constructors -----
    public TransactionSummaryV2() {
    }
    
    public TransactionSummaryV2(Transaction transaction) {
        super(transaction);
        this.accountName = transaction.getAccount().getName();
    }

    // ----- Attributes -----
    private String accountName;

    // ----- Getters and Setters -----
    public String getAccountName() {
        return accountName;
    }
}