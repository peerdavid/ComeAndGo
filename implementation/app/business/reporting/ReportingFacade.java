package business.reporting;

import business.usermanagement.UserException;
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
    private WorkTimeCheckService _workTimeCheck;


    @Inject
    public ReportingFacade(ReportingService reportingService, WorkTimeCheckService workTimeCheck){
        _reportingService = reportingService;
        _workTimeCheck = workTimeCheck;
    }


    @Override
    public Report createCompanyReport(DateTime from, DateTime to) throws Exception {
        return _reportingService.createCompanyReport(from, to);
    }

    @Override
    public Report createEmployeeReport(int userId, DateTime from, DateTime to) throws Exception {
        return _reportingService.createEmployeeReport(userId, from, to);
    }

    @Override
    public Report createBossReport(int userId, DateTime from, DateTime to) throws Exception {
        return _reportingService.createBossReport(userId, from, to);
    }

    @NoLogging
    @NoTransaction
    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime from, DateTime to, int actualUserId) throws Exception {
        return _workTimeCheck.readForbiddenWorkTimeAlerts(userId, from, to);
    }

    @NoLogging
    @NoTransaction
    @Override
    public List<WorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime from, DateTime to, int actualUserId) throws Exception {
        return _workTimeCheck.readForbiddenWorkTimeAlerts(userList, from, to);
    }

    @Override
    public double readHoursWorked(int userId, DateTime when) throws Exception {
        return _reportingService.readHoursWorked(userId, when);
    }

    @Override
    public double readHoursWorkedProgress(int userId) throws Exception {
        return _reportingService.readHoursWorkedProgress(userId);
    }

    @NoLogging
    @NoTransaction
    @Override
    public double calculateOvertime(int userId, DateTime when) throws Exception {
        return _reportingService.calculateOvertime(userId, when);
    }
}
