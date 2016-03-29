package controllers;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import model.TimeTrack;
import model.User;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

import java.util.List;

/**
 * ExampleInterface controller -> to be removed.
 */
public class TimeTrackController extends UserProfileController<CommonProfile> {


    private TimeTracking _timeTracking;


    @Inject
    public TimeTrackController(TimeTracking timeTracking) {
        _timeTracking = timeTracking;
    }


    public Result index() throws Exception {
        _timeTracking.come();
        return ok(views.html.index.render(getUserProfile()));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "user")
    public Result home() {
        User user = User.findById(Integer.parseInt(getUserProfile().getId()));
        List<TimeTrack> tracks = _timeTracking.readTimeTracks(user);
        return ok(views.html.home.render(String.valueOf(tracks.size())));
    }
}
