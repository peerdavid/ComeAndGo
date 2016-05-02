package business.reporting;

import com.google.inject.Inject;
import models.Report;

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
}
