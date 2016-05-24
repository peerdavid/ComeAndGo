package controllers.api.v1;

import business.timetracking.TimeTrackState;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

/**
 * Controller
 */
public class ApiController extends UserProfileController {

    private TimeTracking _timeTracking;


    @Inject
    public ApiController(TimeTracking timeTracking){
        _timeTracking = timeTracking;
    }

    @RequiresAuthentication(clientName = "default")
    public Result come() throws Exception{
        CommonProfile profile = getUserProfile();
        _timeTracking.come(Integer.parseInt(profile.getId()));
        return ok("1");
    }


    @RequiresAuthentication(clientName = "default")
    public Result go() throws Exception {
        CommonProfile profile = getUserProfile();
        _timeTracking.go(Integer.parseInt(profile.getId()));
        return ok("1");
    }


    @RequiresAuthentication(clientName = "default")
    public Result startBreak() throws Exception {
        CommonProfile profile = getUserProfile();
        _timeTracking.startBreak(Integer.parseInt(profile.getId()));
        return ok("1");
    }


    @RequiresAuthentication(clientName = "default")
    public Result endBreak() throws Exception {
        CommonProfile profile = getUserProfile();
        _timeTracking.endBreak(Integer.parseInt(profile.getId()));
        return ok("1");
    }


    @RequiresAuthentication(clientName = "default")
    public Result readState() throws Exception {
        CommonProfile profile = getUserProfile();
        TimeTrackState state = _timeTracking.readState(Integer.parseInt(profile.getId()));
        return ok(state.toString());
    }
}
