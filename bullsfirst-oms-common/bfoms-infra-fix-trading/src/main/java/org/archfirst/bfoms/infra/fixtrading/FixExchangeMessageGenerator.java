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
package org.archfirst.bfoms.infra.fixtrading;

import java.util.Date;

import javax.inject.Inject;

import org.archfirst.bfoms.domain.account.brokerage.order.Order;
import org.archfirst.bfoms.domain.exchange.ExchangeMessageGenerator;
import org.archfirst.bfoms.infra.app.ConfigConstants;
import org.archfirst.bfoms.infra.fixtrading.converters.ClOrdIDConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.FixFormatter;
import org.archfirst.bfoms.infra.fixtrading.converters.InstrumentConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.MoneyConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrderQuantityConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrderSideConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrderTermConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrderTypeConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrigClOrdIDConverter;
import org.archfirst.common.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.field.ExecInst;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelRequest;

/**
 * FixExchangeMessageGenerator
 *
 * @author Naresh Bhatia
 */
public class FixExchangeMessageGenerator implements ExchangeMessageGenerator {
    private static final Logger logger =
        LoggerFactory.getLogger(FixExchangeMessageGenerator.class);

    @Inject private ConfigurationService configurationService;
    
    @Override
    public String generateNewOrderSingleMessage(Order order) {

        logger.debug("Generating NewOrderSingle: {}", order);

        NewOrderSingle fixMessage = new NewOrderSingle(
                ClOrdIDConverter.toFix(getBrokerId(), order.getId()),
                OrderSideConverter.toFix(order.getSide()),
                new TransactTime(order.getCreationTime().toDate()),
                OrderTypeConverter.toFix(order.getType()));

        fixMessage.set(InstrumentConverter.toFix(order.getSymbol()));
        fixMessage.set(OrderQuantityConverter.toFix(order.getQuantity()));
        if (order.getLimitPrice() != null) {
            fixMessage.set(MoneyConverter.toFixPrice(order.getLimitPrice()));
            fixMessage.set(MoneyConverter.toFixCurrency(order.getLimitPrice()));
        }
        fixMessage.set(OrderTermConverter.toFix(order.getTerm()));
        if (order.isAllOrNone()) {
            fixMessage.set(new ExecInst(Character.toString(ExecInst.ALL_OR_NONE)));
        }
        
        logger.debug("Sending message:\n{}", FixFormatter.format(fixMessage));
        return fixMessage.toString();
    }

    @Override
    public String generateOrderCancelRequest(Order order) {

        OrderCancelRequest fixMessage = new OrderCancelRequest(
                OrigClOrdIDConverter.toFix(getBrokerId(), order.getId()),
                ClOrdIDConverter.toFix(getBrokerId(), order.getId()),
                OrderSideConverter.toFix(order.getSide()),
                new TransactTime(new Date()));

        fixMessage.set(InstrumentConverter.toFix(order.getSymbol()));
        fixMessage.set(OrderQuantityConverter.toFix(order.getQuantity()));
        
        logger.debug("Sending message:\n{}", FixFormatter.format(fixMessage));
        return fixMessage.toString();
    }
    
    private String getBrokerId() {
        return configurationService.getString(ConfigConstants.PROP_BROKER_ID);
    }
}