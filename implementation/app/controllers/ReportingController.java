package controllers;

import business.reporting.Reporting;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import models.Payout;
import models.Report;
import models.User;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;


/**
 * Created by david on 02.05.16.
 */
public class ReportingController extends UserProfileController<CommonProfile> {


    private Reporting _reporting;
    private TimeTracking _timeTracking;
    private final Form<Payout> FORM = Form.form(Payout.class);

    @Inject
    public ReportingController(Reporting reporting, TimeTracking timeTracking){
        _reporting = reporting;
        _timeTracking = timeTracking;
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

        // todo: add cases for comment or amountOfHours can't be read (also in requestOverTimePayout)
        // todo: check return render scene (also in requestOverTimePayout)
        Form<Payout> form = FORM.bindFromRequest();
        String comment = form.data().get("comment");
        int amountOfHours = Integer.parseInt(
                form.data().get("numofhours")
        );

        _timeTracking.requestHolidayPayout(userId, amountOfHours, comment);
        Report report = _reporting.createEmployeeReport(userId);
        return ok(views.html.reporting.render(profile, report));
    }

    @RequiresAuthentication(clientName = "default")
    public Result requestOvertimePayout() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        Form<Payout> form = FORM.bindFromRequest();
        String comment = form.data().get("comment");
        int amountOfHours = Integer.parseInt(
                form.data().get("numofhours")
        );

        _timeTracking.requestOvertimePayout(userId, amountOfHours, comment);
        Report report = _reporting.createEmployeeReport(userId);
        return ok(views.html.reporting.render(profile, report));
    }


}
