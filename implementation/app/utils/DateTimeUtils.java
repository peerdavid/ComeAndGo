package utils;

import org.joda.time.DateTime;

import java.io.PrintWriter;

/**
 * Created by csaq5996 on 4/22/16.
 */
public class DateTimeUtils {

    private final static DateTime EMPTY_DATE = new DateTime(0, 0, 0, 0, 0);

    public static String dateTimeToTime(DateTime dateTime) {
        int hours = dateTime.getHourOfDay();
        int minutes = dateTime.getMinuteOfHour();

        String result = String.format("%02d:%02d", hours, minutes);

        return result;

    }

    public static String dateTimeToDate(DateTime dateTime) {
        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthOfYear();
        int year = dateTime.getYear();

        String result = String.format("%02d.%02d.%4d", day, month, year);

        return result;
    }

    public static DateTime stringToTime(String time) {
        return stringToTime(time, EMPTY_DATE);
    }

    public static DateTime stringToTime(String time, DateTime date) {
        String[] parts = time.split(":");

        return new DateTime(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth(),
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1])
        );
    }

    public static DateTime stringToDateTime(String date) {
        return stringToDateTime(date, 0, 0);
    }

    public static DateTime stringToDateTime(String date, int hours, int minutes) {
        String[] parts = date.split("\\.");
        return new DateTime(
            Integer.parseInt(parts[2]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[0]),
            hours, minutes
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
