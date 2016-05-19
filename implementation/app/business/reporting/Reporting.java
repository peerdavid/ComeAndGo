package business.reporting;

import models.Report;
import models.User;
import org.joda.time.DateTime;

import java.util.List;


/**
 * Created by david on 02.05.16.
 */
public interface Reporting {
    Report createCompanyReport() throws Exception;

    Report createEmployeeReport(int userId) throws Exception;

    Report createBossReport(int userId) throws Exception;

   /**
    * starts calculation from begin where timeTracks exist, ends at to
    * @param to
    * @return List of ForbiddenWorkTimeAlerts
    * @throws Exception
    */
    List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(List<User> userList, DateTime from, DateTime to) throws Exception;
    List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(int userId) throws Exception;
    List<ForbiddenWorkTimeAlert> readForbiddenWorkTimeAlerts(int userId, DateTime to) throws Exception;

    double readHoursWorked(int userId, DateTime when) throws Exception;
    double readHoursWorkedProgress(int userId) throws Exception;
}
