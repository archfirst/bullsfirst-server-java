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

import java.util.List;

import org.archfirst.common.domain.BaseRepository;

/**
 * OrderRepository
 *
 * @author Naresh Bhatia
 */
public class OrderRepository extends BaseRepository {

    public Order findOrderByClientOrderId(String clientOrderId) {
        Order order = (Order)entityManager.createQuery(
                "select o from Order o " +
                "where o.clientOrderId = :clientOrderId")
            .setParameter("clientOrderId", clientOrderId)
            .getSingleResult();
        return order;
    }

    public List<Order> findActiveGfdOrders() {
        @SuppressWarnings("unchecked")
        List<Order> orders = entityManager.createQuery(
                "select ord from Order ord " +
                "where ord.status in ('NEW', 'PARTFILD') " +
                "and ord.term = 'GFD'")
            .getResultList();
        return orders;
    }

    public List<Order> findActiveOrdersForInstrument(String symbol) {
        @SuppressWarnings("unchecked")
        List<Order> orders = entityManager.createQuery(
                "select ord from Order ord " +
                "where symbol = :symbol " +
                "and ord.status in ('NEW', 'PARTFILD')")
            .setParameter("symbol", symbol)
            .getResultList();
        return orders;
    }
}