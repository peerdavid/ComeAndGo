package business.timetracking;

import com.google.inject.Inject;
import infrastructure.UserRepository;
import model.TimeTrack;
import model.User;
import javassist.NotFoundException;

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
        }
    }

    @Override
    public void startBreak(int userId) {

    }

    @Override
    public void endBreak(int userId) {

    }


    @Override
    public List<TimeTrack> readTimeTracks(int userId) {
        return _timeTrackingService.readTimeTracks(userId);
    }
}
