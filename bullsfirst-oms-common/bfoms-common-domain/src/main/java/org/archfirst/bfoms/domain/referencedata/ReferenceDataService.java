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
package org.archfirst.bfoms.domain.referencedata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides security definition for all instruments. Keeps instruments in a
 * memory cache. This cache is initialized on startup using an external reference
 * data service (via ReferenceDataAdapter).
 *
 * @author Naresh Bhatia
 */
@Singleton
public class ReferenceDataService {
    private static final Logger logger =
        LoggerFactory.getLogger(ReferenceDataService.class);

    // ----- Constructors -----
    public ReferenceDataService() {
        logger.debug("ReferenceDataService.ReferenceDataService()");
    }

    // ----- Queries -----
    public List<Instrument> getInstruments() {
        return getInstrumentList();
    }

    public Instrument lookup(String symbol) {
        return getInstrumentMap().get(symbol);
    }
    
    // ----- Attributes -----
    @Inject private ReferenceDataAdapter referenceDataAdapter;
    
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

    // ----- Getters and Setters -----
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
        instrumentList = referenceDataAdapter.getInstruments();
        instrumentMap = new HashMap<String, Instrument>();
        for (Instrument instrument : instrumentList) {
            instrumentMap.put(instrument.getSymbol(), instrument);
        }
    }
}