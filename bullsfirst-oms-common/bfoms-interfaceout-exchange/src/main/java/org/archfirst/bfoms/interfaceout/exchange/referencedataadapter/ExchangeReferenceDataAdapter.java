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
package org.archfirst.bfoms.interfaceout.exchange.referencedataadapter;

import java.util.ArrayList;
import java.util.List;

import org.archfirst.bfoms.domain.referencedata.Instrument;
import org.archfirst.bfoms.domain.referencedata.ReferenceDataAdapter;
import org.archfirst.bfoms.interfaceout.exchange.referencedataadapter.client.ReferenceDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExchangeReferenceDataAdapter
 *
 * @author Naresh Bhatia
 */
public class ExchangeReferenceDataAdapter implements ReferenceDataAdapter {
    private static final Logger logger =
        LoggerFactory.getLogger(ExchangeReferenceDataAdapter.class);

    @Override
    public List<Instrument> getInstruments() {
        logger.debug("---> ExchangeReferenceDataAdapter.getInstruments()");
        
        ReferenceDataService service = new ReferenceDataService();
        List<org.archfirst.bfoms.interfaceout.exchange.referencedataadapter.client.Instrument> wsInstruments =
            service.getReferenceDataWebServicePort().getInstruments();

        List<Instrument> instrumentList = new ArrayList<Instrument>();
        for (org.archfirst.bfoms.interfaceout.exchange.referencedataadapter.client.Instrument wsInstrument : wsInstruments)
            instrumentList.add(toDomainInstrument(wsInstrument));

        logger.debug("<-- ExchangeReferenceDataAdapter.getInstruments()");
        return instrumentList;
    }
    
    private static final Instrument toDomainInstrument(
            org.archfirst.bfoms.interfaceout.exchange.referencedataadapter.client.Instrument wsInstrument) {
        
        return new Instrument(
                wsInstrument.getSymbol(),
                wsInstrument.getName(),
                wsInstrument.getExchange());
    }

}