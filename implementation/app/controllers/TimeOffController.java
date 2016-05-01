package controllers;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import controllers.timeoff.TimeOffToXmlConverter;
import models.TimeOff;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
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
        List<TimeOff> timeOffs = _timeTracking.readTimeOffs(Integer.valueOf(profile.getId()));

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
}
