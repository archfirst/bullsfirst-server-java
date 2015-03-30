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

import java.util.SortedSet;
import java.util.TreeSet;

import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.domain.trading.order.OrderSide;

/**
 * Contains active orders for an instrument organized as sorted buy and
 * sell stacks.
 *
 * @author Naresh Bhatia
 */
public class OrderBook {
    
    // ----- Commands -----
    public void add(Order order) {
        if (order.getSide() == OrderSide.Buy) {
            buyStack.add(order);
        }
        else {
            sellStack.add(order);
        }
    }

    // ----- Attributes -----
    private final SortedSet<Order> buyStack = new TreeSet<Order>();
    private final SortedSet<Order> sellStack = new TreeSet<Order>();

    // ----- Getters -----
    public SortedSet<Order> getBuyStack() {
        return buyStack;
    }

    public SortedSet<Order> getSellStack() {
        return sellStack;
    }
}