package controllers;

import business.timetracking.TimeOffType;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import models.TimeOff;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import utils.DateTimeUtils;

import java.util.List;


/**
 * Created by david on 01.05.16.
 */
public class TimeOffController extends UserProfileController<CommonProfile> {

    private TimeTracking _timeTracking;

    @Inject
    public TimeOffController(TimeTracking timeTracking) {
        _timeTracking = timeTracking;
    }


    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();
        return ok(views.html.timeoff.render(profile, null));
    }


    @RequiresAuthentication(clientName = "default")
    public Result readTimeOffDetails(int id) throws Exception {
        CommonProfile profile = getUserProfile();
        TimeOff timeOffToDelete = _timeTracking.readTimeOffById(id);
        return ok(views.html.timeoff.render(profile, timeOffToDelete));
    }


    @RequiresAuthentication(clientName = "default")
    public Result deleteTimeOff(int id) throws Exception {
        CommonProfile profile = getUserProfile();

        _timeTracking.deleteTimeOff(Integer.parseInt(profile.getId()), id);
        return redirect(routes.TimeOffController.index());
    }


    @RequiresAuthentication(clientName = "default")
    public Result readTimeOffCalendar() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());
        List<TimeOff> timeOffs = _timeTracking.readTimeOffs(userId);

        String timeOffString = "";
        for(TimeOff t : timeOffs) {
            timeOffString += timeOffToXml(t);
        }

        // Testing only -> get from business
        return ok("<?xml version=\"1.0\"?>\n" +
                "<monthly>\n" +
                timeOffString + "\n" +
                "</monthly>\n").as("application/xml");
    }


    @RequiresAuthentication(clientName = "default")
    public Result createTimeOff() throws Exception {

        // Load user from session
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        // Bind request parameters
        DynamicForm form = Form.form().bindFromRequest();
        String comment = form.get("comment");
        TimeOffType type = TimeOffType.valueOf(form.get("type"));

        DateTime from = DateTimeUtils.stringToDateTime(form.get("from"));
        DateTime to = DateTimeUtils.stringToDateTime(form.get("to"));

        // Call business logic
        createTimeOffForGivenType(userId, type, from, to, comment);
        return redirect(routes.TimeOffController.index());
    }


    /*
     * HELPER METHODS
     */
    private static String timeOffToXml(TimeOff timeOff) {
        int id = timeOff.getId();
        String name = StringEscapeUtils.escapeXml10(timeOff.getType().toString());
        String comment = StringEscapeUtils.escapeXml10(timeOff.getComment());

        String from = StringEscapeUtils.escapeXml10(timeOff.getFrom().toString("yyyy-MM-dd"));
        String to = StringEscapeUtils.escapeXml10(timeOff.getTo().toString("yyyy-MM-dd"));

        String color = StringEscapeUtils.escapeXml10(timeOffToColor(timeOff));
        String url = routes.TimeOffController.readTimeOffDetails(timeOff.getId()).url();

        return  String.format("\t<event>\n" +
                "\t\t<id>%d</id>\n" +
                "\t\t<name>%s (%s)</name>\n" +
                "\t\t<startdate>%s</startdate>\n" +
                "\t\t<enddate>%s</enddate>\n" +
                "\t\t<starttime></starttime>\n" +
                "\t\t<endtime></endtime>\n" +
                "\t\t<color>%s</color>\n" +
                "\t\t<url>%s</url>\n" +
                "\t</event>\n", id, name, comment, from, to, color, url);
    }

    private static String timeOffToColor(TimeOff timeOff) {
        String grey = "#BDBDBD";
        String orange = "#FF5722";
        String green = "#4CAF50";
        String yellow = "#FFEB3B";
        String blue = "#2196F3";
        String purple = "#9C27B0";

        switch (timeOff.getState()) {
            case REQUEST_SENT:
                return grey;

            case REQUEST_REJECTED:
                return orange;
        }

        switch (timeOff.getType()) {
            case HOLIDAY:
                return green;

            case BUSINESS_TRIP:
                return yellow;

            case EDUCATIONAL_LEAVE:
            case PARENTAL_LEAVE:
            case SPECIAL_HOLIDAY:
                return blue;

            case SICK_LEAVE:
                return purple;

            default:
        }

        return grey;
    }


    private void createTimeOffForGivenType(int userId, TimeOffType type, DateTime from, DateTime to, String comment) throws Exception {
        switch(type) {
            case HOLIDAY:
                _timeTracking.requestHoliday(userId, from, to, comment);
                break;

            case BUSINESS_TRIP:
                _timeTracking.takeBusinessTrip(userId, from, to, comment);
                break;

            case EDUCATIONAL_LEAVE:
                _timeTracking.requestEducationalLeave(userId, from, to, comment);
                break;

            case PARENTAL_LEAVE:
                _timeTracking.takeParentalLeave(userId, from, to, comment);
                break;

            case SICK_LEAVE:
                _timeTracking.takeSickLeave(userId, from, to, comment);
                break;

            case SPECIAL_HOLIDAY:
                _timeTracking.requestSpecialHoliday(userId, from, to, comment);
                break;

            case BANK_HOLIDAY:
                _timeTracking.createBankHoliday(userId, from, to, comment);
                break;

            default:
                throw new IllegalArgumentException("Unknown timeoff type " + type);
        }
    }
}
