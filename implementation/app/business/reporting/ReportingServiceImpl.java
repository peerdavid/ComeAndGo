package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.usermanagement.InternalUserManagement;
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
    public ReportingServiceImpl(InternalUserManagement userManagement, CollectiveAgreement collectiveAgreement, InternalTimeTracking internalTimeTracking){
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
    public Report createBossReport(int userId) throws Exception{
        List<User> employeesOfBoss = _userManagement.readUsersOfBoss(userId);
        return createReport(employeesOfBoss);
    }

    private Report createReport(List<User> users, DateTime to) throws Exception {
        List<ReportEntry> userReports = new ArrayList<>();

        for(User user : users){
            List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(user.getId(), DateTimeUtils.BIG_BANG, to);
            List<TimeOff> timeOffs = _internalTimeTracking.readTimeOffs(user.getId(), DateTimeUtils.BIG_BANG, to);
            List<Payout> payouts = _internalTimeTracking.readPayouts(user.getId());
            userReports.add(_collectiveAgreement.createUserReport(user, timeTracks, timeOffs, payouts));
        }

        ReportEntry summary = createCompanySummary(userReports);
        return new Report(userReports, summary);
    }

    private Report createReport(List<User> users) throws Exception{
        return createReport(users, DateTime.now());
    }

    @Override
    public List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime to) throws Exception {
        Report report = createReport(userList, to);
        List<ForbiddenWorkTimeAlert> alertList = new ArrayList<>();

        for(ReportEntry entry : report.getUserReports()) {
            alertList.addAll(_collectiveAgreement.createForbiddenWorkTimeAlerts(entry));
        }

        return alertList;
    }

    @Override
    public List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime to) throws Exception {
        User user = _userManagement.readUser(userId);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        return readForbiddenWorkTimeAlerts(userList, to);
    }

    @Override
    public double readHoursWorked(int userId) throws Exception {
        DateTime now = DateTime.now();
        List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(userId,
            DateTimeUtils.startOfDay(now), DateTimeUtils.endOfDay(now));

        float result = 0;
        for (TimeTrack timeTrack : timeTracks) {
            DateTime from = timeTrack.getFrom();
            DateTime to = timeTrack.getTo() == null ? now : timeTrack.getTo();
            result += getDifferenceOfMinutes(from, to, DateTimeConstants.MINUTES_PER_DAY);
            for (Break b : timeTrack.getBreaks()) {
                from = b.getFrom();
                to = b.getTo() == null ? now : b.getTo();
                result -= getDifferenceOfMinutes(from, to, DateTimeConstants.MINUTES_PER_DAY);
            }
        }

        return result / 60f;
    }

    private float getDifferenceOfMinutes(DateTime from, DateTime to, int unit) {
        float result = to.getMinuteOfDay() - from.getMinuteOfDay();
        // would be negative if working times go through midnight
        if(result < 0) {
            result = unit - result;
        }
        return result;
    }

    @Override
    public double readHoursWorkedProgress(int userId) throws Exception {
        User user = _userManagement.readUser(userId);

        double result = readHoursWorked(userId) / user.getHoursPerDay();

        return result < 0 ? 0 : result > 1 ? 1 : result;
    }


    private ReportEntry createCompanySummary(List<ReportEntry> userReports) {
        double hoursPerDay = userReports.stream().mapToDouble(d -> d.getHoursPerDay()).sum();
        int numOfUsedHolidays = userReports.stream().mapToInt(d -> d.getNumOfUsedHolidays()).sum();
        int numOfUnusedHolidays = userReports.stream().mapToInt(d -> d.getNumOfUnusedHolidays()).sum();
        int numOfSickDays = userReports.stream().mapToInt(d -> d.getNumOfSickDays()).sum();
        long numOfWorkMinutesShould = userReports.stream().mapToLong(d -> d.getWorkMinutesShould()).sum();
        long numOfWorkMinutesIs = userReports.stream().mapToLong(d -> d.getWorkMinutesIs()).sum();
        long numOfBreakMinutes = userReports.stream().mapToLong(d -> d.getBreakMinutes()).sum();

        return new ReportEntry(null, hoursPerDay, numOfUsedHolidays, numOfUnusedHolidays, numOfSickDays, numOfWorkMinutesShould, numOfWorkMinutesIs, numOfBreakMinutes);
    }
}
