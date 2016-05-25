package business.reporting;

import models.*;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface CollectiveAgreement {

    /**
     * creates a user report
     * @param user to create report
     * @param timeTracks list of timeTracks to use for calculation
     * @param timeOffs list of timeOffs to use for calculation
     * @param payouts list of payouts to use for calculation
     * @param upperBound date to specify end of calculation (always starting at users entry date)
     * @return the reportEntry requested
     */
    ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts, DateTime upperBound);

    /**
     * takes entry and checks for all possible workTimeAlerts like e.g. break overuse (under-use), sickLeave, holiday, flextime hours
     * @param entry the reportEntry to check
     * @param alertList the list to add resulting workTimeAlerts
     */
    void createForbiddenWorkTimeAlerts(ReportEntry entry, List<WorkTimeAlert> alertList);

    /**
     *
     * @param user to check
     * @param workedHoursOfDay amount of hours worked on day specified by "when"
     * @param when day of check
     * @param alertList list to add resulting workTimeAlerts
     */
    void createWorkHoursOfDayAlerts(User user, double workedHoursOfDay, DateTime when, List<WorkTimeAlert> alertList);

    /** checks if user has had enough freeTime between his workDays.
     *  @param user user to check
     * @param when check is starting
     * @param durationWorkTimeNextDays list of hours worked starting at "when" and at least contains 11 days including when
     * @param alertList the alertList to add resulting workTimeAlerts
     */
    void createFreeTimeHoursOfDayAlerts(User user, DateTime when, List<Double> durationWorkTimeNextDays, List<WorkTimeAlert> alertList);

    /**
     * checks if user does not exceed maximum amount of work days per week, and if user has at least free on one day of christmas or New Years Eve
     * @param user to check
     * @param when which day should start the check
     * @param workedHoursNextDays list of hours worked starting at "when" and at least contains 11 days including when
     * @param alertList the alertList to add resulting workTimeAlerts
     */
    void createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(User user, DateTime when, List<Double> workedHoursNextDays, List<WorkTimeAlert> alertList);
}
