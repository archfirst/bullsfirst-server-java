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
package org.archfirst.bfexch.domain.referencedata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Provides security definition for all instruments. Keeps instruments in a
 * memory cache.
 *
 * @author Naresh Bhatia
 */
public class ReferenceDataService {

    // ----- Commands -----
    synchronized public void addInstrument(Instrument instrument) {
        getInstrumentList().add(instrument);
        getInstrumentMap().put(instrument.getSymbol(), instrument);
    }

    // ----- Queries -----
    public List<Instrument> getInstruments() {
        return getInstrumentList();
    }

    public Instrument lookup(String symbol) {
        return getInstrumentMap().get(symbol);
    }

    // ----- Attributes -----
    @Inject private ReferenceDataRepository referenceDataRepository;

    /**
     * We will not synchronize the instrumentList or the instrumentMap
     * because no structural changes (additions or deletions of mappings)
     * are performed after initialization. Also this data structures are
     * lazily initialized using <a href="http://en.wikipedia.org/wiki/Double-checked_locking">
     * double-checked locking</a>.
     */

    /** List of Instruments. */
    private volatile List<Instrument> instrumentList;

    /** Map from symbol to Instrument. */
    private volatile Map<String, Instrument> instrumentMap;

    // ----- Getters -----
    private List<Instrument> getInstrumentList() {
        if (instrumentList == null) {
            synchronized(this) {
                if (instrumentList == null) {
                    fetchInstruments();
                }
            }
        }
        return instrumentList;
    }

    private Map<String, Instrument> getInstrumentMap() {
        if (instrumentMap == null) {
            synchronized(this) {
                if (instrumentMap == null) {
                    fetchInstruments();
                }
            }
        }
        return instrumentMap;
    }

    synchronized private void fetchInstruments() {
        instrumentList = referenceDataRepository.findAllInstruments();
        instrumentMap = new HashMap<String, Instrument>();
        for (Instrument instrument : instrumentList) {
            instrumentMap.put(instrument.getSymbol(), instrument);
        }
    }
}