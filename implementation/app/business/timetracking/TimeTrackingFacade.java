package business.timetracking;

import business.UserException;
import com.google.inject.Inject;
import infrastructure.UserRepository;
import model.TimeTrack;
import model.User;
import javassist.NotFoundException;

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
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startBreak(int userId) {
    }

    @Override
    public void endBreak(int userId) {

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
        } catch (UserException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            result = TimeTrackState.INACTIVE;
        }

        return result;
    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId) {
        try {
            return _timeTrackingService.readTimeTracks(userId);
        } catch (UserException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
