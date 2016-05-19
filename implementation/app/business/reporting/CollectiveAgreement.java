package business.reporting;

import models.*;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface CollectiveAgreement {

    ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts);

    List<WorkTimeAlert> createForbiddenWorkTimeAlerts(ReportEntry entry);
    List<WorkTimeAlert> checkWorkHoursOfDay(User user, double workedHoursOfDay, DateTime when);
    List<WorkTimeAlert> checkFreeTimeHoursOfDay(User user, DateTime when, double durationFreeTimeOfActualDayInH, double durationFreeTimeNext10DaysInH);
}
