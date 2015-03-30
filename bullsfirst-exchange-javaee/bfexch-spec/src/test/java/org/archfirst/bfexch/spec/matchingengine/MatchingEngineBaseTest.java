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
package org.archfirst.bfexch.spec.matchingengine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.archfirst.bfexch.domain.marketdata.MarketDataService;
import org.archfirst.bfexch.domain.marketdata.MarketPrice;
import org.archfirst.bfexch.domain.trading.MatchingEngine;
import org.archfirst.bfexch.domain.trading.order.ExecutionReport;
import org.archfirst.bfexch.domain.trading.order.ExecutionReportType;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.domain.trading.order.OrderSide;
import org.archfirst.bfexch.domain.trading.order.OrderStatus;
import org.archfirst.bfexch.domain.trading.order.OrderTerm;
import org.archfirst.bfexch.domain.trading.order.OrderType;
import org.archfirst.bfexch.spec.mocks.OrderEventRecorder;
import org.archfirst.common.datetime.DateTimeUtil;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.archfirst.common.springtest.AbstractTransactionalSpecTest;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;

/**
 * MatchingEngineBaseTest
 *
 * @author Naresh Bhatia
 */
@ContextConfiguration(locations={"classpath:/org/archfirst/bfexch/spec/applicationContext.xml"})
public abstract class MatchingEngineBaseTest extends AbstractTransactionalSpecTest {

    @Inject private MarketDataService marketDataService;
    @Inject private MatchingEngine matchingEngine;
    @Inject protected OrderEventRecorder eventRecorder;
    
    @BeforeMethod
    public void setup() {
        eventRecorder.clear();
    }
    
    public void setupSymbol(String symbol) {
        marketDataService.createMarketPrice(symbol, new Money("10.00"));
    }

    public void createMarketPrice(String symbol, String price) {
        marketDataService.createMarketPrice(symbol, new Money(price));
    }

    public void placeOrder(
            String creationTime,
            String clientOrderId,
            String side,
            String symbol,
            BigDecimal quantity,
            String type,
            String limitPrice,
            String gtc,
            String allOrNone) {
        
        Order order = new Order(
            DateTimeUtil.parseDateTimeSecond(creationTime),
            clientOrderId,
            OrderSide.valueOf(side),
            symbol,
            new DecimalQuantity(quantity),
            OrderType.valueOf(type),
            (StringUtils.isEmpty(limitPrice)) ? null : new Money(limitPrice),
            (StringUtils.equalsIgnoreCase(gtc, "Y")) ?
                    OrderTerm.GoodTilCanceled : OrderTerm.GoodForTheDay,
            (StringUtils.equalsIgnoreCase(allOrNone, "Y")) ? true : false);
        
        matchingEngine.placeOrder(order);
    }

    public void clearExecutionReports() {
        eventRecorder.clear();
    }

    public List<OrderOut> getBuyStack(String symbol) {
        return convertOrders(
                matchingEngine.getOrderBook(symbol).getBuyStack());
    }

    public List<OrderOut> getSellStack(String symbol) {
        return convertOrders(
                matchingEngine.getOrderBook(symbol).getSellStack());
    }
    
    private List<OrderOut> convertOrders(SortedSet<Order> orders) {
        List<OrderOut> result = new ArrayList<OrderOut>();
        for (Order order : orders) {
            result.add(new OrderOut(order));
        }
        return result;
    }

    public List<ExecutionReportOut> getExecutionReports() {
        return convertExecutionReports(eventRecorder.getExecutionReports());
    }
    
    private List<ExecutionReportOut> convertExecutionReports(
            List<ExecutionReport> executionReports) {
        List<ExecutionReportOut> result = new ArrayList<ExecutionReportOut>();
        for (ExecutionReport executionReport : executionReports) {
            result.add(new ExecutionReportOut(executionReport));
        }
        return result;
    }
    
    public int getNumberOfFills() {
        int numberOfFills = 0;
        List<ExecutionReport> executionReports =
            eventRecorder.getExecutionReports();
        for (ExecutionReport executionReport : executionReports) {
            if (executionReport.getType() == ExecutionReportType.Trade) {
                if (executionReport.getOrderStatus() == OrderStatus.Filled ||
                    executionReport.getOrderStatus() == OrderStatus.PartiallyFilled) {
                    numberOfFills++;
                }
            }
        }
        return numberOfFills;
    }
    
    public BigDecimal getMarketPrice(String symbol) {
        MarketPrice marketPrice = marketDataService.getMarketPrice(symbol);
        return marketPrice.getPrice().getAmount();
    }

    public class OrderOut {
        private final Order order;
        
        public OrderOut(Order order) {
            this.order = order;
        }

        public String getCreationTime() {
            return DateTimeUtil.toStringDateTimeSecond(order.getCreationTime());
        }
        public String getClientOrderId() {
            return order.getClientOrderId();
        }
        public OrderSide getSide() {
            return order.getSide();
        }
        public String getSymbol() {
            return order.getSymbol();
        }
        public DecimalQuantity getQuantity() {
            return order.getQuantity();
        }
        public OrderType getType() {
            return order.getType();
        }
        public String getLimitPrice() {
            return (order.getLimitPrice() == null) ?
                    "" : order.getLimitPrice().getAmount().toString();
        }
        public String getGtc() {
            return (order.getTerm() == OrderTerm.GoodTilCanceled) ? "Y" : "";
        }
        public String getAllOrNone() {
            return order.isAllOrNone() ? "Y" : "";
        }
        public OrderStatus getStatus() {
            return order.getStatus();
        }
    }

    public class ExecutionReportOut {
        private final ExecutionReport executionReport;

        public ExecutionReportOut(ExecutionReport executionReport) {
            this.executionReport = executionReport;
        }
        
        public ExecutionReportType getType() {
            return executionReport.getType();
        }
        public String getOrderId() {
            return executionReport.getOrderId();
        }
        public String getClientOrderId() {
            return executionReport.getClientOrderId();
        }
        public OrderStatus getOrderStatus() {
            return executionReport.getOrderStatus();
        }
        public OrderSide getSide() {
            return executionReport.getSide();
        }
        public String getSymbol() {
            return executionReport.getSymbol();
        }
        public String getLastQty() {
            DecimalQuantity lastQty = executionReport.getLastQty();
            return (lastQty != null) ? lastQty.toString() : "";
        }
        public DecimalQuantity getLeavesQty() {
            return executionReport.getLeavesQty();
        }
        public DecimalQuantity getCumQty() {
            return executionReport.getCumQty();
        }
        public String getLastPrice() {
            Money lastPrice = executionReport.getLastPrice();
            return (lastPrice != null) ? lastPrice.getAmount().toString() : "";
        }
    }
}