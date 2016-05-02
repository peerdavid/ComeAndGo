package business.reporting;

import models.Report;

/**
 * Created by david on 02.05.16.
 */
interface ReportingService {

    Report getCompanyReport() throws Exception;

    Report createEmployeeReport(int userId) throws Exception;

    Report createBossReport(int userId) throws Exception;
}
