package controllers;

import business.timetracking.TimeOffType;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import controllers.timeoff.TimeOffToXmlConverter;
import models.TimeOff;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;

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
    public Result index(){
        CommonProfile profile = getUserProfile();
        return ok(views.html.timeoff.render(profile));
    }


    @RequiresAuthentication(clientName = "default")
    public Result readTimeOffCalendar() throws Exception{
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());
        List<TimeOff> timeOffs = _timeTracking.readTimeOffs(userId);

        String timeOffString = "";
        for(TimeOff t : timeOffs){
            timeOffString += TimeOffToXmlConverter.timeOffToXml(t);
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

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy");
        DateTime from = dtf.parseDateTime(form.get("from"));
        DateTime to = dtf.parseDateTime(form.get("to"));

        // Call business logic
        createTimeOffForGivenType(userId, type, from, to, comment);
        return redirect(routes.TimeOffController.index());
    }


    private void createTimeOffForGivenType(int userId, TimeOffType type, DateTime from, DateTime to, String comment) throws Exception {

        switch(type){
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

            default:
                throw new IllegalArgumentException("Unknown timeoff type " + type);
        }
    }
}
