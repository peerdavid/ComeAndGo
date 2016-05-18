package business.reporting;

import models.Report;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface ReportingService {

    Report getCompanyReport() throws Exception;

    Report createEmployeeReport(int userId) throws Exception;

    Report createBossReport(int userId) throws Exception;

    List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime from, DateTime to) throws Exception;
    List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(int userId) throws Exception;
    List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime to) throws Exception;

    double readHoursWorkedProgress(int userId) throws Exception;
}
