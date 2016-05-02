package business.reporting;

import models.CompanyReport;
import models.ReportEntry;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface ReportingService {

    CompanyReport getCompanyReport() throws Exception;
}
