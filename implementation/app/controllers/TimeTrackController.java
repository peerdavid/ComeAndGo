package controllers;

import business.timetracking.TimeTrackState;
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
        CommonProfile profile = getUserProfile();
        int profileId = Integer.parseInt(profile.getId());

        _timeTracking.come(profileId);

        // return ok(views.html.index.render(profile));
        return ok(views.html.index.render(profile, TimeTrackState.INACTIVE /*_timeTracking.getState(profileId)*/));
    }

    @RequiresAuthentication(clientName = "default")
    public Result come() throws Exception{
        CommonProfile profile = getUserProfile();
        int profileId = Integer.parseInt(profile.getId());
        _timeTracking.come(profileId);
        return redirect(routes.TimeTrackController.index());
    }


    @RequiresAuthentication(clientName = "default")
    public Result pause(){
        CommonProfile profile = getUserProfile();
        int profileId = Integer.parseInt(profile.getId());
        switch (_timeTracking.getState(profileId)) {
            case ACTIVE:
                _timeTracking.startBreak(profileId);
                break;
            case PAUSE:
                _timeTracking.endBreak(profileId);
                break;
            default:
                break;
        }

        return redirect(routes.TimeTrackController.index());
    }

    @RequiresAuthentication(clientName = "default")
    public Result go() throws Exception{
        CommonProfile profile = getUserProfile();
        _timeTracking.go(Integer.parseInt(profile.getId()));
        return redirect(routes.TimeTrackController.index());
    }
}
