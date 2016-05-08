package business.timetracking;

import com.google.inject.Inject;
import models.Payout;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class InternalTimeTrackingFacade implements InternalTimeTracking {

    private TimeTrackingService _timeTrackingService;
    private TimeOffService _timeOffService;
    private PayoutService _payoutService;

    @Inject
    public InternalTimeTrackingFacade(TimeTrackingService timeTrackingService, TimeOffService timeOffService, PayoutService payoutService){
        _timeTrackingService = timeTrackingService;
        _timeOffService = timeOffService;
        _payoutService = payoutService;
    }


    @Override
    public List<TimeOff> readTimeOffs(int userId) throws Exception {
        return _timeOffService.readTimeOffs(userId);
    }

    @Override
    public List<TimeTrack> readTimeTracks(int userId) throws Exception {
        return _timeTrackingService.readTimeTracks(userId);
    }

    @Override
    public List<Payout> readAcceptedPayoutsFromUser(int userId, DateTime from, DateTime to) throws Exception {
        return _payoutService.readAcceptedPayoutsFromUser(userId, from, to);
    }
}
