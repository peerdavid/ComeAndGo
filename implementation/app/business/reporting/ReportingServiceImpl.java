package business.reporting;

import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.CompanyReport;
import models.ReportEntry;
import models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class ReportingServiceImpl implements ReportingService {

    private UserManagement _userManagement;
    private CollectiveAggreement _collectiveAggreement;

    @Inject
    public ReportingServiceImpl(UserManagement userManagement, CollectiveAggreement collectiveAggreement){

        _userManagement = userManagement;
        _collectiveAggreement = collectiveAggreement;
    }


    @Override
    public CompanyReport getCompanyReport() throws Exception {
        List<ReportEntry> userReports = new ArrayList<>();

        for(User user : _userManagement.readUsers()){
            userReports.add(_collectiveAggreement.createUserReport(user, null, null, null));
        }

        ReportEntry summary = createCompanySummary(userReports);
        return new CompanyReport(userReports, summary);
    }


    private ReportEntry createCompanySummary(List<ReportEntry> userReports) {
        double salary = userReports.stream().mapToDouble(d -> d.getSalary()).sum();
        int numOfUsedHolidays = userReports.stream().mapToInt(d -> d.getNumOfUsedHolidays()).sum();
        int numOfUnusedHolidays = userReports.stream().mapToInt(d -> d.getNumOfUnusedHolidays()).sum();
        int numOfSickDays = userReports.stream().mapToInt(d -> d.getNumOfSickDays()).sum();
        int numOfWorkHoursShould = userReports.stream().mapToInt(d -> d.getWorkHoursShould()).sum();
        int numOfWorkHoursIs = userReports.stream().mapToInt(d -> d.getWorkHoursIs()).sum();
        return new ReportEntry(null, salary, numOfUsedHolidays, numOfUnusedHolidays, numOfSickDays, numOfWorkHoursShould, numOfWorkHoursIs);
    }
}
