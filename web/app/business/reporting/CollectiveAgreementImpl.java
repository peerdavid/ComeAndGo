package business.reporting;

import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import models.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import utils.DateTimeUtils;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAgreementImpl implements CollectiveAgreement {

    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts, DateTime upperBound) throws Exception {

        // TimeTrack calculation
        long workMinutesIs = 0;
        long breakMinutes = 0;
        for (TimeTrack t : timeTracks) {
            // If Timetrack has no To date
            if (t.getTo() == null) {
                t.setTo(DateTime.now());
            }

            // TimeTrack is already in history
            if (t.getTo().isBefore(upperBound)) {
                workMinutesIs += (t.getTo().getMillis() - t.getFrom().getMillis()) / (1000 * 60);
                List<Break> breaks = t.getBreaks();
                for (Break b : breaks) {
                    DateTime breakStart = b.getFrom();
                    DateTime breakEnd = (breakEnd = b.getTo()) == null ? DateTime.now() : breakEnd;

                    //boolean breakOverMidnight = isBreakOverMidnight(t, b);
                    //long breakMinutesDifference = breakEnd.getMillisOfDay() - breakStart.getMillisOfDay();
                    //if(breakOverMidnight) {
                    //    breakMinutesDifference = DateTimeConstants.MILLIS_PER_DAY
                    //            - breakEnd.getMillisOfDay() - breakStart.getMillisOfDay();
                    //}
                    breakMinutes += (breakEnd.getMillis() - breakStart.getMillis()) / (1000 * 60);
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

        // TimeOff calculation
        int usedHolidayDays = 0;
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
            }
            if ((t.getType() == TimeOffType.SPECIAL_HOLIDAY) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                specialHolidayDays += getWorkdaysOfTimeOff(t, upperBound);
            }
            if ((t.getType() == TimeOffType.EDUCATIONAL_LEAVE) && (t.getState() == RequestState.REQUEST_ACCEPTED)) {
                educationalLeaveDays += getWorkdaysOfTimeOff(t, upperBound);
            }

        }

        // Payout calculation
        int holidayPayoutHours = 0;
        int overtimePayoutHours = 0;

        for (Payout p : payouts) {
            if ((p.getState() == RequestState.REQUEST_ACCEPTED) && (p.getCreatedOn().isBefore(upperBound))) {
                switch (p.getType()) {
                    case HOLIDAY_PAYOUT:
                        holidayPayoutHours += p.getAmount() * p.getUser().getHoursPerDay();
                        break;
                    case OVERTIME_PAYOUT:
                        overtimePayoutHours += p.getAmount();
                        break;
                }
            }

        }
        long payoutMinutes = 60 * (holidayPayoutHours + overtimePayoutHours);

        // Should-Calculation
        long workMinutesPerDay =  60 * (long) user.getHoursPerDay();
        int workDaysFromEntryToUpperBound = DateTimeUtils.getWorkdaysOfTimeInterval(user.getEntryDate(), upperBound);
        long workMinutesShould = workMinutesPerDay * (workDaysFromEntryToUpperBound
                - sickDays
                - businessTripDays
                - bankHolidayDays
                - parentalLeaveDays
                - usedHolidayDays
                - specialHolidayDays
                - educationalLeaveDays)
                + payoutMinutes;

        double aliqouteHolidayDays = DateTimeUtils.getAliquoteHolidayDays(user.getEntryDate(), upperBound, user.getHolidays());
        double unusedHolidayDays = aliqouteHolidayDays - usedHolidayDays - (holidayPayoutHours / user.getHoursPerDay());


        return new ReportEntry(
                user,
                user.getHoursPerDay(),
                usedHolidayDays,
                unusedHolidayDays,
                sickDays,
                workMinutesShould,
                workMinutesIs - breakMinutes,
                breakMinutes,
                holidayPayoutHours,
                overtimePayoutHours,
                workDaysFromEntryToUpperBound);
    }

    @Override
    public void createGeneralWorkTimeAlerts(ReportEntry entry, List<WorkTimeAlert> alertList) {
        createFlexTimeAlerts(entry, alertList);
        createBreakOverAndUnderConsumptionAlerts(entry, alertList);
        createHolidayAlerts(entry, alertList);
        createSickDayAlerts(entry, alertList);
    }

    private boolean isBreakOverMidnight(TimeTrack timeTrack, Break breakToCheck) {
        // if timeTrack is not over midnight, also break can't be over midnight
        DateTime timeTrackStart = timeTrack.getFrom();
        DateTime timeTrackEnd = timeTrack.getTo() == null ? DateTime.now() : timeTrack.getTo();
        if(timeTrackEnd.isBefore(DateTimeUtils.endOfDay(timeTrackStart))) {
            return false;
        }
        // but if timeTrack is over midnight, we have also to check break
        LocalTime breakStart = breakToCheck.getFrom().toLocalTime();
        LocalTime breakEnd = breakToCheck.getTo() == null ? LocalTime.now() : breakToCheck.getTo().toLocalTime();
        if(breakStart.isBefore(breakEnd)) {
            return false;
        }
        return true;
    }

    private void createSickDayAlerts(ReportEntry entry, List<WorkTimeAlert> alertList) {
        // this check finds out if user has exceeded the tolerated number of sick days per month
        //      for calculation we use average amount of sick days per month and compare with constant
        //      we get allowed sick days for X-day-report as follows:
        //      allowed = (daysObserved / (daysPerYear / 12)) * toleratedPerMonth
        double allowedNumbOfSickDaysForObservedReport =
                ((12 * entry.getWorkdaysOfReport()) / DateTimeUtils.getWorkdaysOfThisYear()) * CollectiveConstants.TOLERATED_SICKLEAVE_DAYS_PER_MONTH;
        if(entry.getNumOfSickDays() > allowedNumbOfSickDaysForObservedReport) {
            alertList.add(createAlert("forbidden_worktime.user_has_many_sick_leaves",
                    WorkTimeAlert.Type.URGENT,
                    userFirstAndLastName(entry.getUser())));
        }
    }

    private void createHolidayAlerts(ReportEntry entry, List<WorkTimeAlert> alertList) {
        // first holiday observation checks, if user uses more holiday than he is owning
        //   for a specific timeSpan, it is relevant to check the user's holiday he gets aliquot for that timeSpan
        //   AND how many holiday he has collected before timeSpan!
        User user = entry.getUser();
        double holidayAliquotForObservedAmountOfReportDays =
                (entry.getWorkdaysOfReport() / DateTimeUtils.getWorkdaysOfThisYear()) * user.getHolidays();
        if (entry.getNumOfUsedHolidays() > holidayAliquotForObservedAmountOfReportDays + entry.getNumOfUnusedHolidays()) {
            alertList.add(createAlert("forbidden_worktime.more_holiday_used_than_available",
                    WorkTimeAlert.Type.URGENT,
                    userFirstAndLastName(user),
                    valueToString(entry.getNumOfUsedHolidays(), 1),
                    valueToString(user.getHolidays(), 1)));
        }
        // second holiday observation checks if user has too many unused holiday left
        //      for that we only have to check if the holiday collected is exceeding a constant, independent on the timeSpan observed
        if (entry.getNumOfUnusedHolidays() > CollectiveConstants.MAX_NUMBER_OF_UNUSED_HOLIDAY_PER_YEAR) {
            alertList.add(createAlert("forbidden_worktime.too_many_unused_holiday_available",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user),
                    valueToString(entry.getNumOfUnusedHolidays(), 1)));
        }
    }

    private void createBreakOverAndUnderConsumptionAlerts(ReportEntry entry, List<WorkTimeAlert> alertList) {
        // takes average value of break per day to ensure user does not overuse...
        double workMinutesIs = entry.getWorkMinutesIs();
        double breakMinutesIs = entry.getBreakMinutes();

        if(breakMinutesIs / entry.getWorkdaysOfReport()
                >= CollectiveConstants.BREAK_MINUTES_PER_DAY * (1 + CollectiveConstants.TOLERATED_BREAK_OVERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_overuses_breaks_regularly",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    percentageToString(100 * breakMinutesIs / workMinutesIs)));
        }
        if(breakMinutesIs / entry.getWorkdaysOfReport()
                < CollectiveConstants.BREAK_MINUTES_PER_DAY * (1 - CollectiveConstants.TOLERATED_BREAK_UNDERUSE_PERCENTAGE)) {
            alertList.add(createAlert("forbidden_worktime.user_underuses_breaks_regularly",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(entry.getUser()),
                    percentageToString(100 * breakMinutesIs / workMinutesIs)));
        }
    }

    private void createFlexTimeAlerts(ReportEntry entry, List<WorkTimeAlert> alertList) {
        WorkTimeAlert.Type typeToInsert;
        String valueToInsert;

        // scale flexTimeSaldo depending on watched timeSpan and pull it up to a year
        int workDaysRespected = entry.getWorkdaysOfReport();
        long workDaysInYear = DateTimeUtils.getWorkdaysOfThisYear();
        long flexTimeSaldoInHoursScaledToYear = (entry.getWorkMinutesDifference() / 60) * workDaysInYear / workDaysRespected;

        // check for work time overshoot
        if(flexTimeSaldoInHoursScaledToYear > CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.75) {
            typeToInsert = WorkTimeAlert.Type.WARNING;
            valueToInsert = percentageToString(75);

            if(flexTimeSaldoInHoursScaledToYear >= CollectiveConstants.MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR) {
                typeToInsert = WorkTimeAlert.Type.URGENT;
                valueToInsert = percentageToString(100);
            }
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_over_specified_percent",
                    typeToInsert, userFirstAndLastName(entry.getUser()), valueToInsert));
        }

        // check for work time undershoot
        if(flexTimeSaldoInHoursScaledToYear < CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR * 0.75) {
            typeToInsert = WorkTimeAlert.Type.WARNING;
            valueToInsert = percentageToString(75);
            if(flexTimeSaldoInHoursScaledToYear <= CollectiveConstants.MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR) {
                typeToInsert = WorkTimeAlert.Type.URGENT;
                valueToInsert = percentageToString(100);
            }
            alertList.add(createAlert("forbidden_worktime.flextime_saldo_under_specified_percent",
                    typeToInsert, userFirstAndLastName(entry.getUser()), valueToInsert));
        }
    }

    @Override
    public void createWorkHoursOfDayAlerts(User user, double workedHoursOfDay, double hoursOfBreaksTaken, DateTime when, List<WorkTimeAlert> alertList) {
        // first check if user has exceeded the daily maximum of working hours
        if(workedHoursOfDay >= CollectiveConstants.MAX_HOURS_PER_DAY) {
            alertList.add(createAlert("forbidden_worktime.user_exceeded_daily_worktime",
                    WorkTimeAlert.Type.WARNING,
                    user.getFirstName() + " " + user.getLastName(),
                    DateTimeUtils.dateTimeToDateString(when),
                    valueToString(CollectiveConstants.MAX_HOURS_PER_DAY - workedHoursOfDay, 0)));
        }
        // then check if user is underneath the daily break consume:
        double minutesOfBreakExpected = CollectiveConstants.BREAK_MINUTES_PER_DAY * (1 - CollectiveConstants.TOLERATED_BREAK_UNDERUSE_PERCENTAGE);
        if(workedHoursOfDay > CollectiveConstants.NUMBER_OF_HOURS_TO_TAKE_BREAK && hoursOfBreaksTaken * 60 < minutesOfBreakExpected) {
            alertList.add(createAlert("forbidden_worktime.user_underused_break_on_date",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user),
                    valueToString(hoursOfBreaksTaken * 60, 0),
                    valueToString(minutesOfBreakExpected, 0),
                    DateTimeUtils.dateTimeToDateString(when)));
        }
    }

    @Override
    public void createFreeTimeHoursOfDayAlerts(User user, DateTime when, List<Double> durationWorkTimeNextDays, List<WorkTimeAlert> alertList) {
        if(durationWorkTimeNextDays == null || durationWorkTimeNextDays.isEmpty()) {
            alertList.add(createAlert("forbidden_worktime.error_in_checking_freetime_and_christmas_clause",
                    WorkTimeAlert.Type.INFORMATION, userFirstAndLastName(user), DateTimeUtils.dateTimeToDateString(when)));
            return;
        }

        // first check the times between the timeTracks if user keeps the defined times of freeTime defined in law
        double durationFreeTimeOfActualDayInH = DateTimeConstants.HOURS_PER_DAY - durationWorkTimeNextDays.get(0);
        double durationFreeTimeNext10DaysInH = 0d;
        // following loop sums up all freeTime hours starting tomorrow and ending at 10th day
        for(int i = 1; i < 11 && i < durationWorkTimeNextDays.size(); ++i) {
            durationFreeTimeNext10DaysInH += DateTimeConstants.HOURS_PER_DAY - durationWorkTimeNextDays.get(i);
        }
        int hoursBetweenTimeTracks = CollectiveConstants.MIN_HOURS_FREETIME_BETWEEN_WORKTIMES;
        if(durationFreeTimeOfActualDayInH < hoursBetweenTimeTracks
                && durationFreeTimeNext10DaysInH / 10 < hoursBetweenTimeTracks) {
            alertList.add(createAlert("forbidden_worktime.freetime_undershoot_with_next_days_no_balance",
                    WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user), DateTimeUtils.dateTimeToDateString(when),
                    valueToString(hoursBetweenTimeTracks - durationFreeTimeOfActualDayInH, 1)));
        }
        else if(durationFreeTimeOfActualDayInH < hoursBetweenTimeTracks) {
            alertList.add(createAlert("forbidden_worktime.freetime_undershoot", WorkTimeAlert.Type.WARNING,
                    userFirstAndLastName(user), DateTimeUtils.dateTimeToDateString(when),
                    valueToString(hoursBetweenTimeTracks - durationFreeTimeOfActualDayInH, 1)));
        }
    }

    @Override
    public void createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(User user, DateTime when, List<Double> workedHoursNextDays, List<WorkTimeAlert> alertList) {
        if(workedHoursNextDays == null || workedHoursNextDays.isEmpty()) {
            // do not add a alert here, because in previous check it would be already added...
            return;
        }

        /* check if user does only work on 5 days per week
         *    (but only check once a week to avoid multiple workTimeAlerts per week)
         */
        if(when.getDayOfWeek() == 1) {
            int daysWorked = 0;
            for(int i = 0; i < 7 && i < workedHoursNextDays.size(); ++i) {
                if(workedHoursNextDays.get(i) != 0) {
                    ++daysWorked;
                }
            }
            // now we know days worked in actual week
            if(daysWorked > CollectiveConstants.MAX_DAYS_OF_WORK_PER_WEEK) {
                alertList.add(createAlert("forbidden_worktime.too_many_workdays_per_week",
                        WorkTimeAlert.Type.WARNING,
                        userFirstAndLastName(user),
                        valueToString(daysWorked, 0),
                        valueToString(CollectiveConstants.MAX_DAYS_OF_WORK_PER_WEEK, 0),
                        DateTimeUtils.weekToString(when)));
            }
        }

        /* if we have christmas... check if user has not worked on new years eve
         *     important note: ordering in workedHoursNextDays is sequential as it was inserted (so it is day for day)
         */
        if(when.getDayOfMonth() == 24 && when.getMonthOfYear() == DateTimeConstants.DECEMBER) {
            double hoursWorkedActualDay = workedHoursNextDays.get(0);
            double hoursWorkedOnNewYearsEve = workedHoursNextDays.get(7);

            if(hoursWorkedActualDay > 0 && hoursWorkedOnNewYearsEve > 0) {
                alertList.add(createAlert("forbidden_worktime.worked_on_both_christmas_and_newyear_eve",
                        WorkTimeAlert.Type.WARNING,
                        userFirstAndLastName(user),
                        valueToString(when.getYear(), 0)));
            }
        }
    }

    private String userFirstAndLastName(User user) {
        return user.getFirstName() + " " + user.getLastName();
    }

    private WorkTimeAlert createAlert(String message, WorkTimeAlert.Type type, String... arguments) {
        return new WorkTimeAlert(message, type, arguments);
    }

    private String percentageToString(double percentage) {
        return String.format("%.0f", percentage) + "%";
    }

    private String valueToString(double value, int amountFloatingNumbers) {
        return String.format("%." + String.valueOf(amountFloatingNumbers) + "f", value);
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
