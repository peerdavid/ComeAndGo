package controllers;

import business.reporting.Reporting;
import business.timetracking.TimeTracking;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import models.Break;
import models.TimeTrack;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;
import utils.DateTimeUtils;

import java.util.List;
import java.util.Map;

public class TimeTrackController extends UserProfileController<CommonProfile> {


    private TimeTracking _timeTracking;
    private Reporting _reporting;

    @Inject
    public TimeTrackController(TimeTracking timeTracking, Reporting reporting) {
        _timeTracking = timeTracking;
        _reporting = reporting;
    }


    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();
        int userId = Integer.parseInt(profile.getId());

        //DateTime now = DateTime.now().plusDays(20);

        //DateTime from = DateTimeUtils.stringToTime("00:00", now);
        //DateTime to = now.plusDays(1);

        DateTime from = DateTimeUtils.startOfDay(DateTime.now());
        DateTime to = DateTimeUtils.endOfDay(DateTime.now());

        List<TimeTrack> timeTrackList = _timeTracking.readTimeTracks(userId, from, to);

        return ok(views.html.index.render(
            profile,
            _timeTracking.readState(userId),
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
    public Result readProgress() throws Exception {
        int userId = Integer.parseInt(getUserProfile().getId());

        int progress = (int) (_reporting.readHoursWorkedProgress(userId) * 100);
        double overtime = 0.0;

        if(progress >= 100)
            overtime = _reporting.calculateOvertime(userId, DateTime.now());

        return ok("{ \"progress\": \"" + progress + "\", \"overtime\": \""
            + String.format("%.1f", overtime) + "\" }");
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
            DateTime today = DateTime.now();
            dateFrom = today.minusSeconds(today.getSecondOfDay());

            // always start on Monday, the week before actual one
            dateFrom = dateFrom.minusDays(dateFrom.getDayOfWeek() + 6);
            from = DateTimeUtils.dateTimeToDateString(dateFrom);

            // always end on Sunday, the current week
            dateTo = today.plusDays(7 - today.getDayOfWeek());
            dateTo = DateTimeUtils.endOfDay(dateTo);
            to = DateTimeUtils.dateTimeToDateString(dateTo);
        } else {
            dateFrom = DateTimeUtils.stringToDateTime(from);
            dateTo = DateTimeUtils.stringToDateTime(to, 23, 59);
        }

        timeTracks = _timeTracking.readTimeTracks(userId, dateFrom, dateTo);

        return ok(views.html.timetracks.render(profile, userId, from, to, timeTracks));
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

            DateTime start = null;
            DateTime end = null;

            Map<String, String> breakFormData = Form.form().bindFromRequest(
                breakStart,
                breakEnd
            ).data();

            if (breakFormData.get(breakStart) != null && !breakFormData.get(breakStart).isEmpty()) {
                start = DateTimeUtils.stringToTime(breakFormData.get(breakStart));
            }
            if (breakFormData.get(breakEnd) != null && !breakFormData.get(breakEnd).isEmpty()) {
                end = DateTimeUtils.stringToTime(breakFormData.get(breakEnd));
            }

            b.setFromAndTo(start, end);
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

        // in case we have a break over midnight (dates from DateTimeUtils.stringToTime() are always equal)
        if(fromDate.isAfter(toDate)) {
            toDate = toDate.plusDays(1);
        }

        timeTrack.addBreak(new Break(fromDate, toDate));

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
}
