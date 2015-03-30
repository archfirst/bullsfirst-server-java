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
package org.archfirst.bfexch.spec.mocks;

import java.util.ArrayList;
import java.util.List;

import org.archfirst.bfexch.domain.trading.order.ExecutionReport;
import org.archfirst.bfexch.domain.trading.order.OrderAccepted;
import org.archfirst.bfexch.domain.trading.order.OrderCancelRejected;
import org.archfirst.bfexch.domain.trading.order.OrderCanceled;
import org.archfirst.bfexch.domain.trading.order.OrderDoneForDay;
import org.archfirst.bfexch.domain.trading.order.OrderEventPublisher;
import org.archfirst.bfexch.domain.trading.order.OrderExecuted;

/**
 * MockBrokerAdapter
 *
 * @author Naresh Bhatia
 */
public class OrderEventRecorder implements OrderEventPublisher {

    private final List<ExecutionReport> executionReports =
        new ArrayList<ExecutionReport>();

    public List<ExecutionReport> getExecutionReports() {
        return executionReports;
    }
    
    public void clear() {
        executionReports.clear();
    }

    @Override
    public void publish(OrderAccepted event) {
        executionReports.add(ExecutionReport.createNewType(event.getOrder()));
    }

    @Override
    public void publish(OrderExecuted event) {
        executionReports.add(ExecutionReport.createTradeType(event.getExecution()));
    }

    @Override
    public void publish(OrderCanceled event) {
    }

    @Override
    public void publish(OrderCancelRejected event) {
    }

    @Override
    public void publish(OrderDoneForDay event) {
    }
}