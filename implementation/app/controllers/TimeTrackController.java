package controllers;

import business.UserException;
import business.timetracking.TimeTrackState;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import models.Break;
import models.TimeTrack;
import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ExampleInterface controller -> to be removed.
 */
public class TimeTrackController extends UserProfileController<CommonProfile> {


    private TimeTracking _timeTracking;

    public static final Form<TimeTrack> FORM = Form.form(TimeTrack.class);


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

        DateTime dateFrom;
        DateTime dateTo;

        List<TimeTrack> timeTracks;
        if(from == null || to == null || from.isEmpty() || to.isEmpty()) {
            dateFrom = DateTime.now();
            dateFrom = dateFrom.minusDays(dateFrom.getDayOfWeek());
            from = dateFrom.dayOfMonth().get() + "." + dateFrom.monthOfYear().get() + "." + dateFrom.year().get();

            dateTo = dateFrom.plusDays(5);
            to = dateTo.dayOfMonth().get() + "." + dateTo.monthOfYear().get() + "." + dateTo.year().get();
        } else {
            String[] fromDate = from.split("\\.");
            String[] toDate = to.split("\\.");

            dateFrom = new DateTime(Integer.parseInt(fromDate[2]), Integer.parseInt(fromDate[1]), Integer.parseInt(fromDate[0]), 0, 0);
            dateTo = new DateTime(Integer.parseInt(toDate[2]), Integer.parseInt(toDate[1]), Integer.parseInt(toDate[0]), 23, 59);
        }

        timeTracks = _timeTracking.readTimeTracks(userId, dateFrom, dateTo);

