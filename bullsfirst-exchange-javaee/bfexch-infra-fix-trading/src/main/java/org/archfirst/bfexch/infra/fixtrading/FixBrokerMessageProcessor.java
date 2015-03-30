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
package org.archfirst.bfexch.infra.fixtrading;

import javax.inject.Inject;

import org.archfirst.bfexch.domain.broker.BrokerMessageProcessor;
import org.archfirst.bfexch.domain.trading.TradingService;
import org.archfirst.bfexch.domain.trading.order.Order;
import org.archfirst.bfexch.infra.fixtrading.converters.FixFormatter;
import org.archfirst.bfexch.infra.fixtrading.converters.FixUtil;
import org.archfirst.bfexch.infra.fixtrading.converters.MoneyConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.OrderQuantityConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.OrderSideConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.OrderTermConverter;
import org.archfirst.bfexch.infra.fixtrading.converters.OrderTypeConverter;
import org.archfirst.common.money.Money;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.MessageUtils;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.Currency;
import quickfix.field.ExecInst;
import quickfix.field.Price;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelRequest;

/**
 * FixBrokerMessageProcessor
 *
 * @author Naresh Bhatia
 */
public class FixBrokerMessageProcessor extends MessageCracker
        implements quickfix.Application, BrokerMessageProcessor {
    private static final Logger logger =
        LoggerFactory.getLogger(FixBrokerMessageProcessor.class);
    
    @Inject private TradingService tradingService;

    @Override
    public void processMessage(String messageText) {
        quickfix.Message fixMessage;
        try {
            fixMessage = MessageUtils.parse(
                    FixUtil.getDefaultMessageFactory(),
                    FixUtil.getDefaultDataDictionary(),
                    messageText);
            logger.debug("Received message:\n{}", FixFormatter.format(fixMessage));
            this.fromApp(fixMessage, null);
        }
        catch (InvalidMessage e) {
            logger.error("Invalid FIX message received: " + messageText, e);
        }
        catch (FieldNotFound e) {
            logger.error("Invalid FIX message received: " + messageText, e);
        }
        catch (IncorrectDataFormat e) {
            logger.error("Invalid FIX message received: " + messageText, e);
        }
        catch (IncorrectTagValue e) {
            logger.error("Invalid FIX message received: " + messageText, e);
        }
        catch (UnsupportedMessageType e) {
            logger.error("Invalid FIX message received: " + messageText, e);
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        crack(message, sessionId);
    }

    @Override
    public void onMessage(NewOrderSingle message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        // Extract symbol
        String symbol = message.getInstrument().getSymbol().getValue();

        // Extract limit price
        Money limitPrice = null;
        if (message.isSetField(Price.FIELD) && message.isSetField(Currency.FIELD)) {
            limitPrice = MoneyConverter.toDomain(message.getPrice(), message.getCurrency());
        }

        // Extract allOrNone
        boolean allOrNone = message.isSetField(ExecInst.FIELD) &&
            message.getExecInst().getValue().indexOf(ExecInst.ALL_OR_NONE) >= 0;

        // Create Order
        Order order = new Order(
                new DateTime(message.getTransactTime().getValue()),
                message.getClOrdID().getValue(),
                OrderSideConverter.toDomain(message.getSide()),
                symbol,
                OrderQuantityConverter.toDomain(message.getOrderQtyData()),
                OrderTypeConverter.toDomain(message.getOrdType()),
                limitPrice,
                OrderTermConverter.toDomain(message.getTimeInForce()),
                allOrNone);

        // Place order
        tradingService.processNewOrderSingle(order);
    }

    @Override
    public void onMessage(OrderCancelRequest message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        
        tradingService.processOrderCancelRequest(
                message.getOrigClOrdID().getValue());
    }

    @Override
    public void onCreate(SessionID sessionId) {
    }

    @Override
    public void onLogon(SessionID sessionId) {
    }

    @Override
    public void onLogout(SessionID sessionId) {
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }
}