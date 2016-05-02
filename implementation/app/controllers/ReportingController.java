package controllers;

import business.reporting.Reporting;
import com.google.inject.Inject;
import models.CompanyReport;
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


    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();

        CompanyReport companyReport = _reporting.createCompanyReport();
        return ok(views.html.reporting.render(profile, companyReport));
    }

}
