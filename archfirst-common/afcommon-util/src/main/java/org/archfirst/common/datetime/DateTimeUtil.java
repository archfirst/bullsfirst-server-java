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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Date and time utilities.
 * 
 * @author Naresh Bhatia
 */
public final class DateTimeUtil {

    /**
     * Date pattern. Example: 12/31/2008
     * <pre>
     *  Usage:
     *    DateTimeFormatter format = DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_PATTERN);
     *    dateTime.toString(format);
     * </pre>
     */
    public static final String DATE_PATTERN = "MM/dd/yyyy";

    /** DateTime pattern. Example: 12/31/2008 4:00 PM */
    public static final String DATE_TIME_PATTERN = "MM/dd/yyyy hh:mm a";

    /** DateTimeMinute pattern. Example: 12/31/2008 16:00:00" */
    public static final String DATE_TIME_SEC_PATTERN = "MM/dd/yyyy HH:mm:ss";

    /** DateTimeTz pattern. Example: 12/31/2008 16:00:00 GMT */
    public static final String DATE_TIME_TZ_PATTERN = "MM/dd/yyyy HH:mm:ss z";

    /** Time pattern. Example: 16:00:00 */
	public static final String TIME_PATTERN = "HH:mm:ss";

    /** Timestamp pattern. Example: 12/31/2008 16:00:00.0000 */
	public static final String TIMESTAMP_PATTERN = "MM/dd/yyyy HH:mm:ss.SSS";

    /** GMT TimeZone ID **/
    public static final String GMT = "GMT";

    /** Maximum time for setting interval end dates **/
    public static final DateTime maxDateTime = new DateTime(9999, 12, 31, 23, 59, 59, 0);

    // -----------------------------------------------------------------------------------
    // Formating Methods
    // -----------------------------------------------------------------------------------
    /**
     * Returns a SimpleDateFormat for the specified time zone and formatting pattern (as
     * specified by SimpleDateFormat).
     *
     * @param timeZone the time zone for the SimpleDateFormat.
     * @param pattern the formatting pattern for the SimpleDateFormat.
     * @return SimpleDateFormat with specified time zone and pattern
     */
    public static final SimpleDateFormat getSimpleDateFormat(
        TimeZone timeZone, String pattern) {

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setLenient(false);
        formatter.setTimeZone(timeZone);
        return formatter;
    }

    /**
     * Returns a SimpleDateFormat for GMT time zone and the specified formatting pattern.
     *
     * @param pattern the formatting pattern for the SimpleDateFormat.
     * @return SimpleDateFormat with GMT time zone and pattern
     */
    public static final SimpleDateFormat getGmtSimpleDateFormat(String pattern) {
        return DateTimeUtil.getSimpleDateFormat(TimeZone.getTimeZone(GMT), pattern);
    }

    /**
     * Returns a SimpleDateFormat for local time zone and the specified formatting pattern.
     * <pre>
     *  Usage:
     *    SimpleDateFormat timePickerFormat =
     *        DateTimeUtil.getLocalSimpleDateFormat(DateTimeUtil.DATE_TIME_PATTERN);
     * </pre>
     *
     * @param pattern the formatting pattern for the SimpleDateFormat.
     * @return SimpleDateFormat with local time zone and pattern
     */
    public static final SimpleDateFormat getLocalSimpleDateFormat(String pattern) {
        return DateTimeUtil.getSimpleDateFormat(TimeZone.getDefault(), pattern);
    }
    
    public static final String toString(LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_PATTERN);
        return date.toString(fmt);
    }

    public static final String toString(DateTime instant) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_PATTERN);
        return instant.toString(fmt);
    }

    public static final String toStringDateTimeSecond(DateTime instant) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_SEC_PATTERN);
        return instant.toString(fmt);
    }

    public static final String toStringISODateTime(DateTime instant) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return instant.toString(fmt);
    }

    public static final String toStringTimestamp(DateTime instant) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.TIMESTAMP_PATTERN);
        return instant.toString(fmt);
    }

    public static final String toString(Interval interval) {
        StringBuilder builder = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_SEC_PATTERN);

        builder.append("[");
        builder.append(interval.getStart().toString(fmt));
        builder.append(", ");
        builder.append(interval.getEnd().toString(fmt));
        builder.append("]");

        return builder.toString();
    }

    public static final LocalDate parseLocalDate(String strDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_PATTERN);
        return fmt.parseDateTime(strDate).toLocalDate();
    }

    public static final DateTime parseDateTime(String strDateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_PATTERN);
        return fmt.parseDateTime(strDateTime);
    }

    public static final DateTime parseDateTimeSecond(String strDateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_SEC_PATTERN);
        return fmt.parseDateTime(strDateTime);
    }

    public static final DateTime parseISODateTime(String strDateTime) {
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.parseDateTime(strDateTime);
    }

    // -----------------------------------------------------------------------------------
    // Setting current time
    // -----------------------------------------------------------------------------------
    /**
     * Sets the current time to return the fixed time specified.
     */
    public static final void setCurrentTimeFixed(
            int year,
            int monthOfYear,
            int dayOfMonth) {
        DateTime currentTime = new DateTime(
            year, monthOfYear, dayOfMonth, 0, 0, 0, 0);
        DateTimeUtils.setCurrentMillisFixed(currentTime.getMillis());
    }
    
    /**
     * Sets the current time to return the fixed time specified.
     */
    public static final void setCurrentTimeFixed(
            int year,
            int monthOfYear,
            int dayOfMonth,
            int hourOfDay,
            int minuteOfHour,
            int secondOfMinute) {
        DateTime currentTime = new DateTime(
            year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0);
        DateTimeUtils.setCurrentMillisFixed(currentTime.getMillis());
    }

    // -----------------------------------------------------------------------------------
    // Interval construction methods
    // -----------------------------------------------------------------------------------
    public static Interval intervalStartingOn(DateTime start) {
        return new Interval(start, maxDateTime);
    }

    public static Interval intervalEndingOn(Interval interval, DateTime end) {
        return new Interval(interval.getStart(), end);
    }

    public static Interval intervalStartingOn(Interval interval, DateTime start) {
        return new Interval(start, interval.getEnd());
    }

    // -----------------------------------------------------------------------------------
    // Convenience methods
    // -----------------------------------------------------------------------------------
    public static final DateTime newDate(
            int year,
            int monthOfYear,
            int dayOfMonth) {
        return new DateTime(year, monthOfYear, dayOfMonth, 0, 0, 0, 0);
    }
}