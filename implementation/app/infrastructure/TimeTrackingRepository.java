package infrastructure;

import model.TimeTrack;
import model.User;
import javassist.NotFoundException;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public interface TimeTrackingRepository {

    void createTimeTrack(TimeTrack timeTrack);
    TimeTrack readTimeTrack(int id) throws NotFoundException;
    List<TimeTrack> readTimeTracks(User user);
    void updateTimeTrack(TimeTrack timeTrack);
    void deleteTimeTrack(TimeTrack timeTrack);
}
