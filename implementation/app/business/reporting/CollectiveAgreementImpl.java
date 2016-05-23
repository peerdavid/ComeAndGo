package business.reporting;

import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import models.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAgreementImpl implements CollectiveAgreement {

    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts, DateTime upperBound) {

        long workMinutesIs = 0;
        long breakMinutes = 0;
        for (TimeTrack t : timeTracks) {
            // TimeTrack is already in history
            if (t.getTo().isBefore(upperBound)) {
                workMinutesIs += (t.getTo().getMillis() - t.getFrom().getMillis()) / (1000 * 60);
                List<Break> breaks = t.getBreaks();
                for (Break b : breaks) {
                    breakMinutes += (b.getTo().getMillis() - b.getFrom().getMillis()) / (1000 * 60);
                }
            }
            // upperBound is between Timetrack from and to
            if (t.getFrom().isBefore(upperBound) && t.getTo().isAfter(upperBound)) {
                workMinutesIs += (upperBound.getMillis() - t.getFrom().getMillis()) / (1000 * 60);
                List<Break> breaks = t.getBreaks();
                for (Break b : breaks) {
                    breakMinutes += (upperBound.getMillis() - b.getFrom().getMillis()) / (1000 * 60);
                }
            }

        }


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
                sickDays += getWorkdaysOfTimeOff(t, upperBound);
            }
            if (t.getType() == TimeOffType.BUSINESS_TRIP) {
                businessTripDays += getWorkdaysOfTimeOff(t, upperBound);
            }
            if (t.getType() == TimeOffType.BANK_HOLIDAY) {
                bankHolidayDays += getWorkdaysOfTimeOff(t, upperBound);
            }
            if (t.getType() == TimeOffType.PARENTAL_LEAVE) {
                parentalLeaveDays += getWorkdaysOfTimeOff(t, upperBound);
            }
            if ((t.getType() == TimeOffType.HOLIDAY) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                usedHolidayDays += getWorkdaysOfTimeOff(t, upperBound);
                acceptedHolidayDays += DateTimeUtils.getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }
            if ((t.getType() == TimeOffType.SPECIAL_HOLIDAY) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                specialHolidayDays += getWorkdaysOfTimeOff(t, upperBound);
            }
            if ((t.getType() == TimeOffType.EDUCATIONAL_LEAVE) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                educationalLeaveDays += getWorkdaysOfTimeOff(t, upperBound);
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
                workMinutesIs - breakMinutes,
                breakMinutes,
                workDaysRespected);
    }


    @Override
    public List<WorkTimeAlert> createForbiddenWorkTimeAlerts(ReportEntry entry) {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        alertList.addAll(checkFlexTimeAlerts(entry));
        alertList.addAll(checkBreakOverAndUnderConsumptionAlerts(entry));
        alertList.addAll(checkHolidayAlerts(entry));
        alertList.addAll(checkSickDayAlerts(entry));
        return alertList;
    }

    private List<WorkTimeAlert> checkSickDayAlerts(ReportEntry entry) {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        if(entry.getNumOfSickDays()
                > (entry.getWorkMinutesIs() * CollectiveConstants.TOLERATED_WORKTIME_TO_SICKLEAVE_RATIO) / (60 * entry.getHoursPerDay())) {
            alertList.add(createAlert("forbidden_worktime.user_has_many_sick_leaves",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser())));
        }
        return alertList;
    }

    private List<WorkTimeAlert> checkHolidayAlerts(ReportEntry entry) {
        User user = entry.getUser();
        List<WorkTimeAlert> alertList = new ArrayList<>();
        if(entry.getNumOfUsedHolidays() > user.getHolidays()) {
            alertList.add(createAlert("forbidden_worktime.more_holiday_used_than_available",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user),
                    String.valueOf(entry.getNumOfUsedHolidays()),
                    String.valueOf(user.getHolidays())));
        }
        if(entry.getNumOfUnusedHolidays() > CollectiveConstants.MAX_NUMBER_OF_UNUSED_HOLIDAY) {
            alertList.add(createAlert("forbidden_worktime.too_many_unused_holiday_available",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user),
                    String.valueOf(entry.getNumOfUnusedHolidays())));
        }
        return alertList;
    }

    private List<WorkTimeAlert> checkBreakOverAndUnderConsumptionAlerts(ReportEntry entry) {
        double workMinutesIs = entry.getWorkMinutesIs();
        double breakMinutesIs = entry.getBreakMinutes();

        List<WorkTimeAlert> alertList = new ArrayList<>();
        if(breakMinutesIs * CollectiveConstants.WORKTIME_TO_BREAK_RATIO
                > workMinutesIs * (1 + CollectiveConstants.TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_overuses_breaks_regularly",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    String.valueOf(breakMinutesIs * 100 / workMinutesIs)));
        } else if(breakMinutesIs * CollectiveConstants.WORKTIME_TO_BREAK_RATIO
                < workMinutesIs * (1 - CollectiveConstants.TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_underuses_breaks_regularly",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    String.valueOf(breakMinutesIs * 100 / workMinutesIs)));
        }
        return alertList;
    }

    private List<WorkTimeAlert> checkFlexTimeAlerts(ReportEntry entry) {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        WorkTimeAlert.Type typeToInsert;
        String valueToInsert;

        // scale flexTimeSaldo depending on watched timeSpan and pull it up to a year
        long workDaysRespected = entry.getWorkDaysRespected();
        long workDaysInYear = DateTimeUtils.getWorkdaysOfThisYear();
        long flexTimeSaldoInHoursScaledToYear = (entry.getWorkMinutesDifference() / 60) * workDaysInYear / workDaysRespected;

        // check for work time overshoot
        if(flexTimeSaldoInHoursScaledToYear > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR * 7.75) {
            typeToInsert = WorkTimeAlert.Type.WARNING;
            valueToInsert = "75";

            if(flexTimeSaldoInHoursScaledToYear > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR) {
                typeToInsert = WorkTimeAlert.Type.URGENT;
                valueToInsert = "100";
            }

            alertList.add(createAlert("forbidden_worktime.flextime_saldo_over_specified_percent",
                    typeToInsert, userFirstAndLastName(entry.getUser()), valueToInsert));
        }

        // check for work time undershoot
        if(flexTimeSaldoInHoursScaledToYear < CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.75) {
            typeToInsert = WorkTimeAlert.Type.WARNING;
            valueToInsert = "75";
            if(flexTimeSaldoInHoursScaledToYear < CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR) {
                typeToInsert = WorkTimeAlert.Type.URGENT;
                valueToInsert = "100";
            }

            alertList.add(createAlert("forbidden_worktime.flextime_saldo_under_specified_percent",
                    typeToInsert, userFirstAndLastName(entry.getUser()), valueToInsert));
        }

        return alertList;
    }

    @Override
    public List<WorkTimeAlert> checkWorkHoursOfDay(User user, double workedHoursOfDay, DateTime when) {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        if(workedHoursOfDay > CollectiveConstants.MAX_HOURS_PER_DAY) {
            alertList.add(createAlert("forbidden_worktime.user_exceeded_daily_worktime",
                    WorkTimeAlert.Type.WARNING,
                    user.getFirstName() + " " + user.getLastName(),
                    DateTimeUtils.dateTimeToDateString(when),
                    String.valueOf(CollectiveConstants.MAX_HOURS_PER_DAY - workedHoursOfDay)));
        }
        return alertList;
    }

    @Override
    public List<WorkTimeAlert> checkFreeTimeHoursOfDay(User user, DateTime when, double durationFreeTimeOfActualDayInH, List<Double> durationWorkTimeNextDays) {
        // first check the times between the timeTracks if user keeps the defined times of freeTime defined in law
        List<WorkTimeAlert> alertList = new ArrayList<>();
        double durationFreeTimeNext10DaysInH = 0d;
        for(Double workTimeAtDay : durationWorkTimeNextDays) {
            durationFreeTimeNext10DaysInH += DateTimeConstants.HOURS_PER_DAY - workTimeAtDay;
        }
        long hoursBetweenTimeTracks = CollectiveConstants.MIN_HOURS_FREETIME_BETWEEN_WORKTIMES;
        if(durationFreeTimeOfActualDayInH < hoursBetweenTimeTracks
                && durationFreeTimeNext10DaysInH / 10 < CollectiveConstants.MIN_HOURS_FREETIME_BETWEEN_WORKTIMES) {
            alertList.add(createAlert("forbidden_worktime.freetime_undershoot_with_next_days_no_balance",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user), DateTimeUtils.dateTimeToDateString(when),
                    String.valueOf(hoursBetweenTimeTracks - durationFreeTimeOfActualDayInH)));
        }
        else if(durationFreeTimeOfActualDayInH < CollectiveConstants.MIN_HOURS_FREETIME_BETWEEN_WORKTIMES) {
            alertList.add(createAlert("forbidden_worktime.freetime_undershoot", WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user), DateTimeUtils.dateTimeToDateString(when),
                    String.valueOf(hoursBetweenTimeTracks - durationFreeTimeOfActualDayInH)));
        }

        return alertList;
    }

    @Override
    public List<WorkTimeAlert> checkFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClause(User user, DateTime when, double hoursWorkedActualDay, List<Double> workedHoursNextDays) {
        List<WorkTimeAlert> alertList = new ArrayList<>();
        // check if user does only work on 5 days per week
        //      (but only check once a week to avoid multiple workTimeAlerts per week)
        if(when.getDayOfWeek() == 1) {
            int daysWorked = 0;
            for(int i = 0; i < 7; ++i) {
                if(workedHoursNextDays.get(i) != 0) {
                    ++daysWorked;
                }
            }
            // now we know days worked in actual week
            if(daysWorked > CollectiveConstants.MAX_DAYS_OF_WORK_PER_WEEK) {
                alertList.add(createAlert("forbidden_worktime.too_many_workdays_per_week",
                        WorkTimeAlert.Type.WARNING,
                        userFirstAndLastName(user),
                        String.valueOf(daysWorked),
                        String.valueOf(CollectiveConstants.MAX_DAYS_OF_WORK_PER_WEEK),
                        DateTimeUtils.weekToString(when)));
            }
        }

        // if we have christmas... check if user has not worked on new years eve
        //  important note: ordering in workedHoursNextDays is sequential as it was inserted (so it is day for day)
        if(when.getDayOfMonth() == 24 && when.getMonthOfYear() == DateTimeConstants.DECEMBER) {
            double hoursWorkedOnNewYearsEve = workedHoursNextDays.get(6);
            if((hoursWorkedActualDay >= user.getHoursPerDay() && hoursWorkedOnNewYearsEve > 0)
                    || (hoursWorkedActualDay > 0 && hoursWorkedOnNewYearsEve >= user.getHoursPerDay())) {
                alertList.add(createAlert("forbidden_worktime.worked_on_both_christmas_and_newyear_eve",
                        WorkTimeAlert.Type.WARNING,
                        userFirstAndLastName(user),
                        String.valueOf(when.getYear())));
            }
        }
        return alertList;
    }

    private String userFirstAndLastName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private WorkTimeAlert createAlert(String message, WorkTimeAlert.Type type, String... arguments) {
        WorkTimeAlert alert = new WorkTimeAlert(message, type);
        for(String arg : arguments) {
            alert.addArguments(arg);
        }
        return alert;
    }



    private static int getWorkdaysOfTimeOff(TimeOff timeoff, DateTime upperBound) {
        // TimeOff is already complete in history
        if (timeoff.getTo().isBefore(upperBound)) {
            return DateTimeUtils.getWorkdaysOfTimeInterval(timeoff.getFrom(), timeoff.getTo());
        }
        // TimeOff has started, but not finished today
        if (timeoff.getFrom().isBefore(upperBound) && timeoff.getTo().isAfter(upperBound)) {
            return DateTimeUtils.getWorkdaysOfTimeInterval(timeoff.getFrom(), upperBound);
        }
        // If TimeOff is in future, do not consider now
        return 0;
    }

}
