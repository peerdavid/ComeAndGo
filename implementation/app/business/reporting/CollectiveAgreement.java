package business.reporting;

import models.*;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface CollectiveAgreement {

    ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts, DateTime upperBound);

    void createForbiddenWorkTimeAlerts(ReportEntry entry, List<WorkTimeAlert> alertList);
    void checkWorkHoursOfDay(User user, double workedHoursOfDay, DateTime when, List<WorkTimeAlert> alertList);
    void checkFreeTimeHoursOfDay(User user, DateTime when, double durationFreeTimeOfActualDayInH, List<Double> durationWorkTimeNextDays, List<WorkTimeAlert> alertList);
    void checkFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClause(User user, DateTime when, double hoursWorkedActualDay, List<Double> workedHoursNextDays, List<WorkTimeAlert> alertList);
}
