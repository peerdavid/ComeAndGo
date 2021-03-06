package utils;

import models.TimeOff;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.PrintWriter;
import java.security.InvalidParameterException;

/**
 * Created by csaq5996 on 4/22/16.
 */
public class DateTimeUtils {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

    private final static DateTime EMPTY_DATE = DateTime.now();
    public final static DateTime BIG_BANG = new DateTime(0, 1, 1, 0, 0, 0, 0);

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

    public static String weekToString(DateTime when) {
        return dateTimeToDateString(startOfWeek(when)) + " - " + dateTimeToDateString(endOfWeek(when));
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

    public static DateTime startOfMonth(DateTime date) {
        date = date.minusDays(date.getDayOfMonth() - 1);
        return startOfDay(date);
    }

    public static DateTime endOfMonth(DateTime date) {
        // to avoid to get into next after next month... (e.g. 31.1. + 31 days would be March...)
        DateTime nextMonth = date.plusDays(date.getDayOfMonth() > 15 ? 16 : 31);
        return startOfMonth(nextMonth).minusMillis(1);
    }

    public static DateTime startOfDay(DateTime day) {
        return day.minusMillis(day.getMillisOfDay());
    }

    public static DateTime endOfDay(DateTime day) {
        return startOfDay(day).minusMillis(1).plusDays(1);
    }

    public static DateTime startOfWeek(DateTime week) {
        week = week.minusDays(week.getDayOfWeek() - 1);
        week = startOfDay(week);
        return week;
    }

    public static DateTime endOfWeek(DateTime week) {
        week = startOfWeek(week);
        week = week.minusMillis(1).plusDays(7);
        return week;
    }

   /**
    * @param d2
    * @param d1
    * @return the difference d2-d1 in minutes
    */
    public static long getDateTimeDifferenceInMinutes(DateTime d2, DateTime d1) {
      if(d2.toLocalDate().isEqual(d1.toLocalDate())) {
          return d2.getMinuteOfDay() - d1.getMinuteOfDay();
      }
      else {
        int minutesFromD1ToNextDay = DateTimeConstants.MINUTES_PER_DAY - d1.getMinuteOfDay();
        int minutesFromD2ToPreviousDay = d2.getMinuteOfDay();

        DateTime dayAfterD1 = endOfDay(d1).plusMillis(1);
        DateTime dayBeforeD2 = startOfDay(d2);
        int daysDifference = dayBeforeD2.getDayOfYear() - dayAfterD1.getDayOfYear()
            + (dayBeforeD2.getYear() - dayAfterD1.getYear()) * 365;
        return minutesFromD1ToNextDay + minutesFromD2ToPreviousDay + daysDifference * DateTimeConstants.MINUTES_PER_DAY;
      }
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

    public static int getWorkdaysOfThisYearUpToNow(DateTime entryDate) {
        DateTime january1st = DateTimeUtils.startOfActualYear();

        // If user joined company this year
        if (entryDate.getYear() == DateTime.now().getYear()) {
            return getWorkdaysOfTimeInterval(entryDate, DateTime.now());
        }
        return getWorkdaysOfTimeInterval(january1st, DateTime.now());
    }


    public static int getWorkdaysOfThisYearUpToSpecificDate(DateTime entryDate, DateTime limit) {
        DateTime january1st = DateTimeUtils.startOfActualYear();

        // If user joined company this year
        if (entryDate.getYear() == DateTime.now().getYear()) {
            return getWorkdaysOfTimeInterval(entryDate, limit);
        }
        return getWorkdaysOfTimeInterval(january1st, limit);
    }

    public static int getWorkdaysOfThisYear() {
        DateTime january1st = DateTimeUtils.startOfActualYear();
        DateTime december31th = DateTimeUtils.endOfActualYear();

        return getWorkdaysOfTimeInterval(january1st, december31th);
    }

    // This function only counts real work days, not the weekend
    public static int getWorkdaysOfTimeInterval(DateTime from, DateTime to) {
        int workdays = 0;
        int dayDifference = Days.daysBetween(from.toLocalDate(), to.toLocalDate()).getDays();
        for (int i = 0; i <= dayDifference; i++) {
            if (from.plusDays(i).getDayOfWeek() != 6 && from.plusDays(i).getDayOfWeek() != 7) {
                workdays++;
            }
        }
        return workdays;
    }

    public static double getAliquoteHolidayDays(DateTime from, DateTime to, double holidayDaysPerYear) {
        double aliquoteHolidayPerDay = holidayDaysPerYear/365;
        int dayDifference = Days.daysBetween(from.toLocalDate(), to.toLocalDate()).getDays();
        return dayDifference * aliquoteHolidayPerDay;
    }

}
