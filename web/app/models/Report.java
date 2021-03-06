package models;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
public class Report {

    private List<ReportEntry> userReports;
    private ReportEntry summary;


    public Report(List<ReportEntry> userReports, ReportEntry summary) {
        this.userReports = userReports;
        this.summary = summary;
    }


    public List<ReportEntry> getUserReports() {
        return userReports;
    }


    public ReportEntry getSummary() {
        return summary;
    }
}
