package controllers;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import controllers.timeoff.TimeOffToXmlConverter;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;

import java.util.List;

import static play.data.Form.form;

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
        return ok(views.html.timeoff.render(getUserProfile()));
    }


    @RequiresAuthentication(clientName = "default")
    public Result readTimeOffCalendar() throws Exception{

        List<TimeOff> timeOffs = _timeTracking.readTimeOffs(Integer.valueOf(getUserProfile().getId()));

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
    public Result createTimeOff(String comment) throws Exception {
        int userId = Integer.getInteger(getUserProfile().getId());
        _timeTracking.requestHoliday(userId, DateTime.now(), DateTime.now(), comment);
        return redirect(routes.TimeOffController.index());
    }
}
