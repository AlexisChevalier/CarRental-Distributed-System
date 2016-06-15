/**
 * CarRental
 *
 * This file provides date management abstraction methods
 * I am using UTC as common timezone
 * The dates are encoded using the ISO8601 format to be sent over the network
 * The dates are stored as timestamp in the database - the timestamp is rounded to the day, discarding the time elements
 */

package com.vehiclerental.utils;

import com.vehiclerental.exceptions.InvalidDateException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    //Results of the comparison system
    public final static int DATE1_BEFORE_DATE2 = -1;
    public final static int DATE1_AFTER_DATE2 = 1;
    public final static int DATE1_EQUAL_DATE2 = 0;

    //Default timezone
    private final static TimeZone timeZone = TimeZone.getTimeZone("UTC");

    /**
     * Returns the entire number of business booking days between two given dates
     *
     * @param start pickup date (included)
     * @param end return date (included)
     * @return the number of booking days
     */
    public static long getBookingDays(Calendar start, Calendar end) {
        long diff = end.getTime().getTime() - start.getTime().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    /**
     * Format and rounds a given timestamp to a ISO8601-compliant date string
     * @param timestampWithMilliseconds timestamp in milliseconds
     * @return ISO8601 date string
     */
    public static String getIso8601DateString(long timestampWithMilliseconds) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeInMillis(timestampWithMilliseconds);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(timeZone);
        return getIso8601DateFormat().format(calendar.getTime());
    }

    /**
     * Converts an ISO8601 date string to a Java Calendar
     * @param string ISO8601 date string
     * @return Calendar
     */
    public static Calendar getCalendarFromIso8601String(String string) throws InvalidDateException {
        try {
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(getIso8601DateFormat().parse(string));
            cal.setTimeZone(timeZone);
            return cal;
        } catch (Exception e) {
            throw new InvalidDateException("Invalid date format");
        }
    }

    /**
     * Format and rounds a given timestamp to a Java calendar
     * @param timestampWithMilliseconds timestamp in milliseconds
     * @return Calendar
     */
    public static Calendar getCalendarFromTimestamp(long timestampWithMilliseconds) throws InvalidDateException {
        try {
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTimeInMillis(timestampWithMilliseconds);
            cal.setTimeZone(timeZone);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        } catch (Exception e) {
            throw new InvalidDateException("Invalid date format");
        }
    }

    /**
     * Formats a date to an ISO8601 date string
     * @param date the date
     * @return ISO8601 date string
     */
    public static String getIso8601DateString(Calendar date) {
        return getIso8601DateFormat().format(date.getTime());
    }

    /**
     * Generates an ISO8601-compliant date formatter
     * @return the date formatter
     */
    private static DateFormat getIso8601DateFormat() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.ENGLISH);
        df.setTimeZone(timeZone);
        return df;
    }

    /**
     * Returns a Java calendar rounded to the current day
     * @return the calendar
     */
    public static Calendar getTodayCalendar() {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeZone(timeZone);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Compare two calendars without time comparison
     * @param cal1 first calendar
     * @param cal2 second calendar
     * @return DATE1_EQUAL_DATE2 if the dates are the same, DATE1_BEFORE_DATE2 if the date1 is before date2 and DATE1_AFTER_DATE2 otherwise
     */
    public static int compareCalendar(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return 0;
        }
        if (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            return DATE1_EQUAL_DATE2;
        }

        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)
                || cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)
                || cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)) {
            return DATE1_BEFORE_DATE2;
        }

        return DATE1_AFTER_DATE2;
    }
}