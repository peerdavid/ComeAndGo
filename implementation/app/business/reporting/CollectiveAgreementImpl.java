package business.reporting;

import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import models.*;
import org.joda.time.DateTime;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAgreementImpl implements CollectiveAgreement {

    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {

        long workMinutesWithoutBreak = timeTracks.stream().mapToLong(t -> (t.getTo().getMillis() - t.getFrom().getMillis()) / (1000 * 60)).sum();
        long breakMinutes = timeTracks.stream().mapToLong(
                t -> t.getBreaks().stream().mapToLong(
                        b -> (b.getTo().getMillis() - b.getFrom().getMillis()) / (1000 * 60)
                ).sum()
        ).sum();


        int usedHolidayDays = 0;
        int acceptedHolidayDays = 0;
        int sickDays = 0;
        int businessTripDays = 0;
        int specialHolidayDays = 0;
        int bankHolidayDays = 0;
        int educationalLeaveDays = 0;
        int parentalLeaveDays = 0;

        for (TimeOff t : timeOffs) {
            if (t.getType() == TimeOffType.SICK_LEAVE) {
                sickDays += getWorkdaysOfTimeOff(t);
            }
            if (t.getType() == TimeOffType.BUSINESS_TRIP) {
                businessTripDays += getWorkdaysOfTimeOff(t);
            }
            if (t.getType() == TimeOffType.BANK_HOLIDAY) {
                bankHolidayDays += getWorkdaysOfTimeOff(t);
            }
            if (t.getType() == TimeOffType.PARENTAL_LEAVE) {
                parentalLeaveDays += getWorkdaysOfTimeOff(t);
            }
            if ((t.getType() == TimeOffType.HOLIDAY) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                usedHolidayDays += getWorkdaysOfTimeOff(t);
                acceptedHolidayDays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }
            if ((t.getType() == TimeOffType.SPECIAL_HOLIDAY) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                specialHolidayDays += getWorkdaysOfTimeOff(t);
            }
            if ((t.getType() == TimeOffType.EDUCATIONAL_LEAVE) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                educationalLeaveDays += getWorkdaysOfTimeOff(t);
            }

        }

        long workMinutesPerDay =  60 * (long) user.getHoursPerDay();
        long workMinutesShould = workMinutesPerDay * (getWorkdaysOfThisYearUpToNow(user.getEntryDate())
                - sickDays
                - businessTripDays
                - bankHolidayDays
                - parentalLeaveDays
                - usedHolidayDays
                - specialHolidayDays
                - educationalLeaveDays);

        return new ReportEntry(
                user,
                user.getHoursPerDay(),
                acceptedHolidayDays,
                user.getHolidays() - acceptedHolidayDays,
                sickDays,
                workMinutesShould,
                workMinutesWithoutBreak - breakMinutes,
                breakMinutes);
    }


    @Override
    public List<ForbiddenWorkTimeAlert> createForbiddenWorkTimeAlerts(ReportEntry entry) {
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        User user = entry.getUser();

        // check working times regarding plus saldo
        long flexTimeSaldoInHours = entry.getWorkMinutesDifference() / 60;
        if(flexTimeSaldoInHours > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR) {
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_over_specified_percent",
                user.getFirstName(), "100"));
        }
        else if(flexTimeSaldoInHours > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.75) {
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_over_specified_percent",
                user.getFirstName(), "75"));
        }

        // check working times regarding minus saldo
        if(flexTimeSaldoInHours < CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR) {
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_under_specified_percent",
                user.getFirstName(), "100"));
        }
        else if(flexTimeSaldoInHours < CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.75) {
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_under_specified_percent",
                user.getFirstName(), "75"));
        }

        // check for break overuse and under consumption
        double workMinutesIs = entry.getWorkMinutesIs();
        double breakMinutesIs = entry.getBreakMinutes();
        if(breakMinutesIs * CollectiveConstants.WORKTIME_TO_BREAK_RATIO
            > workMinutesIs * (1 + CollectiveConstants.TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_overuses_breaks_regularly",
                user.getFirstName(), String.valueOf(breakMinutesIs * 100 / workMinutesIs)));
        } else if(breakMinutesIs * CollectiveConstants.WORKTIME_TO_BREAK_RATIO
            < workMinutesIs * (1 - CollectiveConstants.TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_underuses_breaks_regularly",
                user.getFirstName(), String.valueOf(breakMinutesIs * 100 / workMinutesIs)));
        }

        // check for holiday
        if(entry.getNumOfUsedHolidays() > user.getHolidays()) {
            alertList.add(createAlert("forbidden_worktime.more_holiday_used_than_available",
                user.getFirstName(), String.valueOf(entry.getNumOfUsedHolidays()), String.valueOf(user.getHolidays())));
        }
        if(entry.getNumOfUnusedHolidays() > CollectiveConstants.MAX_NUMBER_OF_UNUSED_HOLIDAY) {
            alertList.add(createAlert("forbidden_worktime.too_many_unused_holiday_available", user.getFirstName(),
                String.valueOf(entry.getNumOfUnusedHolidays())));
        }

        // check for sick leaves
        if(entry.getNumOfSickDays()
            > (entry.getWorkMinutesIs() * CollectiveConstants.TOLERATED_WORKTIME_TO_SICKLEAVE_RATIO) / (60 * entry.getHoursPerDay())) {
            alertList.add(createAlert("forbidden_worktime.user_has_many_sick_leaves", user.getFirstName()));
        }

        // TODO: add check for timeTracks where user exceeded MAX_HOURS_PER_DAY
        return alertList;
    }

    private ForbiddenWorkTimeAlert createAlert(String message, String... arguments) {
        ForbiddenWorkTimeAlert alert = new ForbiddenWorkTimeAlert(message);
        for(String arg : arguments) {
            alert.addArguments(arg);
        }
        return alert;
    }

    private static int getWorkdaysOfThisYearUpToNow(DateTime entryDate) {
        DateTime january1st = DateTimeUtils.startOfActualYear();

        // If user joined company this year
        if (entryDate.getYear() == DateTime.now().getYear()) {
            return getWorkdaysOfTimeInterval(january1st, DateTime.now()) - entryDate.getDayOfYear();
        }
        return getWorkdaysOfTimeInterval(january1st, DateTime.now());
    }

    private static int getWorkdaysOfThisYear() {
        DateTime january1st = DateTimeUtils.startOfActualYear();
        DateTime december31th = DateTimeUtils.endOfActualYear();

        return getWorkdaysOfTimeInterval(january1st, december31th);
    }

    // This function only counts real work days, not the weekend
    private static int getWorkdaysOfTimeInterval(DateTime from, DateTime to) {
        int workdays = 0;
        for (int i = 0; i <= to.getDayOfYear() - from.getDayOfYear(); i++) {
            if (from.plusDays(i).getDayOfWeek() != 6 && from.plusDays(i).getDayOfWeek() != 7) {
                workdays++;
            }
        }
        return workdays;
    }

    private static int getWorkdaysOfTimeOff(TimeOff timeoff) {
        // TimeOff is already complete in history
        if (timeoff.getTo().isBeforeNow()) {
            return getWorkdaysOfTimeInterval(timeoff.getFrom(), timeoff.getTo());
        }
        // TimeOff has started, but not finished today
        if (timeoff.getFrom().isBeforeNow() && timeoff.getTo().isAfterNow()) {
            return getWorkdaysOfTimeInterval(timeoff.getFrom(), DateTime.now());
        }
        // If TimeOff is in future, do not consider now
        return 0;
    }

}
