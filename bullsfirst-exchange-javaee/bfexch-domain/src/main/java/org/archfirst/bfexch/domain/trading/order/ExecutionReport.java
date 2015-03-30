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
package org.archfirst.bfexch.domain.trading.order;

import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;

/**
 * ExecutionReport
 *
 * @author Naresh Bhatia
 */
public class ExecutionReport {

    // ----- Constructors -----
    /**
     * Constructs an ExecutionReport
     * 
     * @param type
     * @param order
     * @param execution is optional. For example, an execution report of type
     * New simply acknowledges a new order - there is no execution associated
     * with this report.
     */
    private ExecutionReport(
            ExecutionReportType type,
            Order order,
            Execution execution) {
        this.type = type;
        this.orderId = order.getId().toString();
        this.executionId = (execution == null) ? null : execution.getId().toString();
        this.clientOrderId = order.getClientOrderId();
        this.orderStatus = order.getStatus();
        this.side = order.getSide();
        this.symbol = order.getSymbol();
        this.lastQty = (execution == null) ? null : execution.getQuantity();
        this.leavesQty = order.getLeavesQty();
        this.cumQty = order.getCumQty();
        this.lastPrice = (execution == null) ? null : execution.getPrice();
        this.weightedAvgPrice = order.getWeightedAveragePriceOfExecutions();
    }
    
    // ----- Factory Methods -----
    public static ExecutionReport createNewType(Order order) {
        return new ExecutionReport(
                ExecutionReportType.New,
                order,
                null);
    }
    
    public static ExecutionReport createTradeType(Execution execution) {
        return new ExecutionReport(
                ExecutionReportType.Trade,
                execution.getOrder(),
                execution);
    }
    
    public static ExecutionReport createCanceledType(Order order) {
        return new ExecutionReport(
                ExecutionReportType.Canceled,
                order,
                null);
    }

    public static ExecutionReport createDoneForDayType(Order order) {
        return new ExecutionReport(
                ExecutionReportType.DoneForDay,
                order,
                null);
    }

    // ----- Attributes -----
    private final ExecutionReportType type;
    private final String orderId;
    private final String executionId;
    private final String clientOrderId;
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
    public String getOrderId() {
        return orderId;
    }
    public String getExecutionId() {
        return executionId;
    }
    public String getClientOrderId() {
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