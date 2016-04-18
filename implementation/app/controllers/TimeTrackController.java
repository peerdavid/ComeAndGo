package controllers;

import business.timetracking.TimeTrackState;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import models.TimeTrack;
import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

import java.util.Collections;
import java.util.Date;
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


    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());
        int progress = 70;

        DateTime now = DateTime.now();

        DateTime from = now.minusHours(now.getHourOfDay()).minusMinutes(now.getMinuteOfHour());
        DateTime to = DateTime.now().plusDays(1);

        List<TimeTrack> timeTrackList = _timeTracking.readTimeTracks(userId, from, to);

        return ok(views.html.index.render(profile, _timeTracking.getState(userId), progress, timeTrackList));
    }

    @RequiresAuthentication(clientName = "default")
    public Result come() throws Exception {
        CommonProfile profile = getUserProfile();
        _timeTracking.come(Integer.parseInt(profile.getId()));
        return redirect(routes.TimeTrackController.index());
    }


    @RequiresAuthentication(clientName = "default")
    public Result pause() throws Exception {

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
        }

        return redirect(routes.TimeTrackController.index());
    }

    @RequiresAuthentication(clientName = "default")
    public Result go() throws Exception {
        CommonProfile profile = getUserProfile();
        _timeTracking.go(Integer.parseInt(profile.getId()));
        return redirect(routes.TimeTrackController.index());
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result editTimeTracks(int userId, String from, String to) throws Exception {
        CommonProfile profile = getUserProfile();

        List<TimeTrack> timeTracks;
        if(from == null || to == null || from.isEmpty() || to.isEmpty()) {
            timeTracks = Collections.emptyList();
        } else {
            String[] fromDate = from.split("\\.");
            String[] toDate = to.split("\\.");

            DateTime dateFrom = new DateTime(Integer.parseInt(fromDate[2]), Integer.parseInt(fromDate[1]), Integer.parseInt(fromDate[0]), 0, 0);
            DateTime dateTo = new DateTime(Integer.parseInt(toDate[2]), Integer.parseInt(toDate[1]), Integer.parseInt(toDate[0]), 23, 59);

            timeTracks = _timeTracking.readTimeTracks(userId, dateFrom, dateTo);
        }

        return ok(views.html.edittimetracks.render(profile, userId, timeTracks));
    }
}
