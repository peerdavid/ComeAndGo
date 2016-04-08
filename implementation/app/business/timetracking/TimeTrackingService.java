package business.timetracking;

import model.TimeTrack;
import model.User;
import javassist.NotFoundException;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
interface TimeTrackingService {
    int come(int userId);
    void go(int userId) throws NotFoundException;

    // ToDo: We need a decision here: should the facade directly use the repository?
    List<TimeTrack> readTimeTracks(int userId);
}
