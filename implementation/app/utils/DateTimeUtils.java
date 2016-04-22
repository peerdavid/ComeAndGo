package utils;

import org.joda.time.DateTime;

import java.io.PrintWriter;

/**
 * Created by csaq5996 on 4/22/16.
 */
public class DateTimeUtils {

    public static String dateTimeToTime(DateTime dateTime){
        int hours = dateTime.getHourOfDay();
        int minutes = dateTime.getMinuteOfHour();

        String result = String.format("%02d:%02d",hours,minutes);

        return result;

    }

    public static String dateTimeToDate(DateTime dateTime){
        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthOfYear();
        int year = dateTime.getYear();

        String result = String.format("%02d.%02d.%4d",day,month,year);

        return result;
    }
}
