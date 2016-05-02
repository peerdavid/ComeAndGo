package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.usermanagement.InternalUserManagement;
import com.google.inject.Inject;
import models.Report;
import models.ReportEntry;
import models.TimeTrack;
import models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class ReportingServiceImpl implements ReportingService {

    private InternalTimeTracking _internalTimeTracking;
    private InternalUserManagement _userManagement;
    private CollectiveAggreement _collectiveAggreement;


    @Inject
    public ReportingServiceImpl(InternalUserManagement userManagement, CollectiveAggreement collectiveAggreement, InternalTimeTracking internalTimeTracking){
        _userManagement = userManagement;
        _collectiveAggreement = collectiveAggreement;
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


    private Report createReport(List<User> users) throws Exception{
        List<ReportEntry> userReports = new ArrayList<>();

        for(User user : users){
            List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(user.getId());
            userReports.add(_collectiveAggreement.createUserReport(user, timeTracks, null, null));
        }

        ReportEntry summary = createCompanySummary(userReports);
        return new Report(userReports, summary);
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
