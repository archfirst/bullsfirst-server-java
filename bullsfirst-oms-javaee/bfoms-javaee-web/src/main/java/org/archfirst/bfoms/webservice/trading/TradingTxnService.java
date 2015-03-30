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
package org.archfirst.bfoms.webservice.trading;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.archfirst.bfoms.domain.account.BaseAccountService;
import org.archfirst.bfoms.domain.account.TransactionCriteria;
import org.archfirst.bfoms.domain.account.TransactionSummary;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountService;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountSummary;
import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderCriteria;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderEstimate;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderParams;
import org.archfirst.bfoms.domain.account.external.ExternalAccountParams;
import org.archfirst.bfoms.domain.account.external.ExternalAccountService;
import org.archfirst.bfoms.domain.account.external.ExternalAccountSummary;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;

/**
 * TradingTxnService
 *
 * @author Naresh Bhatia
 */
@Stateless
public class TradingTxnService {
    
    // ----- Commands -----
    public Long openNewAccount(String username, String accountName) {
        return brokerageAccountService.openNewAccount(username, accountName);
    }

    public Long addExternalAccount(String username, ExternalAccountParams params) {
        return externalAccountService.addExternalAccount(username, params);
    }

    public void changeAccountName(Long accountId, String newName) {
        baseAccountService.changeAccountName(accountId, newName);
    }
    
    public void transferCash(
            String username,
            Money amount,
            Long fromAccountId,
            Long toAccountId) {
        baseAccountService.transferCash(
                username, amount, fromAccountId, toAccountId);
    }
        
    public void transferSecurities(
            String username,
            String symbol,
            BigDecimal quantity,
            Money pricePaidPerShare,
            Long fromAccountId,
            Long toAccountId) {
        baseAccountService.transferSecurities(
                username,
                symbol,
                new DecimalQuantity(quantity),
                pricePaidPerShare,
                fromAccountId,
                toAccountId);
    }
        
    public Long placeOrder(String username, Long brokerageAccountId, OrderParams params) {
        return this.brokerageAccountService.placeOrder(
                username, brokerageAccountId, params);
    }
    
    public void cancelOrder(String username, Long orderId) {
        this.brokerageAccountService.cancelOrder(username, orderId);
    }
    
    // ----- Queries -----
    public List<BrokerageAccountSummary> getBrokerageAccountSummaries(String username) {
        return this.brokerageAccountService.getAccountSummaries(username);
    }

    public List<ExternalAccountSummary> getExternalAccountSummaries(String username) {
        return this.externalAccountService.getAccountSummaries(username);
    }

    public List<Order> getOrders(String username, OrderCriteria criteria) {
        return this.brokerageAccountService.getOrders(username, criteria);
    }

    public OrderEstimate getOrderEstimate(
            String username,
            Long brokerageAccountId,
            OrderParams params) {
        return this.brokerageAccountService.getOrderEstimate(
                username, brokerageAccountId, params);
    }

    public List<TransactionSummary> getTransactionSummaries(
            String username, TransactionCriteria criteria) {
        return this.baseAccountService.getTransactionSummaries(username, criteria);
    }
    
    // ----- Attributes -----
    @Inject private BaseAccountService baseAccountService;
    @Inject private BrokerageAccountService brokerageAccountService;
    @Inject private ExternalAccountService externalAccountService;
}