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
package org.archfirst.bfexch.domain.trading;

import java.util.List;

import javax.inject.Inject;

import org.archfirst.bfexch.domain.marketdata.MarketDataEventPublisher;
import org.archfirst.bfexch.domain.marketdata.MarketDataRepository;
import org.archfirst.bfexch.domain.marketdata.MarketPrice;
import org.archfirst.bfexch.domain.marketdata.MarketPriceChanged;
import org.archfirst.bfexch.domain.trading.order.Execution;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.domain.trading.order.OrderAccepted;
import org.archfirst.bfexch.domain.trading.order.OrderEventPublisher;
import org.archfirst.bfexch.domain.trading.order.OrderExecuted;
import org.archfirst.bfexch.domain.trading.order.OrderRepository;
import org.archfirst.bfexch.domain.trading.order.OrderStatus;
import org.archfirst.bfexch.domain.trading.order.OrderType;
import org.archfirst.bfexch.domain.util.Constants;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MatchingEngine
 *
 * @author Naresh Bhatia
 */
public class MatchingEngine {
    private static final Logger logger =
        LoggerFactory.getLogger(MatchingEngine.class);
    
    // ----- Commands -----
    public void placeOrder(Order order) {
        this.acceptOrder(order);
        this.performMatching(order.getSymbol());
    }
    
    private void performMatching(String symbol) {
        logger.debug("Pricing engine triggered for symbol {}", symbol);
        MarketPrice marketPrice =
            marketDataRepository.findMarketPrice(symbol);
        Money preMatchingPrice = marketPrice.getPrice();
        OrderBook orderBook = getOrderBook(symbol);

        // Iterate through buy orders
        for (Order buyOrder : orderBook.getBuyStack()) {
            logger.debug("Trying to match buy order:\n{}", buyOrder);
            MatchResult matchResult =
                new MatchResult(false, NoMatchReason.priceMismatch);
            
            // Iterate through sell orders to match the current buy order
            for (Order sellOrder : orderBook.getSellStack()) {

                // Skip sell order if it has been filled in a previous iteration
                if (!sellOrder.isActive()) {
                    continue;
                }

                matchResult = matchOrder(buyOrder, sellOrder, marketPrice);

                // Analyze match result and break out of inner loop if appropriate
                if (matchResult.isMatch()) {
                    if (buyOrder.getStatus() == OrderStatus.Filled) {
                        logger.debug("Buy order filled, stop matching with sell orders");
                        break;
                    }
                }
                else { // no match
                    if (matchResult.noMatchReason == NoMatchReason.priceMismatch) {
                        logger.debug("Buy order did not match due to price mismatch, stop matching with sell orders");
                        break;
                    }
                }
            }

            // If buy order did not match due to price mismatch, then break.
            // (No other buy orders will match.)
            if (matchResult.isMatch()==false &&
                matchResult.getNoMatchReason()==NoMatchReason.priceMismatch) {
                logger.debug("Buy order did not match due to price mismatch, stop matching other buy orders");
                break;
            }
        }
        
        // If market price has changed are a result of this run, publish the new price
        if (!marketPrice.getPrice().eq(preMatchingPrice)) {
            marketDataEventPublisher.publish(new MarketPriceChanged(marketPrice));
        }
    }
    
