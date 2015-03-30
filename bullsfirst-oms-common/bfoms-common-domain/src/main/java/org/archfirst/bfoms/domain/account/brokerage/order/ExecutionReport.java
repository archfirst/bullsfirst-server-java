/**
 * Copyright 2010 Archfirst
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
package org.archfirst.bfoms.domain.account.brokerage.order;

import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;

/**
 * ExecutionReport
 *
 * @author Naresh Bhatia
 */
public class ExecutionReport {

    // ----- Constructors -----
    public ExecutionReport(
            ExecutionReportType type,
            String exchangeOrderId,
            String executionId,
            Long clientOrderId,
            OrderStatus orderStatus,
            OrderSide side,
            String symbol,
            DecimalQuantity lastQty,
            DecimalQuantity leavesQty,
            DecimalQuantity cumQty,
            Money lastPrice,
            Money weightedAvgPrice) {
        this.type = type;
        this.exchangeOrderId = exchangeOrderId;
        this.executionId = executionId;
        this.clientOrderId = clientOrderId;
        this.orderStatus = orderStatus;
        this.side = side;
        this.symbol = symbol;
        this.lastQty = lastQty;
        this.leavesQty = leavesQty;
        this.cumQty = cumQty;
        this.lastPrice = lastPrice;
        this.weightedAvgPrice = weightedAvgPrice;
    }

    // ----- Factory Methods -----
    public static ExecutionReport createNewType(
            Order order) {
        return new ExecutionReport(
                ExecutionReportType.New,
                null,
                null,
                order.getId(),
                OrderStatus.New,
                order.getSide(),
                order.getSymbol(),
                null,
                order.getLeavesQty(),
                order.getCumQty(),
                null,
                null);
    }
    
    public static ExecutionReport createTradeType(
            Order order,
            OrderStatus newStatus,
            DecimalQuantity quantity,
            Money price) {
        return new ExecutionReport(
                ExecutionReportType.Trade,
                null,
                null,
                order.getId(),
                newStatus,
                order.getSide(),
                order.getSymbol(),
                quantity,
                order.getLeavesQty().minus(quantity),
                order.getCumQty().plus(quantity),
                price,
                null);
    }
    
    // ----- Queries -----
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("type=").append(type);
        builder.append(", exchangeOrderId=").append(exchangeOrderId);
        builder.append(", executionId=").append(executionId);
        builder.append(", clientOrderId=").append(clientOrderId);
        builder.append(", orderStatus=").append(orderStatus);
        builder.append(", side=").append(side);
        builder.append(", symbol=").append(symbol);
        builder.append(", lastQty=").append(lastQty);
        builder.append(", leavesQty=").append(leavesQty);
        builder.append(", cumQty=").append(cumQty);
        builder.append(", lastPrice=").append(lastPrice);
        builder.append(", weightedAvgPrice=").append(weightedAvgPrice);
        return builder.toString();
    }

    // ----- Attributes -----
    private final ExecutionReportType type;
    private final String exchangeOrderId;
    private final String executionId;
    private final Long clientOrderId;
    private final OrderStatus orderStatus;
    private final OrderSide side;
    private final String symbol;
    private final DecimalQuantity lastQty;
    private final DecimalQuantity leavesQty;
    private final DecimalQuantity cumQty;
    private final Money lastPrice;
    private final Money weightedAvgPrice;

    // ----- Getters -----
    public ExecutionReportType getType() {
        return type;
    }
    public String getExchangeOrderId() {
        return exchangeOrderId;
    }
    public String getExecutionId() {
        return executionId;
    }
    public Long getClientOrderId() {
        return clientOrderId;
    }
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public OrderSide getSide() {
        return side;
    }
    public String getSymbol() {
        return symbol;
    }
    public DecimalQuantity getLastQty() {
        return lastQty;
    }
    public DecimalQuantity getLeavesQty() {
        return leavesQty;
    }
    public DecimalQuantity getCumQty() {
        return cumQty;
    }
    public Money getLastPrice() {
        return lastPrice;
    }
    public Money getWeightedAvgPrice() {
        return weightedAvgPrice;
    }
}