package business.timetracking;

import com.google.inject.Inject;
import models.TimeOff;
import models.TimeTrack;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class InternalTimeTrackingFacade implements InternalTimeTracking {

    private TimeTrackingService _timeTrackingService;
    private TimeOffService _timeOffService;

    @Inject
    public InternalTimeTrackingFacade(TimeTrackingService timeTrackingService, TimeOffService timeOffService){
        _timeTrackingService = timeTrackingService;
        _timeOffService = timeOffService;
    }


    @Override
    public List<TimeOff> readTimeOffs(int userId) throws Exception {
        return _timeOffService.readTimeOffs(userId);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws Exception {
        return _timeTrackingService.readTimeTracks(userId);
    }
}
