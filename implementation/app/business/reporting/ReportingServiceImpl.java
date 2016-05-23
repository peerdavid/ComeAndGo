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
    public Report getCompanyReport() throws Exception {
        List<User> users = _userManagement.readUsers();
        return createReport(users);
    }


    @Override
    public Report createEmployeeReport(int userId) throws Exception {
        List<User> users = new ArrayList<>();
        users.add(_userManagement.readUser(userId));
        return createReport(users);
    }


    @Override
    public Report createBossReport(int userId) throws Exception {
        List<User> employeesOfBoss = _userManagement.readUsersOfBoss(userId);
        return createReport(employeesOfBoss);
    }

    private Report createReport(List<User> users, DateTime to) throws Exception {
        List<ReportEntry> userReports = new ArrayList<>();

        for(User user : users) {
            List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(user.getId(), DateTimeUtils.BIG_BANG, to);
            List<TimeOff> timeOffs = _internalTimeTracking.readTimeOffs(user.getId(), DateTimeUtils.BIG_BANG, to);
            List<Payout> payouts = _internalTimeTracking.readPayouts(user.getId());
            userReports.add(_collectiveAgreement.createUserReport(user, timeTracks, timeOffs, payouts, to));
        }

        ReportEntry summary = createCompanySummary(userReports);
        return new Report(userReports, summary);
    }

    private Report createReport(List<User> users) throws Exception {
        return createReport(users, DateTime.now());
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
        int numOfUnusedHolidays = userReports.stream().mapToInt(d -> d.getNumOfUnusedHolidays()).sum();
        int numOfSickDays = userReports.stream().mapToInt(d -> d.getNumOfSickDays()).sum();
        long numOfWorkMinutesShould = userReports.stream().mapToLong(d -> d.getWorkMinutesShould()).sum();
        long numOfWorkMinutesIs = userReports.stream().mapToLong(d -> d.getWorkMinutesIs()).sum();
        long numOfBreakMinutes = userReports.stream().mapToLong(d -> d.getBreakMinutes()).sum();
        long workDaysRespected = userReports.stream().mapToLong(d -> d.getWorkDaysRespected()).sum();

        return new ReportEntry(null, hoursPerDay, numOfUsedHolidays, numOfUnusedHolidays, numOfSickDays,
                numOfWorkMinutesShould, numOfWorkMinutesIs, numOfBreakMinutes, workDaysRespected);
    }
}
