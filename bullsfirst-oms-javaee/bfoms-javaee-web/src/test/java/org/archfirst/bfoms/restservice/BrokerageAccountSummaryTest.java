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
package org.archfirst.bfoms.restservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.archfirst.bfoms.domain.account.brokerage.BrokerageAccountSummary;
import org.archfirst.bfoms.domain.account.brokerage.Position;
import org.archfirst.bfoms.restservice.util.ObjectMapperProvider;
import org.archfirst.common.money.Money;
import org.archfirst.common.quantity.DecimalQuantity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for checking serialization/deserialization of BrokerageAccountSummary.
 */
public class BrokerageAccountSummaryTest {

    private static Long ACCOUNT_ID = 1L;
    public static String ACCOUNT_NAME = "Brokerage Account 1";
    
    public static Money CASH_IN_ACCOUNT = new Money("1000.00");

    public static String INS_SYMBOL = "CSCO";
    public static String INS_NAME = "CSCO";
    public static Money INS_LAST_TRADE = new Money("20.00");

    private static Long LOT_ID = 1L;
    private static DateTime LOT_CREATION_TIME = new DateTime(2011, 1, 1, 0, 0, 0, 0);
    private static DecimalQuantity LOT_QUANTITY = new DecimalQuantity(100);
    public static Money LOT_PRICE_PAID = new Money("10.00");
    
    @Test
    public void testSerialization()
        throws JsonGenerationException, JsonMappingException, IOException {
        
        Position cashPosition = new Position();
        cashPosition.setCashPosition(ACCOUNT_ID, ACCOUNT_NAME, CASH_IN_ACCOUNT);

        Position instrumentPosition = new Position();
        instrumentPosition.setInstrumentPosition(
                ACCOUNT_ID, ACCOUNT_NAME, INS_SYMBOL, INS_NAME, INS_LAST_TRADE);
        
        Position lotPosition = new Position();
        lotPosition.setLotPosition(
                ACCOUNT_ID, ACCOUNT_NAME, INS_SYMBOL, INS_NAME, LOT_ID,
                LOT_CREATION_TIME, LOT_QUANTITY, INS_LAST_TRADE, LOT_PRICE_PAID);
        
        instrumentPosition.addChild(lotPosition);
        instrumentPosition.calculateInstrumentPosition();
        
        List<Position> positions = new ArrayList<Position>();
        positions.add(instrumentPosition);
        positions.add(cashPosition);
        
        BrokerageAccountSummary summary1 = new BrokerageAccountSummary(
                ACCOUNT_ID,
                ACCOUNT_NAME,
                CASH_IN_ACCOUNT,
                CASH_IN_ACCOUNT.plus(instrumentPosition.getMarketValue()),
                true,
                true,
                true,
                positions);
        
        ObjectMapper mapper =
            new ObjectMapperProvider().getContext(ObjectMapper.class);
        String json1 = mapper.writeValueAsString(summary1);
        // System.out.println(json1);
        
        BrokerageAccountSummary summary2 =
            mapper.readValue(json1, BrokerageAccountSummary.class);
        
        Assert.assertEquals(summary2, summary1);
    }
}