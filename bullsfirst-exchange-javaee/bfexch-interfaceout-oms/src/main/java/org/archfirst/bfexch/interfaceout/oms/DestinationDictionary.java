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
package org.archfirst.bfexch.interfaceout.oms;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.jms.Destination;

/**
 * Dictionary to lookup JMS destinations for brokers. 
 *
 * @author Naresh Bhatia
 */
@Singleton
public class DestinationDictionary {
    
    // ----- Queries -----
    public Destination getBrokerDestination(String brokerId) {
        return getDestinationMap().get(brokerId);
    }

    // ----- Attributes -----
    /**
     * Map from brokerId to JMS destination. We will not synchronize this map
     * because no structural changes (additions or deletions of mappings)
     * are performed after initialization. Also this map is lazily initialized
     * using <a href="http://en.wikipedia.org/wiki/Double-checked_locking">
     * double-checked locking</a>.
     */
    private volatile Map<String, Destination> destinationMap = null;

    @Resource(mappedName="jms/ExchangeToOmsJavaeeQueue")
    private Destination jveeDestination;
    
    @Resource(mappedName="jms/ExchangeToOmsSpringQueue")
    private Destination spngDestination;
    
    // ----- Getters  -----
    private Map<String, Destination> getDestinationMap() {
        if (destinationMap == null) {
            synchronized(this) {
                if (destinationMap == null) {
                    initDestinationMap();
                }
            }
        }
        return destinationMap;
    }

    private void initDestinationMap() {
        destinationMap = new HashMap<String, Destination>();
        destinationMap.put("JVEE", jveeDestination);
        destinationMap.put("SPNG", spngDestination);
    }
}