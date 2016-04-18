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

    /*
     * ToDo: Annotate with logging and transaction = true
     */
    @Override
    public void come(int userId) throws Exception {
        _timeTrackingService.come(userId);
    }


    /*
     * Note: Our facade is responsible for exception handling, use case
     * logging and transaction handling. In this case we know exactly
     * the right use case -> so we could react with (for instance): start
     * a new subusecase, were we create a new timetrack with the same
     * from and to time and send the report to our admin... or something like this
     */
    @Override
    public void go(int userId) {
        try {
            _timeTrackingService.go(userId);
        } catch (UserException | TimeTrackException | NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startBreak(int userId) {
        try {
            _timeTrackingService.createBreak(userId);
        } catch (TimeTrackException | UserException | NotFoundException e) {
            // TODO: add exception handling here
            e.printStackTrace();
        }
    }

    @Override
    public void endBreak(int userId) {
        try {
            _timeTrackingService.endBreak(userId);
        } catch (TimeTrackException | UserException | NotFoundException e) {
            // TODO: add exception handling here
            e.printStackTrace();
        }
    }

    @Override
    public TimeTrackState getState(int userId) {
        TimeTrackState result = TimeTrackState.INACTIVE;

        try {
            if(_timeTrackingService.isActive(userId)) {
                result = TimeTrackState.ACTIVE;
            }

            if(_timeTrackingService.takesBreak(userId)) {
                result = TimeTrackState.PAUSE;
            }
        } catch (UserException | NotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId) {
        try {
            return _timeTrackingService.readTimeTracks(userId);
        } catch (UserException e) {
            e.printStackTrace();
        } catch (TimeTrackException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) {
        try {
            return _timeTrackingService.readTimeTracks(userId, from, to);
        } catch (UserException | TimeTrackException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void addTimeTrack(TimeTrack timeTrack) {

    }

    @Override
    public void deleteTimeTrack(TimeTrack timeTrack) {

    }

    @Override
    public void updateTimeTrack(TimeTrack timeTrack) {

    }

    @Override
    public void addBreak(TimeTrack timeTrack, Break breakToInsert) {

    }

    @Override
    public void deleteBreak(Break breakToDelete) {

    }

    @Override
    public void updateBreak(Break breakToUpdate) {

    }
}
