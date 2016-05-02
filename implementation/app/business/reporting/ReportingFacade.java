package business.reporting;

import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.ReportEntry;
import models.User;

import java.util.ArrayList;
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
    public List<ReportEntry> createCompanyReport() throws Exception {
        return _reportingService.getCompanyReport();
    }
}
