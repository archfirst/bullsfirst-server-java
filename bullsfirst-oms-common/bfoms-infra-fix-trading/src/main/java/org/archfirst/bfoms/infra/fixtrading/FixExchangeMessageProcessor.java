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

import javax.inject.Inject;

import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountService;
import org.archfirst.bfoms.domain.exchange.ExchangeMessageProcessor;
import org.archfirst.bfoms.infra.fixtrading.converters.AvgPriceConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.ClOrdIDConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.CumQtyConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.ExecutionTypeConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.FixFormatter;
import org.archfirst.bfoms.infra.fixtrading.converters.FixUtil;
import org.archfirst.bfoms.infra.fixtrading.converters.LastPriceConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.LastQtyConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.LeavesQtyConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrderSideConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrderStatusConverter;
import org.archfirst.bfoms.infra.fixtrading.converters.OrigClOrdIDConverter;
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
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.OrderCancelReject;

/**
 * FixExchangeMessageProcessor
 *
 * @author Naresh Bhatia
 */
public class FixExchangeMessageProcessor extends MessageCracker
        implements quickfix.Application, ExchangeMessageProcessor {
    private static final Logger logger =
        LoggerFactory.getLogger(FixExchangeMessageProcessor.class);

    @Inject private BrokerageAccountService brokerageAccountService;

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
    public void onMessage(ExecutionReport message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

        // Extract symbol
        String symbol = message.getInstrument().getSymbol().getValue();

        // Extract execution report
        org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReport executionReport =
            new org.archfirst.bfoms.domain.account.brokerage.order.ExecutionReport(
                    ExecutionTypeConverter.toDomain(message.getExecType()),
                    message.getOrderID().getValue(),
                    message.getExecID().getValue(),
                    ClOrdIDConverter.toDomain(message.getClOrdID()),
                    OrderStatusConverter.toDomain(message.getOrdStatus()),
                    OrderSideConverter.toDomain(message.getSide()),
                    symbol,
                    LastQtyConverter.toDomain(message.getLastQty()),
                    LeavesQtyConverter.toDomain(message.getLeavesQty()),
                    CumQtyConverter.toDomain(message.getCumQty()),
                    LastPriceConverter.toDomain(message.getLastPx()),
                    AvgPriceConverter.toDomain(message.getAvgPx()));

        // Send to brokerageAccountService for processing
        brokerageAccountService.processExecutionReport(executionReport);
    }

    @Override
    public void onMessage(OrderCancelReject message, SessionID sessionID)
            throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        
        brokerageAccountService.processOrderCancelReject(
                OrigClOrdIDConverter.toDomain(message.getOrigClOrdID()),
                OrderStatusConverter.toDomain(message.getOrdStatus()));
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