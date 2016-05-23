package controllers;

import business.reporting.Reporting;
import business.timetracking.TimeTracking;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Payout;
import models.Report;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;


/**
 * Created by david on 02.05.16.
 */
public class ReportingController extends UserProfileController<CommonProfile> {


    private Reporting _reporting;
    private TimeTracking _timeTracking;
    private final Form<Payout> FORM = Form.form(Payout.class);

    @Inject
    public ReportingController(Reporting reporting, TimeTracking timeTracking) {
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

        Form<Payout> form = FORM.bindFromRequest();
        String comment = form.data().get("comment");
        int amountOfDays = Integer.parseInt(
                form.data().get("numofhours")
        );

        checkPayoutParameters(amountOfDays,comment);

        _timeTracking.requestHolidayPayout(userId, amountOfDays, comment);

        return redirect(routes.ReportingController.employeeReport());
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

        checkPayoutParameters(amountOfHours,comment);

        _timeTracking.requestOvertimePayout(userId, amountOfHours, comment);

        return redirect(routes.ReportingController.employeeReport());
    }

    private void checkPayoutParameters(int amount, String comment) throws Exception {
        if(amount < 0) {
            throw new UserException(Messages.get("exceptions.reporting.field_numofhours_negative"));
        } else if(amount == 0) {
            throw new UserException(Messages.get("exceptions.reporting.field_numofhours_empty"));
        } else if(comment.length() == 0) {
            throw new UserException(Messages.get("exceptions.reporting.field_comment_empty"));
        }
    }
}
