package business.timetracking;

import models.TimeOff;
import models.TimeTrack;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
public interface InternalTimeTracking {
    List<TimeOff> readTimeOffs(int userId) throws Exception;
    List<TimeTrack> readTimeTracks(int userId) throws Exception;
}
