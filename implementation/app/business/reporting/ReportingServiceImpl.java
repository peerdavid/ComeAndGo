package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.CompanyReport;
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
    private UserManagement _userManagement;
    private CollectiveAggreement _collectiveAggreement;

    @Inject
    public ReportingServiceImpl(UserManagement userManagement, CollectiveAggreement collectiveAggreement, InternalTimeTracking internalTimeTracking){
        _userManagement = userManagement;
        _collectiveAggreement = collectiveAggreement;
        _internalTimeTracking = internalTimeTracking;
    }


    @Override
    public CompanyReport getCompanyReport() throws Exception {
        List<ReportEntry> userReports = new ArrayList<>();


        for(User user : _userManagement.readUsers()){
            List<TimeTrack> timeTracks = _internalTimeTracking.readTimeTracks(user.getId());
            userReports.add(_collectiveAggreement.createUserReport(user, timeTracks, null, null));
        }

        ReportEntry summary = createCompanySummary(userReports);
        return new CompanyReport(userReports, summary);
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
