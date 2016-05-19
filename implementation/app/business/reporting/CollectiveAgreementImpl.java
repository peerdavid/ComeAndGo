package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import com.google.inject.Inject;
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
                acceptedHolidayDays += DateTimeUtils.getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }
            if ((t.getType() == TimeOffType.SPECIAL_HOLIDAY) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                specialHolidayDays += getWorkdaysOfTimeOff(t);
            }
            if ((t.getType() == TimeOffType.EDUCATIONAL_LEAVE) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                educationalLeaveDays += getWorkdaysOfTimeOff(t);
            }

        }

        long workMinutesPerDay =  60 * (long) user.getHoursPerDay();
        long workDaysRespected = DateTimeUtils.getWorkdaysOfThisYearUpToNow(user.getEntryDate());
        long workMinutesShould = workMinutesPerDay * (workDaysRespected
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
                breakMinutes,
                workDaysRespected);
    }


    @Override
    public List<ForbiddenWorkTimeAlert> createForbiddenWorkTimeAlerts(ReportEntry entry) {
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        alertList.addAll(checkFlexTimeAlerts(entry));
        alertList.addAll(checkBreakOverAndUnderConsumptionAlerts(entry));
        alertList.addAll(checkHolidayAlerts(entry));
        alertList.addAll(checkSickDayAlerts(entry));
        return alertList;
    }

    private List<ForbiddenWorkTimeAlert> checkSickDayAlerts(ReportEntry entry) {
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        if(entry.getNumOfSickDays()
                > (entry.getWorkMinutesIs() * CollectiveConstants.TOLERATED_WORKTIME_TO_SICKLEAVE_RATIO) / (60 * entry.getHoursPerDay())) {
            alertList.add(createAlert("forbidden_worktime.user_has_many_sick_leaves",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser())));
        }
        return alertList;
    }

    private List<ForbiddenWorkTimeAlert> checkHolidayAlerts(ReportEntry entry) {
        User user = entry.getUser();
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        if(entry.getNumOfUsedHolidays() > user.getHolidays()) {
            alertList.add(createAlert("forbidden_worktime.more_holiday_used_than_available",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user),
                    String.valueOf(entry.getNumOfUsedHolidays()),
                    String.valueOf(user.getHolidays())));
        }
        if(entry.getNumOfUnusedHolidays() > CollectiveConstants.MAX_NUMBER_OF_UNUSED_HOLIDAY) {
            alertList.add(createAlert("forbidden_worktime.too_many_unused_holiday_available",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user),
                    String.valueOf(entry.getNumOfUnusedHolidays())));
        }
        return alertList;
    }

    private List<ForbiddenWorkTimeAlert> checkBreakOverAndUnderConsumptionAlerts(ReportEntry entry) {
        double workMinutesIs = entry.getWorkMinutesIs();
        double breakMinutesIs = entry.getBreakMinutes();

        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        if(breakMinutesIs * CollectiveConstants.WORKTIME_TO_BREAK_RATIO
                > workMinutesIs * (1 + CollectiveConstants.TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_overuses_breaks_regularly",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    String.valueOf(breakMinutesIs * 100 / workMinutesIs)));
        } else if(breakMinutesIs * CollectiveConstants.WORKTIME_TO_BREAK_RATIO
                < workMinutesIs * (1 - CollectiveConstants.TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_underuses_breaks_regularly",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    String.valueOf(breakMinutesIs * 100 / workMinutesIs)));
        }
        return alertList;
    }

    private List<ForbiddenWorkTimeAlert> checkFlexTimeAlerts(ReportEntry entry) {
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        long flexTimeSaldoInHours = entry.getWorkMinutesDifference() / 60;
        if(flexTimeSaldoInHours > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR) {
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_over_specified_percent",
                    ForbiddenWorkTimeAlert.Type.URGENT,
                    userFirstAndLastName(entry.getUser()),
                    "100"));
        }
        else if(flexTimeSaldoInHours > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.75) {
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_over_specified_percent",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    "75"));
        }
        return alertList;
    }

    @Override
    public List<ForbiddenWorkTimeAlert> checkWorkHoursOfDay(User user, double workedHoursOfDay, DateTime when) {
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();
        if(workedHoursOfDay > CollectiveConstants.MAX_HOURS_PER_DAY) {
            alertList.add(createAlert("forbidden_worktime.user_exceeded_daily_worktime",
                    ForbiddenWorkTimeAlert.Type.WARNING,
                    user.getFirstName() + " " + user.getLastName(),
                    DateTimeUtils.dateTimeToDateString(when),
                    String.valueOf(CollectiveConstants.MAX_HOURS_PER_DAY - workedHoursOfDay)));
        }
        return alertList;
    }

    private String userFirstAndLastName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private ForbiddenWorkTimeAlert createAlert(String message, ForbiddenWorkTimeAlert.Type type, String... arguments) {
        ForbiddenWorkTimeAlert alert = new ForbiddenWorkTimeAlert(message, type);
        for(String arg : arguments) {
            alert.addArguments(arg);
        }
        return alert;
    }



    private static int getWorkdaysOfTimeOff(TimeOff timeoff) {
        // TimeOff is already complete in history
        if (timeoff.getTo().isBeforeNow()) {
            return DateTimeUtils.getWorkdaysOfTimeInterval(timeoff.getFrom(), timeoff.getTo());
        }
        // TimeOff has started, but not finished today
        if (timeoff.getFrom().isBeforeNow() && timeoff.getTo().isAfterNow()) {
            return DateTimeUtils.getWorkdaysOfTimeInterval(timeoff.getFrom(), DateTime.now());
        }
        // If TimeOff is in future, do not consider now
        return 0;
    }

}
