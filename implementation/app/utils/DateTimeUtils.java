package utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.PrintWriter;

/**
 * Created by csaq5996 on 4/22/16.
 */
public class DateTimeUtils {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

    private final static DateTime EMPTY_DATE = DateTime.now();

    public static String dateTimeToTimeString(DateTime time) {
        return TIME_FORMATTER.print(time);

    }

    public static String dateTimeToDateString(DateTime date) {
        return DATE_FORMATTER.print(date);
    }

    public static DateTime stringToTime(String time) {
        return stringToTime(time, EMPTY_DATE);
    }

    public static DateTime stringToTime(String timeString, DateTime date) {
        DateTime time = TIME_FORMATTER.parseDateTime(timeString);

        return new DateTime(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth(),
            time.getHourOfDay(),
            time.getMinuteOfHour()
        );
    }

    public static DateTime stringToDateTime(String date) {
        return stringToDateTime(date, 0, 0);
    }

    public static DateTime stringToDateTime(String dateString, int hours, int minutes) {
        DateTime date = DATE_FORMATTER.parseDateTime(dateString);
        return new DateTime(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth(),
            hours, minutes
        );
    }

    public static DateTime startOfActualYear() {
        DateTime now = DateTime.now();
        now = now.minusMillis(now.getMillisOfDay());
        now = now.minusDays(now.getDayOfYear() - 1);
        return now;
    }

    public static DateTime endOfActualYear() {
        DateTime startOfYear = startOfActualYear();
        startOfYear = startOfYear.minusMillis(1);
        return startOfYear.plusYears(1);
    }

    public static DateTime stringToDateTime(String dateString, DateTime time) {
        DateTime date = DATE_FORMATTER.parseDateTime(dateString);
        return new DateTime(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth(),
            time.getHourOfDay(),
            time.getMinuteOfHour()
        );
    }

    public static DateTime mergeDateAndTime(DateTime date, DateTime time) {
        return new DateTime(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth(),
            time.getHourOfDay(),
            time.getMinuteOfHour()
        );
    }
}