    private MatchResult matchOrder(
            Order buyOrder, Order sellOrder, MarketPrice marketPrice) {

        logger.debug("Before match:\n{}\n{}", buyOrder, sellOrder);
        
        if (isAllOrNoneRestricted(buyOrder, sellOrder)) {
            logger.debug("No match: AllOrNone restriction");
            return new MatchResult(false, NoMatchReason.allOrNone);
        }

        MatchResult matchResult =
            new MatchResult(false, NoMatchReason.priceMismatch);

        if (buyOrder.getType() == OrderType.Market) {
            if (sellOrder.getType() == OrderType.Market) {
                executeOrders(buyOrder, sellOrder, marketPrice.getPrice());
            }
            else {  // sell order is limit order
                executeOrders(buyOrder, sellOrder, sellOrder.getLimitPrice());
                marketPrice.change(sellOrder.getLimitPrice());
            }
            matchResult = new MatchResult(true, null);
        }
        else {  // buy order is a limit order
            if (sellOrder.getType() == OrderType.Market) {
                executeOrders(buyOrder, sellOrder, buyOrder.getLimitPrice());
                marketPrice.change(buyOrder.getLimitPrice());
                matchResult = new MatchResult(true, null);
            }
            else {  // sell order is limit order
                Money buyPrice = buyOrder.getLimitPrice();
                Money sellPrice = sellOrder.getLimitPrice();
                if (buyPrice.compareTo(sellPrice) >= 0) {
                    Money executionPrice =
                        buyPrice.plus(sellPrice).div(2, Constants.PRICE_SCALE);
                    executeOrders(buyOrder, sellOrder, executionPrice);
                    marketPrice.change(executionPrice);
                    matchResult = new MatchResult(true, null);
                }
            }
        }

        logger.debug("After match:\n{}\n{}", buyOrder, sellOrder);
        
        return matchResult;
    }
    
    /**
     * Is the specified match restricted by an AllOrNone condition on one of
     * the orders.
     * @param buyOrder
     * @param sellOrder
     * @return true if match cannot be made due to AllOrNone restriction
     */
    private boolean isAllOrNoneRestricted(Order buyOrder, Order sellOrder) {

        boolean restricted = false;
        
        // Check for buy side restriction
        if (buyOrder.isAllOrNone()) {
            if (buyOrder.getLeavesQty().compareTo(sellOrder.getLeavesQty()) > 0) {
                restricted = true;
            }
        }
        
        // Check for sell side restriction
        if (restricted == false && sellOrder.isAllOrNone()) {
            if (sellOrder.getLeavesQty().compareTo(buyOrder.getLeavesQty()) > 0) {
                restricted = true;
            }
        }
        
        return restricted;
    }
    
    /**
     * Sets order status to New and persists it.
     */
    private void acceptOrder(Order order) {
        order.accept(orderRepository);
        orderEventPublisher.publish(new OrderAccepted(order));
    }
    
    private void executeOrders(Order buyOrder, Order sellOrder, Money price) {
        DecimalQuantity quantity =
            buyOrder.getLeavesQty().min(sellOrder.getLeavesQty());
        DateTime executionTime = new DateTime();
        this.executeOrder(buyOrder, executionTime, quantity, price);
        this.executeOrder(sellOrder, executionTime, quantity, price);
    }
    
    /**
     * Executes the order and adds an execution to it.
     */
    private void executeOrder(
            Order order,
            DateTime executionTime,
            DecimalQuantity executionQty,
            Money price) {
        Execution execution =
            order.execute(orderRepository, executionTime, executionQty, price);
        orderEventPublisher.publish(new OrderExecuted(execution));
    }

    // ----- Queries -----
    public OrderBook getOrderBook(String symbol) {
        OrderBook orderBook = new OrderBook();
        List<Order> orders = orderRepository.findActiveOrdersForInstrument(symbol);
        for (Order order : orders) {
            orderBook.add(order);
        }
        return orderBook;
    }

    // ----- Attributes -----
    @Inject private OrderRepository orderRepository;
    @Inject private OrderEventPublisher orderEventPublisher;
    @Inject private MarketDataRepository marketDataRepository;
    @Inject private MarketDataEventPublisher marketDataEventPublisher;

    // ----- Nested Types -----
    private enum NoMatchReason {
        allOrNone,
        priceMismatch
    }
    
    private class MatchResult {
        private final boolean match;
        private final NoMatchReason noMatchReason;
        
        public MatchResult(boolean match, NoMatchReason noMatchReason) {
            this.match = match;
            this.noMatchReason = noMatchReason;
        }

        public boolean isMatch() {
            return match;
        }
        public NoMatchReason getNoMatchReason() {
            return noMatchReason;
        }
    }
}