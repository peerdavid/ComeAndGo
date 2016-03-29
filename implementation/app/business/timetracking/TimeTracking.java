package business.timetracking;

import domain.TimeTrack;
import domain.User;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public interface TimeTracking {
    void come() throws Exception;
    void go();
    List<TimeTrack> readTimeTracks(User user);
}
