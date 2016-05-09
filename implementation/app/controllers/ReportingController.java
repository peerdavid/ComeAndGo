package controllers;

import business.reporting.Reporting;
import com.google.inject.Inject;
import models.Report;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;


/**
 * Created by david on 02.05.16.
 */
public class ReportingController extends UserProfileController<CommonProfile> {


    private Reporting _reporting;


    @Inject
    public ReportingController(Reporting reporting){
        _reporting = reporting;
    }


    @RequiresAuthentication(clientName = "default", authorizerName = "personal")
    public Result companyReport() throws Exception {
        CommonProfile profile = getUserProfile();

        Report report = _reporting.createCompanyReport();
        return ok(views.html.reporting.render(profile, report));
    }


    @RequiresAuthentication(clientName = "default")
    public Result employeeReport() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        Report report = _reporting.createEmployeeReport(userId);
        return ok(views.html.reporting.render(profile, report));
    }


    @RequiresAuthentication(clientName = "default", authorizerName = "boss")
    public Result bossReport() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        Report report = _reporting.createBossReport(userId);
        return ok(views.html.reporting.render(profile, report));
    }


    @RequiresAuthentication(clientName = "default")
    public Result requestHolidayPayout() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());


        Report report = _reporting.createEmployeeReport(userId);
        return ok(views.html.reporting.render(profile, report));
    }
}
