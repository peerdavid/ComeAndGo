package business.timetracking;

import com.google.inject.Inject;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
class TimeTrackingFacade implements TimeTracking {


    private TimeTrackingService _timeTrackingService;
    private TimeOffService _timeOffService;

    @Inject
    public TimeTrackingFacade(TimeTrackingService timeTrackingService, TimeOffService timeOffService) {
        _timeTrackingService = timeTrackingService;
        _timeOffService = timeOffService;
    }


    @Override
    public void come(int userId) throws Exception {
        _timeTrackingService.come(userId);
    }


    @Override
    public void go(int userId) throws Exception {
            _timeTrackingService.go(userId);
    }


    @Override
    public void startBreak(int userId) throws Exception {
            _timeTrackingService.createBreak(userId);
    }


    @Override
    public void endBreak(int userId) throws Exception {
            _timeTrackingService.endBreak(userId);
    }


    @Override
    public TimeTrackState readState(int userId) throws Exception {
        TimeTrackState result = TimeTrackState.INACTIVE;

        if(_timeTrackingService.isActive(userId)) {
            result = TimeTrackState.ACTIVE;
        }

        if(_timeTrackingService.takesBreak(userId)) {
            result = TimeTrackState.PAUSE;
        }

        return result;
    }

    @Override
    public TimeTrack readTimeTrackById(int id) throws Exception {
        return _timeTrackingService.readTimeTrackById(id);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws Exception {
        return _timeTrackingService.readTimeTracks(userId);
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception {
        return _timeTrackingService.readTimeTracks(userId, from, to);
    }


    @Override
    public void createTimeTrack(int userId, DateTime from, DateTime to) throws Exception {
        _timeTrackingService.createTimeTrack(userId, from, to);
    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) throws Exception {
        _timeTrackingService.deleteTimeTrack(timeTrack);
    }

    @Override
    public void updateTimeTrack(TimeTrack timeTrack) throws Exception {
        _timeTrackingService.updateTimeTrack(timeTrack);
    }

    @Override
    public void deleteTimeOff(int userId, int id) throws Exception {
        _timeOffService.deleteTimeOff(userId, id);
    }

    @Override
    public void takeSickLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.takeSickLeave(userId, from, to, comment);
    }

    @Override
    public void takeBusinessTrip(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.takeBusinessTrip(userId, from, to, comment);
    }

    @Override
    public void requestHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.requestHoliday(userId, from, to, comment);
    }

    @Override
    public void requestSpecialHoliday(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.requestSpecialHoliday(userId, from, to, comment);
    }

    @Override
    public void acceptHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.acceptHoliday(timeOffId, bossId);
    }

    @Override
    public void rejectHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.rejectHoliday(timeOffId, bossId);
    }

    @Override
    public void takeParentalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.takeParentalLeave(userId, from, to, comment);
    }

    @Override
    public void requestEducationalLeave(int userId, DateTime from, DateTime to, String comment) throws Exception {
        _timeOffService.requestEducationalLeave(userId, from, to, comment);
    }

    @Override
    public void acceptSpecialHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.acceptSpecialHoliday(timeOffId, bossId);

    }

    @Override
    public void rejectSpecialHoliday(int timeOffId, int bossId) throws Exception {
        _timeOffService.rejectSpecialHoliday(timeOffId, bossId);
    }

    @Override
    public void acceptEducationalLeave(int timeOffId, int bossId) throws Exception {
        _timeOffService.acceptEducationalLeave(timeOffId, bossId);
    }

    @Override
    public void rejectEducationalLeave(int timeOffId, int bossId) throws Exception {
        _timeOffService.rejectEducationalLeave(timeOffId, bossId);
    }

    @Override
    public TimeOff readTimeOffById(int timeOffId) throws Exception {
        return _timeOffService.readTimeOffById(timeOffId);
    }

    @Override
    public List<TimeOff> readTimeOffs(int userId) throws Exception {
        return _timeOffService.readTimeOffs(userId);
    }
}
