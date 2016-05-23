package business.reporting;

import business.usermanagement.UserException;
import models.Report;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface ReportingService {

    Report createCompanyReport(DateTime from, DateTime to) throws Exception;

    Report createEmployeeReport(int userId, DateTime from, DateTime to) throws Exception;

    Report createBossReport(int userId, DateTime from, DateTime to) throws Exception;

    double readHoursWorked(int userId, DateTime when) throws Exception;

    double readHoursWorkedProgress(int userId) throws Exception;

    double calculateOvertime(int userId, DateTime when) throws Exception;
}
