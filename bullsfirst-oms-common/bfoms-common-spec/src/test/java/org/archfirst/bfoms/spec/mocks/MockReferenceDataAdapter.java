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
package org.archfirst.bfoms.spec.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.archfirst.bfoms.domain.referencedata.Instrument;
import org.archfirst.bfoms.domain.referencedata.ReferenceDataAdapter;

/**
 * MockReferenceDataAdapter
 *
 * @author Naresh Bhatia
 */
public class MockReferenceDataAdapter implements ReferenceDataAdapter {

    /** List of Instruments. */
    private volatile List<Instrument> instrumentList =
        new ArrayList<Instrument>();

    /** Map from symbol to Instrument. */
    private volatile Map<String, Instrument> instrumentMap =
        new HashMap<String, Instrument>();
    
    public MockReferenceDataAdapter() {
        this.addInstrument("AAPL", "Apple Inc.", "NASDAQ");
        this.addInstrument("CSCO", "Cisco Systems, Inc.", "NASDAQ");
    }
    
    public void addInstrument(String symbol, String name, String exchange) {
        Instrument instrument = new Instrument(symbol, name, exchange);
        instrumentList.add(instrument);
        instrumentMap.put(symbol, instrument);
    }

    @Override
    public List<Instrument> getInstruments() {
        return instrumentList;
    }
}