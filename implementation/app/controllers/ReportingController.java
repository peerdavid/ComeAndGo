package controllers;

import business.reporting.Reporting;
import com.google.inject.Inject;
import models.ReportEntry;
import org.jetbrains.annotations.NotNull;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
public class ReportingController extends UserProfileController<CommonProfile> {


    private Reporting _reporting;


    @Inject
    public ReportingController(Reporting reporting){
        _reporting = reporting;
    }


    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();

        List<ReportEntry> companyReport = _reporting.createCompanyReport();
        ReportEntry summary = createCompanySummary(companyReport);

        return ok(views.html.reporting.render(profile, companyReport, summary));
    }



    private ReportEntry createCompanySummary(List<ReportEntry> companyReport) {
        double salary = companyReport.stream().mapToDouble(d -> d.getSalary()).sum();
        int numOfUsedHolidays = companyReport.stream().mapToInt(d -> d.getNumOfUsedHolidays()).sum();
        int numOfUnusedHolidays = companyReport.stream().mapToInt(d -> d.getNumOfUnusedHolidays()).sum();
        int numOfSickDays = companyReport.stream().mapToInt(d -> d.getNumOfSickDays()).sum();
        int numOfWorkHoursShould = companyReport.stream().mapToInt(d -> d.getWorkHoursShould()).sum();
        int numOfWorkHoursIs = companyReport.stream().mapToInt(d -> d.getWorkHoursIs()).sum();
        return new ReportEntry(null, salary, numOfUsedHolidays, numOfUnusedHolidays, numOfSickDays, numOfWorkHoursShould, numOfWorkHoursIs);
    }

}
