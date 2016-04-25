package infrastructure;

import business.timetracking.TimeTrackException;
import business.UserException;
import models.Break;
import models.TimeTrack;
import models.User;
import javassist.NotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public interface TimeTrackingRepository {

    int createTimeTrack(TimeTrack timeTrack) throws UserException;

    TimeTrack readTimeTrack(int id) throws NotFoundException;

    TimeTrack readActiveTimeTrack(User user) throws NotFoundException;

    List<TimeTrack> readTimeTracks(User user);

    List<TimeTrack> readTimeTracks(User user, DateTime from, DateTime to);

    List<TimeTrack> readTimeTracksOverlay(User user, TimeTrack timeTrack);

    void updateTimeTrack(TimeTrack timeTrack);

    void deleteTimeTrack(TimeTrack timeTrack);

    Break readActiveBreak(User user) throws NotFoundException;

    void deleteBreak(Break actualBreak);

    void updateBreak(Break actualBreak);
}
