package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class ReportingServiceImpl implements ReportingService {

    private InternalTimeTracking _internalTimeTracking;
    private InternalUserManagement _userManagement;
    private CollectiveAgreement _collectiveAgreement;


    @Inject
    public ReportingServiceImpl(InternalUserManagement userManagement, CollectiveAgreement collectiveAgreement, InternalTimeTracking internalTimeTracking) {
        _userManagement = userManagement;
        _collectiveAgreement = collectiveAgreement;
        _internalTimeTracking = internalTimeTracking;
    }


    @Override
    public Report createCompanyReport(DateTime from, DateTime to) throws Exception {
        List<User> users = _userManagement.readUsers();
        return createReport(users, from, to);
    }


    @Override
    public Report createEmployeeReport(int userId, DateTime from, DateTime to) throws Exception {
        List<User> users = new ArrayList<>();
        users.add(_userManagement.readUser(userId));
        return createReport(users, from, to);
    }


    @Override
    public Report createBossReport(int userId, DateTime from, DateTime to) throws Exception {
        List<User> employeesOfBoss = _userManagement.readUsersOfBoss(userId);
        return createReport(employeesOfBoss, from, to);
    }

    private Report createReport(List<User> users, DateTime from, DateTime to) throws Exception {
        List<ReportEntry> userReports = new ArrayList<>();

        if (to.isBefore(from)) {
            throw new UserException("exceptions.reporting.to_before_from");
        }

        for(User user : users) {
            List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(user.getId(), user.getEntryDate(), to);
            List<TimeOff> timeOffs = _internalTimeTracking.readTimeOffs(user.getId(), user.getEntryDate(), to);
            List<Payout> payouts = _internalTimeTracking.readPayouts(user.getId());

            if (from.isBefore(user.getEntryDate())) {
                from = user.getEntryDate();
            }
            userReports.add(subtractReports(_collectiveAgreement.createUserReport(user, timeTracks, timeOffs, payouts, from),
                    _collectiveAgreement.createUserReport(user, timeTracks, timeOffs, payouts, to)));
        }

        ReportEntry summary = createCompanySummary(userReports);
        return new Report(userReports, summary);
    }

    /**
     *
     * this method checks user TimeTracks if it exceeds MAX_HOURS_PER_DAY from CollectiveConstants
     * It cuts timeTracks which are over midnight and calculates only times regarding that day
     * starting at 0.00 and ending at 23.59
     *
     * @param userId
     * @param when
     * @return amounts of minutes worked on that day
     * @throws Exception
     */
    public double readHoursWorked(int userId, DateTime when) throws Exception {
        DateTime startOfDay = DateTimeUtils.startOfDay(when);
        DateTime endOfDay = DateTimeUtils.endOfDay(when);

        List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(userId, startOfDay, endOfDay);

        double result = 0;
        for (TimeTrack timeTrack : timeTracks) {
            DateTime from = timeTrack.getFrom().isBefore(startOfDay) ? startOfDay : timeTrack.getFrom();
            DateTime to = timeTrack.getTo() == null ? DateTime.now() : (timeTrack.getTo().isAfter(endOfDay) ? endOfDay : timeTrack.getTo());
            result += to.getMinuteOfDay() - from.getMinuteOfDay();
            for (Break b : timeTrack.getBreaks()) {
                from = b.getFrom().isBefore(startOfDay) ? startOfDay : b.getFrom();
                to = b.getTo() == null ? DateTime.now() : (b.getTo().isAfter(endOfDay) ? endOfDay : b.getTo());
                result -= to.getMinuteOfDay() - from.getMinuteOfDay();
            }
        }
        return result / 60;
    }

    @Override
    public double readHoursWorkedProgress(int userId) throws Exception {
        User user = _userManagement.readUser(userId);
        double result = readHoursWorked(userId, DateTime.now()) / user.getHoursPerDay();
        return result < 0 ? 0 : result > 1 ? 1 : result;
    }

    @Override
    public double calculateOvertime(int userId, DateTime when) throws Exception {
        User user = _userManagement.readUser(userId);

        double result = readHoursWorked(userId, when) - user.getHoursPerDay();

        return result <= 0 ? 0 : result;
    }


    private ReportEntry createCompanySummary(List<ReportEntry> userReports) {
        double hoursPerDay = userReports.stream().mapToDouble(d -> d.getHoursPerDay()).sum();
        int numOfUsedHolidays = userReports.stream().mapToInt(d -> d.getNumOfUsedHolidays()).sum();
        double numOfUnusedHolidays = userReports.stream().mapToDouble(d -> d.getNumOfUnusedHolidays()).sum();
        int numOfSickDays = userReports.stream().mapToInt(d -> d.getNumOfSickDays()).sum();
        long numOfWorkMinutesShould = userReports.stream().mapToLong(d -> d.getWorkMinutesShould()).sum();
        long numOfWorkMinutesIs = userReports.stream().mapToLong(d -> d.getWorkMinutesIs()).sum();
        long numOfBreakMinutes = userReports.stream().mapToLong(d -> d.getBreakMinutes()).sum();
        long numOfHolidayPayoutHours = userReports.stream().mapToLong(d -> d.getHolidayPayoutHours()).sum();
        long numOfOvertimePayoutHours = userReports.stream().mapToLong(d -> d.getOvertimePayoutHours()).sum();
        int workDaysRespected = userReports.stream().mapToInt(d -> d.getWorkdaysOfReport()).sum();

        return new ReportEntry(null, hoursPerDay, numOfUsedHolidays, numOfUnusedHolidays, numOfSickDays,
                numOfWorkMinutesShould, numOfWorkMinutesIs, numOfBreakMinutes, numOfHolidayPayoutHours,
                numOfOvertimePayoutHours, workDaysRespected);
    }
    
    private static ReportEntry subtractReports(ReportEntry from, ReportEntry to) {
        return new ReportEntry(
                from.getUser(), from.getUser().getHoursPerDay(),
                to.getNumOfUsedHolidays() - from.getNumOfUsedHolidays(),
                to.getNumOfUnusedHolidays() - from.getNumOfUnusedHolidays(),
                to.getNumOfSickDays() - from.getNumOfSickDays(),
                to.getWorkMinutesShould() - from.getWorkMinutesShould(),
                to.getWorkMinutesIs() - from.getWorkMinutesIs(),
                to.getBreakMinutes() - from.getBreakMinutes(),
                to.getHolidayPayoutHours() - from.getHolidayPayoutHours(),
                to.getOvertimePayoutHours() - from.getOvertimePayoutHours(),
                to.getWorkdaysOfReport() - from.getWorkdaysOfReport());
    }


}
