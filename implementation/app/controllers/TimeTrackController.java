package controllers;

import business.timetracking.TimeTracking;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import controllers.timeoff.TimeOffToXmlConverter;
import models.Break;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import utils.DateTimeUtils;

import java.util.List;
import java.util.Map;

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

        DateTime from = DateTimeUtils.stringToTime("00:00", now);
        DateTime to = now.plusDays(1);

        List<TimeTrack> timeTrackList = _timeTracking.readTimeTracks(userId, from, to);

        return ok(views.html.index.render(
            profile,
            _timeTracking.readState(userId),
            progress,
            timeTrackList)
        );
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

        switch (_timeTracking.readState(profileId)) {
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
    public Result readTimeTracks(int userId, String from, String to) throws Exception {
        CommonProfile profile = getUserProfile();

        DateTime dateFrom;
        DateTime dateTo;

        List<TimeTrack> timeTracks;
        if (from == null || to == null || from.isEmpty() || to.isEmpty()) {
            dateFrom = DateTime.now();
            dateFrom = dateFrom.minusDays(dateFrom.getDayOfWeek());
            from = DateTimeUtils.dateTimeToDateString(dateFrom);

            dateTo = dateFrom.plusDays(5);
            to = DateTimeUtils.dateTimeToDateString(dateTo);
        } else {
            dateFrom = DateTimeUtils.stringToDateTime(from);
            dateTo = DateTimeUtils.stringToDateTime(to);
        }

        timeTracks = _timeTracking.readTimeTracks(userId, dateFrom, dateTo);

        return ok(views.html.edittimetracks.render(profile, userId, from, to, timeTracks));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result updateTimeTrack(int userId, String from, String to) throws Exception {
        Map<String, String> formData = Form.form().bindFromRequest(
            "startdate",
            "enddate",
            "starttime",
            "endtime"
        ).data();

        TimeTrack timeTrack = _timeTracking.readTimeTrackById(Integer.parseInt(formData.get("id")));

        // update timetrack dates
        if (formData.get("startdate") != null && !formData.get("startdate").isEmpty()) {
            timeTrack.setFrom(DateTimeUtils.stringToDateTime(formData.get("startdate")));
        }
        if (formData.get("enddate") != null && !formData.get("enddate").isEmpty()) {
            timeTrack.setTo(DateTimeUtils.stringToDateTime(formData.get("enddate"), 23, 59));
        }

        // update timetrack times
        if (formData.get("starttime") != null && !formData.get("starttime").isEmpty()) {
            String[] d = formData.get("starttime").split(":");
            timeTrack.setFrom(
                DateTimeUtils.stringToTime(formData.get("starttime"), timeTrack.getFrom())
            );
        }
        if (formData.get("endtime") != null && !formData.get("endtime").isEmpty()) {
            timeTrack.setTo(
                DateTimeUtils.stringToTime(formData.get("endtime"), timeTrack.getTo())
            );
        }

        // update timetrack breaks
        for (Break b : timeTrack.getBreaks()) {
            String breakStart = "break_starttime" + b.getId();
            String breakEnd = "break_endtime" + b.getId();

            DynamicForm breakFormData = Form.form().bindFromRequest(
                breakStart,
                breakEnd
            );

            if (formData.get(breakStart) != null && !breakFormData.get(breakStart).isEmpty()) {
                b.setFrom(DateTimeUtils.stringToTime(breakFormData.get(breakStart)));
            }
            if (formData.get(breakEnd) != null && !breakFormData.get(breakEnd).isEmpty()) {
                b.setTo(DateTimeUtils.stringToTime(breakFormData.get(breakEnd)));
            }
        }

        _timeTracking.updateTimeTrack(timeTrack);

        return redirect(routes.TimeTrackController.readTimeTracks(userId, from, to));
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

        return redirect(routes.TimeTrackController.readTimeTracks(userId, from, to));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result createBreak(int timetrackId, int userId, String from, String to) throws Exception {
        TimeTrack timeTrack = _timeTracking.readTimeTrackById(timetrackId);

        Map<String, String> formData = Form.form().bindFromRequest(
            "starttime",
            "endtime"
        ).data();

        DateTime fromDate = null;
        DateTime toDate = null;

        if (formData.get("starttime") != null && !formData.get("starttime").isEmpty()) {
            fromDate = DateTimeUtils.stringToTime(formData.get("starttime"));
        }
        if (formData.get("endtime") != null && !formData.get("endtime").isEmpty()) {
            toDate = DateTimeUtils.stringToTime(formData.get("endtime"));
        }
        if (fromDate == null || toDate == null) {
            throw new UserException("exceptions.timetracking.error_in_break_form");
        }

        timeTrack.getBreaks().add(new Break(fromDate, toDate));

        _timeTracking.updateTimeTrack(timeTrack);

        return redirect(routes.TimeTrackController.readTimeTracks(userId, from, to));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result createTimeTrack(int userId, String from, String to) throws Exception {
        Map<String, String> formData = Form.form().bindFromRequest(
            "startdate",
            "enddate",
            "starttime",
            "endtime"
        ).data();

        DateTime fromDate = null;
        DateTime toDate;

        if (formData.get("startdate") != null && !formData.get("startdate").isEmpty()) {
            fromDate = DateTimeUtils.stringToDateTime(formData.get("startdate"));
        }
        if (formData.get("enddate") != null && !formData.get("enddate").isEmpty()) {
            toDate = DateTimeUtils.stringToDateTime(formData.get("enddate"), 23, 59);
        } else if (fromDate != null) {
            toDate = fromDate;
        } else {
            throw new UserException("exceptions.timetracking.error_in_timetrack_form");
        }

        if (formData.get("starttime") != null && !formData.get("starttime").isEmpty()) {
            fromDate = DateTimeUtils.stringToTime(formData.get("starttime"), fromDate);
        }
        if (formData.get("endtime") != null && !formData.get("endtime").isEmpty()) {
            toDate = DateTimeUtils.stringToTime(formData.get("endtime"), toDate);
        }
        if (fromDate == null || toDate == null) {
            throw new UserException("exceptions.timetracking.error_in_timetrack_form");
        }


        _timeTracking.createTimeTrack(userId, fromDate, toDate);

        return redirect(routes.TimeTrackController.readTimeTracks(userId, from, to));
    }


    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result deleteTimeTrack(int timetrackId, int userId, String from, String to) throws Exception {
        TimeTrack timeTrack = _timeTracking.readTimeTrackById(timetrackId);

        _timeTracking.deleteTimeTrack(timeTrack);

        return redirect(routes.TimeTrackController.readTimeTracks(userId, from, to));
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
