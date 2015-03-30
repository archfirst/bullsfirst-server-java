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
package org.archfirst.bfcommon.jsontrading.test;

import org.archfirst.bfcommon.jsontrading.JsonMessage;
import org.archfirst.bfcommon.jsontrading.JsonMessageMapper;
import org.archfirst.bfcommon.jsontrading.Money;
import org.archfirst.bfcommon.jsontrading.NewOrderSingle;
import org.archfirst.bfcommon.jsontrading.Order;
import org.archfirst.bfcommon.jsontrading.OrderSide;
import org.archfirst.bfcommon.jsontrading.OrderStatus;
import org.archfirst.bfcommon.jsontrading.OrderTerm;
import org.archfirst.bfcommon.jsontrading.OrderType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * NewOrderSingleTest
 *
 * @author Naresh Bhatia
 */
public class NewOrderSingleTest {
    
    private DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    @Test
    public void testNewOrderSingle() throws Exception {
        
        // Create 2011-01-01T09:00:00.000-04:00
        DateTime orderDate = new DateTime(
                2011, 1, 1, 9, 0, 0, 0, DateTimeZone.forOffsetHours(-4));
        
        // Create a JsonMessage with NewOrderSingle
        Order order = new Order(
                fmt.print(orderDate),
                "JVEE-1",
                OrderSide.Buy,
                "AAPL",
                100,
                OrderType.Limit,
                new Money("100.00", "USD"),
                OrderTerm.GoodForTheDay,
                false,
                OrderStatus.PendingNew);
        NewOrderSingle newOrderSingle = new NewOrderSingle(order);

        // Write out the JsonMessage as a string
        String jsonMessageString = JsonMessageMapper.toFormattedString(newOrderSingle);
        
        // Read the JsonMessage back
        JsonMessage jsonMessageRead = JsonMessageMapper.fromString(jsonMessageString);
        
        // Make sure that the message type has been retrieved properly
        Assert.assertTrue(
                jsonMessageRead.getClass().equals(NewOrderSingle.class));
        
        // Make sure that the order has been retrieved properly
        Order orderRead =
            ((NewOrderSingle)jsonMessageRead).getOrder();

        // Can't compare the two DateTime objects with equals
        // because deserialization loses the time zone
        Assert.assertTrue(
                (fmt.parseDateTime(orderRead.getCreationTime()))
                .isEqual(orderDate));
        
        Assert.assertEquals(orderRead.getClientOrderId(), order.getClientOrderId());
        Assert.assertEquals(orderRead.getSide(), order.getSide());
        Assert.assertEquals(orderRead.getSymbol(), order.getSymbol());
        Assert.assertEquals(orderRead.getQuantity(), order.getQuantity());
        Assert.assertEquals(orderRead.getType(), order.getType());

        // Can't compare the two BigDecimal objects with equals
        // because precision may be different (e.g. 100.0 and 100.00)
        Assert.assertEquals(orderRead.getLimitPrice().getAmount().compareTo(order.getLimitPrice().getAmount()), 0);

        Assert.assertEquals(orderRead.getLimitPrice().getCurrency(), order.getLimitPrice().getCurrency());
        Assert.assertEquals(orderRead.getTerm(), order.getTerm());
        Assert.assertEquals(orderRead.isAllOrNone(), order.isAllOrNone());
        Assert.assertEquals(orderRead.getStatus(), order.getStatus());
    }
}