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
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.archfirst.bfoms.domain.account.TransactionCriteria;
import org.archfirst.bfoms.domain.account.TransactionSummary;
import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountSummary;
import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderCriteria;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderEstimate;
import org.archfirst.bfoms.domain.account.brokerage.order.OrderParams;
import org.archfirst.bfoms.domain.account.external.ExternalAccountParams;
import org.archfirst.bfoms.domain.account.external.ExternalAccountSummary;
import org.archfirst.common.money.Money;

/**
 * TradingWebService
 *
 * @author Naresh Bhatia
 */
@WebService(targetNamespace = "http://archfirst.org/bfoms/tradingservice.wsdl", serviceName = "TradingService")
@HandlerChain(file = "handler-chain.xml")
public class TradingWebService {

    // ----- Commands -----
    @WebMethod(operationName = "OpenNewAccount", action = "OpenNewAccount")
    @WebResult(name = "AccountId")
    public Long openNewAccount(
            @WebParam(name = "AccountName")
            String accountName) {
        return tradingTxnService.openNewAccount(getUsername(), accountName);
    }

    @WebMethod(operationName = "AddExternalAccount", action = "AddExternalAccount")
    @WebResult(name = "AccountId")
    public Long addExternalAccount(
            @WebParam(name = "ExternalAccountParams")
            ExternalAccountParams params) {
        return tradingTxnService.addExternalAccount(getUsername(), params);
    }

    @WebMethod(operationName = "ChangeAccountName", action = "ChangeAccountName")
    public void changeAccountName(
            @WebParam(name = "AccountId")
            Long accountId,
            @WebParam(name = "NewName")
            String newName) {
        tradingTxnService.changeAccountName(accountId, newName);
    }
    
    @WebMethod(operationName = "TransferCash", action = "TransferCash")
    public void transferCash(
            @WebParam(name = "Amount")
            Money amount,
            @WebParam(name = "FromAccountId")
            Long fromAccountId,
            @WebParam(name = "ToAccountId")
            Long toAccountId) {
        tradingTxnService.transferCash(getUsername(), amount, fromAccountId, toAccountId);
    }

    @WebMethod(operationName = "TransferSecurities", action = "TransferSecurities")
    public void transferSecurities(
            @WebParam(name = "Symbol")
            String symbol,
            @WebParam(name = "Quantity")
            BigDecimal quantity,
            @WebParam(name = "PricePaidPerShare")
            Money pricePaidPerShare,
            @WebParam(name = "FromAccountId")
            Long fromAccountId,
            @WebParam(name = "ToAccountId")
            Long toAccountId) {
        tradingTxnService.transferSecurities(
                getUsername(), symbol, quantity, pricePaidPerShare,
                fromAccountId, toAccountId);
    }

    @WebMethod(operationName = "PlaceOrder", action = "PlaceOrder")
    @WebResult(name = "OrderId")
    public Long placeOrder(
            @WebParam(name = "BrokerageAccountId")
            Long brokerageAccountId,
            @WebParam(name = "OrderParams")
            OrderParams orderParams) {
        return tradingTxnService.placeOrder(
                getUsername(), brokerageAccountId, orderParams);
    }
    
    @WebMethod(operationName = "CancelOrder", action = "CancelOrder")
    public void cancelOrder(
            @WebParam(name = "OrderId")
            Long orderId) {
        tradingTxnService.cancelOrder(getUsername(), orderId);
    }
    
    // ----- Queries -----
    @WebMethod(operationName = "GetBrokerageAccountSummaries", action = "GetBrokerageAccountSummaries")
    @WebResult(name = "BrokerageAccountSummary")
    public List<BrokerageAccountSummary> getBrokerageAccountSummaries() {
        return this.tradingTxnService.getBrokerageAccountSummaries(getUsername());
    }

    @WebMethod(operationName = "GetExternalAccountSummaries", action = "GetExternalAccountSummaries")
    @WebResult(name = "ExternalAccountSummary")
    public List<ExternalAccountSummary> getExternalAccountSummaries() {
        return this.tradingTxnService.getExternalAccountSummaries(getUsername());
    }

    @WebMethod(operationName = "GetOrders", action = "GetOrders")
    @WebResult(name = "Order")
    public List<Order> getOrders(
        @WebParam(name = "OrderCriteria")
        OrderCriteria criteria) {
        return this.tradingTxnService.getOrders(getUsername(), criteria);
    }

    @WebMethod(operationName = "GetOrderEstimate", action = "GetOrderEstimate")
    @WebResult(name = "OrderEstimate")
    public OrderEstimate getOrderEstimate(
            @WebParam(name = "BrokerageAccountId")
            Long brokerageAccountId,
            @WebParam(name = "OrderParams")
            OrderParams orderParams) {
        return tradingTxnService.getOrderEstimate(
                getUsername(), brokerageAccountId, orderParams);
    }

    @WebMethod(operationName = "GetTransactionSummaries", action = "GetTransactionSummaries")
    @WebResult(name = "TransactionSummary")
    public List<TransactionSummary> getTransactionSummaries(
            @WebParam(name = "TransactionCriteria")
            TransactionCriteria criteria) {
        return tradingTxnService.getTransactionSummaries(getUsername(), criteria);
    }

    private String getUsername() {
        MessageContext msgContext = wsContext.getMessageContext();
        @SuppressWarnings("unchecked")
        Map<String, List<String>> http_headers =
            (Map<String, List<String>>)msgContext.get(MessageContext.HTTP_REQUEST_HEADERS);
        List<String> usernameList = http_headers.get(USERNAME_KEY);
        return usernameList.get(0);
    }

    // ----- Attributes -----
    private static final String USERNAME_KEY = "username";
    
    @Inject private TradingTxnService tradingTxnService;
    @Resource private WebServiceContext wsContext;
}