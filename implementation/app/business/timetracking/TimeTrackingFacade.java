package business.timetracking;

import business.UserException;
import com.google.inject.Inject;
import infrastructure.TimeTrackException;
import models.Break;
import models.TimeTrack;
import javassist.NotFoundException;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

/**
 * Created by david on 21.03.16.
 */
class TimeTrackingFacade implements TimeTracking {


    private TimeTrackingService _timeTrackingService;

    @Inject
    public TimeTrackingFacade(TimeTrackingService timeTrackingService) {
        _timeTrackingService = timeTrackingService;
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
    public TimeTrackState getState(int userId) throws Exception {
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
    public List<TimeTrack> readTimeTracks(int userId) throws Exception {
        return _timeTrackingService.readTimeTracks(userId);
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception {
        return _timeTrackingService.readTimeTracks(userId, from, to);
    }


    @Override
    public void addTimeTrack(TimeTrack timeTrack) throws Exception {
        _timeTrackingService.addTimeTrack(timeTrack);
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
    public void addBreak(TimeTrack timeTrack, Break breakToInsert) throws Exception {
        _timeTrackingService.addBreak(timeTrack, breakToInsert);
    }


    @Override
    public void deleteBreak(Break breakToDelete) throws Exception {
        _timeTrackingService.deleteBreak(breakToDelete);
    }


    @Override
    public void updateBreak(Break breakToUpdate) throws Exception {
        _timeTrackingService.updateBreak(breakToUpdate);
    }
}
