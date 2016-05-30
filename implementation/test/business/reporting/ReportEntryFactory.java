package business.reporting;

import models.ReportEntry;
import models.User;
import utils.DateTimeUtils;

/**
 * Created by Stefan on 28.05.2016.
 */
public class ReportEntryFactory {
    static int daysInYear = DateTimeUtils.getWorkdaysOfThisYear();
    static double hoursPerDay = 38.5 / 5;


    public static ReportEntry createAnnualReportWithFlextimeExceedingForUser(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR + 1;
        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithNearlyExceededFlextimeForUser(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)(CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.8);
        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithTooManyMinusHours(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)(CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR - 1);

        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithNearlyTooManyMinusHours(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)(CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.8);
        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithBreakOverUse(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        return new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould,
                (long)(daysInYear * 30 * (1 + CollectiveConstants.TOLERATED_BREAK_OVERUSE_PERCENTAGE) + daysInYear), 0, 0, daysInYear);
    }

    public static ReportEntry createAnnualReportWithBreakUnderUse(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        return new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould,
                (long)(daysInYear * 30 * (1 - CollectiveConstants.TOLERATED_BREAK_UNDERUSE_PERCENTAGE) - daysInYear), 0, 0, daysInYear);
    }

    public static ReportEntry createAnnualReportWithHolidayOverConsumption(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        return new ReportEntry(user, hoursPerDay, 26, 0, 4, workMinutesShould, workMinutesShould,
                daysInYear * 30, 0, 0, daysInYear);
    }

    public static ReportEntry createAnnualReportWithTooManyUnusedHoliday(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        return new ReportEntry(user, hoursPerDay, 25, CollectiveConstants.MAX_NUMBER_OF_UNUSED_HOLIDAY_PER_YEAR + 1, 0, workMinutesShould, workMinutesShould,
                daysInYear * 30, 0, 0, daysInYear);
    }

    public static ReportEntry createAnnualReportWithTooManySickDays(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        return new ReportEntry(user, hoursPerDay, 25, 0, (int)(CollectiveConstants.TOLERATED_SICKLEAVE_DAYS_PER_MONTH * 12) + 1, workMinutesShould, workMinutesShould,
                daysInYear * 30, 0, 0, daysInYear);
    }

    public static ReportEntry createValidReport(User user) {
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        return new ReportEntry(user, hoursPerDay, 25, 0, 0, workMinutesShould, workMinutesShould,
                daysInYear * 30, 0, 0, daysInYear);
    }
}
