package business.timetracking;

import models.Payout;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
public interface InternalTimeTracking {
    List<TimeOff> readTimeOffs(int userId) throws Exception;
    List<TimeOff> readTimeOffs(int userId, DateTime from, DateTime to) throws Exception;

    List<TimeTrack> readTimeTracks(int userId) throws Exception;
    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception;

    List<Payout> readPayouts(int userId) throws Exception;
}
