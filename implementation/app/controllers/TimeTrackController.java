package controllers;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

/**
 * ExampleInterface controller -> to be removed.
 */
public class TimeTrackController extends UserProfileController<CommonProfile> {


    private TimeTracking _timeTracking;


    @Inject
    public TimeTrackController(TimeTracking timeTracking) {
        _timeTracking = timeTracking;
    }


    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        _timeTracking.come();
        return ok(views.html.index.render(getUserProfile()));
    }

    @RequiresAuthentication(clientName = "default")
    public Result come() throws Exception{
        _timeTracking.come();
        return ok(views.html.index.render(getUserProfile()));
    }

    /*
    @RequiresAuthentication(clientName = "default")
    public Result pauseStart(){

        return ok(views.html.index.render(getUserProfile()));
    }
    */

    @RequiresAuthentication(clientName = "default")
    public Result go() throws Exception{
        _timeTracking.go();
        return ok("Go");
    }
}
