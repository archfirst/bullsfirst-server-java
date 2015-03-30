/**
 * Copyright 2010 Archfirst
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
package org.archfirst.common.datetime;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * EventStats
 *
 * @author Naresh Bhatia
 */
public class EventStats {

    // ----- Constructors -----
    public EventStats(int totalEvents, long firstEventNanos, long lastEventNanos) {
        this.totalEvents = totalEvents;
        this.firstEventNanos = firstEventNanos;
        this.lastEventNanos = lastEventNanos;
    }

    // ----- Queries -----
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Total events=").append(totalEvents);
        builder.append(", Total time=").append(getTotalMillis()).append(" milliseconds");
        builder.append(", Time/Event=").append(getMillisPerEvent()).append(" milliseconds");

        return builder.toString();
    }

    // ----- Attributes -----
    private static BigDecimal NanosPerMilli = new BigDecimal("1000000");

    private final int totalEvents;
    private final long firstEventNanos;
    private final long lastEventNanos;

    private boolean initialized = false;
    private BigDecimal totalMillis;
    private BigDecimal millisPerEvent;
    
    // ----- Getters -----
    public int getTotalEvents() {
        return totalEvents;
    }
    public long getFirstEventNanos() {
        return firstEventNanos;
    }
    public long getLastEventNanos() {
        return lastEventNanos;
    }

    public BigDecimal getTotalMillis() {
        if (!initialized) {
            init();
        }
        return totalMillis;
    }

    public BigDecimal getMillisPerEvent() {
        if (!initialized) {
            init();
        }
        return millisPerEvent;
    }

    private void init() {
        long totalNanos = lastEventNanos - firstEventNanos;

        this.totalMillis = new BigDecimal(totalNanos)
            .divide(NanosPerMilli, 0, RoundingMode.HALF_UP);

        if (totalEvents == 0) {
            millisPerEvent = new BigDecimal("0.000");
        }
        else {
            millisPerEvent = totalMillis.divide(
                    new BigDecimal(totalEvents), 3, RoundingMode.HALF_UP);
        }
    
        this.initialized = true;
    }
}