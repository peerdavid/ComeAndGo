package business.reporting;

import models.ReportEntry;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
public interface Reporting {
    List<ReportEntry> createCompanyReport() throws Exception;
}
