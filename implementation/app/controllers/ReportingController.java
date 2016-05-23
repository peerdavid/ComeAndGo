package controllers;

import business.reporting.Reporting;
import business.reporting.WorkTimeAlert;
import business.timetracking.TimeTracking;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Payout;
import models.Report;
import org.apache.commons.lang3.tuple.Pair;
import org.h2.util.DateTimeUtils;
import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F;
import play.mvc.Result;

import java.util.Collections;
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
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        DateTime fromDate = (from == null || from.isEmpty()) ? utils.DateTimeUtils.startOfActualYear() : utils.DateTimeUtils.stringToDateTime(from);
        DateTime toDate = (to == null || to.isEmpty()) ? DateTime.now() : utils.DateTimeUtils.stringToDateTime(to);

        // ToDo: Get all workTimeAlerts for all employees of boss
        List<WorkTimeAlert> workTimeAlerts = Collections.emptyList();

        Report report = _reporting.createCompanyReport(fromDate, toDate);
        return ok(views.html.reporting.render(profile, report, workTimeAlerts,
            from == null ? utils.DateTimeUtils.dateTimeToDateString(fromDate) : from,
            to == null ? utils.DateTimeUtils.dateTimeToDateString(toDate) : to));
    }


    @RequiresAuthentication(clientName = "default")
    public Result employeeReport(String requestedId, String from, String to) throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        DateTime fromDate = (from == null || from.isEmpty()) ? utils.DateTimeUtils.startOfActualYear() : utils.DateTimeUtils.stringToDateTime(from);
        DateTime toDate = (to == null || to.isEmpty()) ? DateTime.now() : utils.DateTimeUtils.stringToDateTime(to);

        List<WorkTimeAlert> workTimeAlerts = getWorkTimeAlerts(userId, fromDate, toDate);

        Report report = _reporting.createEmployeeReport(userId, fromDate, toDate);
        return ok(views.html.reporting.render(profile, report, workTimeAlerts,
            from == null ? utils.DateTimeUtils.dateTimeToDateString(fromDate) : from,
            to == null ? utils.DateTimeUtils.dateTimeToDateString(toDate) : to));
    }


    @RequiresAuthentication(clientName = "default", authorizerName = "boss")
    public Result bossReport(String requestedId, String from, String to) throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        DateTime fromDate = (from == null || from.isEmpty()) ? utils.DateTimeUtils.startOfActualYear() : utils.DateTimeUtils.stringToDateTime(from);
        DateTime toDate = (to == null || to.isEmpty()) ? DateTime.now() : utils.DateTimeUtils.stringToDateTime(to);

        // ToDo: Get all workTimeAlerts for all employees of boss
        List<WorkTimeAlert> workTimeAlerts = Collections.emptyList();

        Report report = _reporting.createBossReport(userId, fromDate, toDate);
        return ok(views.html.reporting.render(profile, report, workTimeAlerts,
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
        return _reporting.readForbiddenWorkTimeAlerts(userId, from, to);
    }
}
