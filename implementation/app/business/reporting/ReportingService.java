package business.reporting;

import business.usermanagement.UserException;
import models.Report;

/**
 * Created by david on 02.05.16.
 */
interface ReportingService {

    Report getCompanyReport() throws Exception;

    Report createEmployeeReport(int userId) throws Exception;

    Report createBossReport(int userId) throws Exception;

    double readHoursWorkedProgress(int userId) throws Exception;

    double readHoursWorked(int userId) throws Exception;
}
