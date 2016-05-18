package business.reporting;

import com.google.inject.Inject;
import models.Report;
import models.User;
import org.joda.time.DateTime;
import utils.aop.NoLogging;
import utils.aop.NoTransaction;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class ReportingFacade implements Reporting {

    private ReportingService _reportingService;


    @Inject
    public ReportingFacade(ReportingService reportingService){
        _reportingService = reportingService;
    }


    @Override
    public Report createCompanyReport() throws Exception {
        return _reportingService.getCompanyReport();
    }

    @Override
    public Report createEmployeeReport(int userId) throws Exception {
        return _reportingService.createEmployeeReport(userId);
    }

    @Override
    public Report createBossReport(int userId) throws Exception {
        return _reportingService.createBossReport(userId);
    }

    @Override
    public List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime to) throws Exception {
        return _reportingService.readForbiddenWorkTimeAlerts(userList, to);
    }

    @Override
    public List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime to) throws Exception {
        return _reportingService.readForbiddenWorkTimeAlerts(userId, to);
    }

    @NoLogging
    @NoTransaction
    @Override
    public double readHoursWorkedProgress(int userId) throws Exception {
        return _reportingService.readHoursWorkedProgress(userId);
    }

    @Override
    public double readHoursWorked(int userId) throws Exception {
        return _reportingService.readHoursWorked(userId);
    }
}
