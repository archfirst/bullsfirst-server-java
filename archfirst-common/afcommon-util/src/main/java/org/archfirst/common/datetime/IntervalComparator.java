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

import java.util.Comparator;

import org.joda.time.Interval;

/**
 * Compares two intervals, first by their start times and if start times match then by
 * their end times. If you do not want to create a new instance of this class every time
 * you want to compare two intervals, simply call the getInstance() method to get a static
 * instance. The only reason we provide a public constructor is for libraries like
 * Hibernate that require a class name and construct an instance of that class themselves.
 * 
 * @author Naresh Bhatia
 */
public class IntervalComparator implements Comparator<Interval> {

    public static final IntervalComparator theSingletonInstance = new IntervalComparator();

    public static final IntervalComparator getInstance() {
        return theSingletonInstance;
    }

    @Override
    public int compare(Interval interval1, Interval interval2) {
        int result = 0; // assume equal

        if (interval1 == null) {
            return -1;
        }
        else if (interval2 == null) {
            return 1;
        }
        else {
            result = interval1.getStart().compareTo(interval2.getStart());
            if (result == 0) {
                result = interval1.getEnd().compareTo(interval2.getEnd());
            }
        }

        return result;
    }
}