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
package org.archfirst.bfoms.spec.accounts;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.archfirst.bfoms.domain.account.BaseAccountService;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccount;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountService;
import org.archfirst.bfoms.domain.account.external.ExternalAccountParams;
import org.archfirst.bfoms.domain.account.external.ExternalAccountService;
import org.archfirst.bfoms.domain.marketdata.MarketDataService;
import org.archfirst.bfoms.domain.security.RegistrationRequest;
import org.archfirst.bfoms.domain.security.SecurityService;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.springtest.AbstractTransactionalSpecTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * BaseAccountsTest
 *
 * @author Naresh
 */
@ContextConfiguration(locations={"classpath:/org/archfirst/bfoms/spec/applicationContext.xml"})
public abstract class BaseAccountsTest extends AbstractTransactionalSpecTest  {

    protected static String FIRST_NAME1 = "John";
    protected static String LAST_NAME1 = "Smith";
    protected static String USERNAME1 = "jsmith";
    protected static String PASSWORD1 = "cool";
    protected static String BROKERAGE_ACCOUNT_NAME1 = "Brokerage Account 1";
    protected static String BROKERAGE_ACCOUNT_NAME2 = "Brokerage Account 2";
    protected static String EXTERNAL_ACCOUNT_NAME1 = "External Account 1";
    protected static String EXTERNAL_ROUTING_NUMBER = "011000123";
    protected static String EXTERNAL_ACCOUNT_NUMBER = "0157-8965-2278";
    
    @Inject protected BaseAccountService baseAccountService;
    @Inject protected BrokerageAccountService brokerageAccountService;
    @Inject protected ExternalAccountService externalAccountService;
    @Inject protected MarketDataService marketDataService;
    @Inject protected SecurityService securityService;
    
    protected Long brokerageAccount1Id;
    protected Long brokerageAccount2Id;
    protected Long externalAccount1Id;
    
    // Map of account name to account id
    private Map<String, Long> accountMap = new HashMap<String, Long>(); 
    
    protected Long getAccountId(String accountName) {
        return accountMap.get(accountName);
    }
    
    protected void createUser1() throws Exception {
        securityService.registerUser(
                new RegistrationRequest(FIRST_NAME1, LAST_NAME1, USERNAME1, PASSWORD1));
    }

    protected void createExternalAccount1() {
        externalAccount1Id = createExternalAccount(EXTERNAL_ACCOUNT_NAME1);
    }
    
    protected void createBrokerageAccount1() {
        brokerageAccount1Id = createBrokerageAccount(BROKERAGE_ACCOUNT_NAME1);
    }

    protected void createBrokerageAccount2() {
        brokerageAccount2Id = createBrokerageAccount(BROKERAGE_ACCOUNT_NAME2);
    }
    
    private Long createExternalAccount(String accountName) {

        // Create the account if it does not exist
        Long accountId = getAccountId(accountName);
        if (accountId == null) {
            ExternalAccountParams params = new ExternalAccountParams(
                    accountName,
                    EXTERNAL_ROUTING_NUMBER,
                    EXTERNAL_ACCOUNT_NUMBER);
            accountId = externalAccountService.addExternalAccount(
                    USERNAME1, params);
            accountMap.put(accountName, accountId);
        }
        
        return accountId;
    }
    
    public void setUpExternalAccount(String accountName) {
        createExternalAccount(accountName);
    }
    
    private Long createBrokerageAccount(String accountName) {

        // Create the account if it does not exist
        Long accountId = getAccountId(accountName);
        if (accountId == null) {
            accountId = brokerageAccountService.openNewAccount(
                    USERNAME1, accountName);
            accountMap.put(accountName, accountId);
        }
        
        return accountId;
    }
    
    public void setUpBrokerageAccount(String accountName, BigDecimal cashPosition) {
        
        createBrokerageAccount(accountName);
        
        // Adjust cash position
        BigDecimal currentCashPosition = getCashPosition(accountName).getAmount();
        if (currentCashPosition != cashPosition) {
            transferCash(
                    EXTERNAL_ACCOUNT_NAME1,
                    accountName,
                    cashPosition.subtract(currentCashPosition));
        }
    }
    
    public void setUpBrokerageAccount(String accountName, String symbol, BigDecimal position) {
        
        createBrokerageAccount(accountName);
        
        // Adjust securities position
        BigDecimal currentPosition = getSecuritiesPosition(accountName, symbol);
        if (currentPosition != position) {
            transferSecurities(
                    EXTERNAL_ACCOUNT_NAME1,
                    accountName,
                    symbol,
                    position.subtract(currentPosition));
        }
    }
    
    public Money getCashPosition(String accountName) {
        Long accountId = getAccountId(accountName);
        BrokerageAccount account =
            brokerageAccountService.findAccount(accountId);
        return account.getCashPosition();
    }
    
    public BigDecimal getSecuritiesPosition(String accountName, String symbol) {
        Long accountId = getAccountId(accountName);
        return brokerageAccountService.getNumberOfShares(accountId, symbol).getValue();
    }
    
    public void transferCash(String accountName1, String accountName2, BigDecimal amount) {
        this.baseAccountService.transferCash(
                USERNAME1,
                new Money(amount),
                getAccountId(accountName1),
                getAccountId(accountName2));
    }
    
    public void transferSecurities(String accountName1, String accountName2, String symbol, BigDecimal quantity) {
        this.baseAccountService.transferSecurities(
                USERNAME1,
                symbol,
                new DecimalQuantity(quantity),
                new Money(),
                getAccountId(accountName1),
                getAccountId(accountName2));
    }
}