        return ok(views.html.edittimetracks.render(profile, userId, from, to, timeTracks));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result saveTimeTrack(int userId, String from, String to) throws Exception {
        CommonProfile profile = getUserProfile();

        Map<String, String[]> formData = request().body().asFormUrlEncoded();

        TimeTrack timeTrack = _timeTracking.readTimeTrackById(Integer.parseInt(formData.get("id")[0]));


        // update timetrack dates
        if(formData.get("startdate") != null && !formData.get("startdate")[0].isEmpty()) {
            String[] s = formData.get("startdate")[0].split("\\.");
            timeTrack.setFrom(new DateTime(Integer.parseInt(s[2]), Integer.parseInt(s[1]), Integer.parseInt(s[0]), 0, 0));
        }
        if(formData.get("enddate") != null && !formData.get("enddate")[0].isEmpty()) {
            String[] s = formData.get("enddate")[0].split("\\.");
            timeTrack.setTo(new DateTime(Integer.parseInt(s[2]), Integer.parseInt(s[1]), Integer.parseInt(s[0]), 23, 59));
        }

        // update timetrack times
        if(formData.get("starttime") != null && !formData.get("starttime")[0].isEmpty()) {
            String[] d = formData.get("starttime")[0].split(":");
            timeTrack.setFrom(
                new DateTime(
                    timeTrack.getFrom().getYear(),
                    timeTrack.getFrom().getMonthOfYear(),
                    timeTrack.getFrom().getDayOfMonth(),
                    Integer.parseInt(d[0]),
                    Integer.parseInt(d[1]))
            );
        }
        if(formData.get("endtime") != null && !formData.get("endtime")[0].isEmpty()) {
            String[] d = formData.get("endtime")[0].split(":");
            timeTrack.setTo(
                new DateTime(
                    timeTrack.getTo().getYear(),
                    timeTrack.getTo().getMonthOfYear(),
                    timeTrack.getTo().getDayOfMonth(),
                    Integer.parseInt(d[0]),
                    Integer.parseInt(d[1]))
            );
        }

        // update timetrack breaks
        for (Break b : timeTrack.getBreaks()) {
            String breakStart = "break_starttime" + b.getId();
            String breakEnd = "break_endtime" + b.getId();

            if (formData.get(breakStart) != null && !formData.get(breakStart)[0].isEmpty()) {
                String[] d = formData.get(breakStart)[0].split(":");
                b.setFrom(new DateTime(0, 0, 0, Integer.parseInt(d[0]), Integer.parseInt(d[1])));
            }
            if (formData.get(breakEnd) != null && !formData.get(breakEnd)[0].isEmpty()) {
                String[] d = formData.get(breakEnd)[0].split(":");
                b.setTo(new DateTime(0, 0, 0, Integer.parseInt(d[0]), Integer.parseInt(d[1])));
            }
        }

        _timeTracking.updateTimeTrack(timeTrack);

        return redirect(routes.TimeTrackController.editTimeTracks(userId, from, to));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result deleteBreak(int breakId, int timetrackId, int userId, String from, String to) throws Exception {
        TimeTrack timeTrack = _timeTracking.readTimeTrackById(timetrackId);

        for (int i = 0; i < timeTrack.getBreaks().size(); ++i) {
            if (timeTrack.getBreaks().get(i).getId() == breakId) {
                timeTrack.getBreaks().remove(i);
                break;
            }
        }

        _timeTracking.updateTimeTrack(timeTrack);

        return redirect(routes.TimeTrackController.editTimeTracks(userId, from, to));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result addBreak(int timetrackId, int userId, String from, String to) throws Exception {
        TimeTrack timeTrack = _timeTracking.readTimeTrackById(timetrackId);

        Map<String, String[]> formData = request().body().asFormUrlEncoded();

        DateTime fromDate = null;
        DateTime toDate = null;

        if (formData.get("starttime") != null && !formData.get("starttime")[0].isEmpty()) {
            String[] d = formData.get("starttime")[0].split(":");
            fromDate = new DateTime(0, 0, 0, Integer.parseInt(d[0]), Integer.parseInt(d[1]));
        }
        if (formData.get("endtime") != null && !formData.get("endtime")[0].isEmpty()) {
            String[] d = formData.get("endtime")[0].split(":");
            toDate = new DateTime(0, 0, 0, Integer.parseInt(d[0]), Integer.parseInt(d[1]));
        }
        if (fromDate == null || toDate == null) throw new UserException("exceptions.timetracking.error_in_break_form");

        timeTrack.getBreaks().add(new Break(fromDate, toDate));

        _timeTracking.updateTimeTrack(timeTrack);

        return redirect(routes.TimeTrackController.editTimeTracks(userId, from, to));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result addTimeTrack(int userId, String from, String to) throws Exception {
        Map<String, String[]> formData = request().body().asFormUrlEncoded();

        DateTime fromDate = null;
        DateTime toDate = null;

        if(formData.get("startdate") != null && !formData.get("startdate")[0].isEmpty()) {
            String[] s = formData.get("startdate")[0].split("\\.");
            fromDate = new DateTime(Integer.parseInt(s[2]), Integer.parseInt(s[1]), Integer.parseInt(s[0]), 0, 0);
        }
        if(formData.get("enddate") != null && !formData.get("enddate")[0].isEmpty()) {
            String[] s = formData.get("enddate")[0].split("\\.");
            toDate = new DateTime(Integer.parseInt(s[2]), Integer.parseInt(s[1]), Integer.parseInt(s[0]), 23, 59);
        }

        if (formData.get("starttime") != null && !formData.get("starttime")[0].isEmpty()) {
            String[] d = formData.get("starttime")[0].split(":");
            fromDate = fromDate.plusHours(Integer.parseInt(d[0])).plusMinutes(Integer.parseInt(d[1]));
        }
        if (formData.get("endtime") != null && !formData.get("endtime")[0].isEmpty()) {
            String[] d = formData.get("endtime")[0].split(":");
            toDate = toDate.plusHours(Integer.parseInt(d[0])).plusMinutes(Integer.parseInt(d[1]));
        }
        if (fromDate == null || toDate == null) throw new UserException("exceptions.timetracking.error_in_break_form");


        _timeTracking.addTimeTrack(userId, fromDate, toDate);

        return redirect(routes.TimeTrackController.editTimeTracks(userId, from, to));
    }
}
