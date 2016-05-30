package business.reporting;

import models.ReportEntry;
import models.User;
import utils.DateTimeUtils;

/**
 * Created by Stefan on 28.05.2016.
 */
public class ReportEntryFactory {
    public static ReportEntry createAnnualReportWithFlextimeExceedingForUser(User user) {
        int daysInYear = DateTimeUtils.getWorkdaysOfThisYear();
        double hoursPerDay = 38.5 / 5;
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR + 1;
        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithNearlyExceededFlextimeForUser(User user) {
        int daysInYear = DateTimeUtils.getWorkdaysOfThisYear();
        double hoursPerDay = 40 / 5;
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)(CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.8);
        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithTooManyMinusHours(User user) {
        int daysInYear = DateTimeUtils.getWorkdaysOfThisYear();
        double hoursPerDay = 40 / 5;
        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)(CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR - 1);

        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithNearlyTooManyMinusHours(User user) {
        int daysInYear = DateTimeUtils.getWorkdaysOfThisYear();
        double hoursPerDay = 40 / 5;

        long workMinutesShould = (long)(hoursPerDay * daysInYear * 60);
        long flexTimeExceedInHours = (long)(CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.8);
        ReportEntry entry = new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould + 60 * flexTimeExceedInHours,
                daysInYear * 30, 0, 0, daysInYear);
        return entry;
    }

    public static ReportEntry createAnnualReportWithBreakOverUser(User user) {
        int dayInYear = DateTimeUtils.getWorkdaysOfThisYear();
        double hoursPerDay = 38.5 / 5;
        long workMinutesShould = (long)(hoursPerDay * dayInYear * 60);
        return new ReportEntry(user, hoursPerDay, 25, 0, 4, workMinutesShould, workMinutesShould,
                (long)(dayInYear * 30 * (1 + CollectiveConstants.TOLERATED_BREAK_MISSUSE_PERCENTAGE)), 0, 0, dayInYear);
    }
}
