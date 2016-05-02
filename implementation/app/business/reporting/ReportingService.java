package business.reporting;

import models.ReportEntry;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface ReportingService {

    List<ReportEntry> getCompanyReport() throws Exception;
}
