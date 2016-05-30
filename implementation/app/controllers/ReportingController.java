package controllers;

import business.reporting.Reporting;
import business.reporting.WorkTimeAlert;
import business.timetracking.TimeTracking;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Payout;
import models.Report;
import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Result;
import utils.DateTimeUtils;

import java.util.List;


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
    public Result companyReport(String requestedId, String from, String to) throws Exception {
        return generateReport(requestedId, from, to, SecurityRole.ROLE_PERSONNEL_MANAGER);
    }


    @RequiresAuthentication(clientName = "default")
    public Result employeeReport(String requestedId, String from, String to) throws Exception {
        return generateReport(requestedId, from, to, SecurityRole.ROLE_USER);
    }


    @RequiresAuthentication(clientName = "default", authorizerName = "boss")
    public Result bossReport(String requestedId, String from, String to) throws Exception {
        return generateReport(requestedId, from, to, SecurityRole.ROLE_BOSS);
    }

    private Result generateReport(String requestedId, String from, String to, String role) throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        DateTime fromDate = (from == null || from.isEmpty()) ? utils.DateTimeUtils.startOfActualYear() : utils.DateTimeUtils.stringToDateTime(from);
        DateTime toDate = DateTimeUtils.endOfDay((to == null || to.isEmpty()) ? DateTime.now() : utils.DateTimeUtils.stringToDateTime(to));

        List<WorkTimeAlert> workTimeAlerts = null;
        if(requestedId != null) {
            workTimeAlerts = _reporting.readForbiddenWorkTimeAlerts(Integer.parseInt(requestedId), fromDate, toDate, userId);
        }

        Report report;
        switch (role) {
            case SecurityRole.ROLE_BOSS:
                report = _reporting.createBossReport(userId, fromDate, toDate);
                break;
            case SecurityRole.ROLE_PERSONNEL_MANAGER:
                report = _reporting.createCompanyReport(fromDate, toDate);
                break;
            default:
                report = _reporting.createEmployeeReport(userId, fromDate, toDate);
                break;
        }

        return ok(views.html.reporting.render(profile, report,
            workTimeAlerts,
            from == null ? utils.DateTimeUtils.dateTimeToDateString(fromDate) : from,
            to == null ? utils.DateTimeUtils.dateTimeToDateString(toDate) : to));
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

        return redirect(routes.ReportingController.employeeReport(null,null,null));
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

        return redirect(routes.ReportingController.employeeReport(null,null,null));
    }

    private void checkPayoutParameters(int amount, String comment) throws Exception {
        if (amount < 0) {
            throw new UserException(Messages.get("exceptions.reporting.field_numofhours_negative"));
        } else if (amount == 0) {
            throw new UserException(Messages.get("exceptions.reporting.field_numofhours_empty"));
        } else if (comment.length() == 0) {
            throw new UserException(Messages.get("exceptions.reporting.field_comment_empty"));
        }
    }

    private List<WorkTimeAlert> getWorkTimeAlerts(int userId, DateTime from, DateTime to) throws Exception {
        return _reporting.readForbiddenWorkTimeAlerts(userId, from, to, 0);
    }
}
