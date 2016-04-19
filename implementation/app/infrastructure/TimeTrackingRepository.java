package infrastructure;

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

    int createTimeTrack(TimeTrack timeTrack, User user) throws UserException;

    TimeTrack readTimeTrack(int id) throws NotFoundException;

    TimeTrack getActiveTimeTrack(User user) throws NotFoundException;

    List<TimeTrack> readTimeTracks(User user);

    List<TimeTrack> readTimeTracks(User user, DateTime from, DateTime to);

    List<TimeTrack> readTimeTracksOverlay(User user, TimeTrack timeTrack);

    List<Break> readBreakListOverlay(TimeTrack timeTrack, Break breakToInsert);

    void updateTimeTrack(TimeTrack timeTrack);

    void deleteTimeTrack(TimeTrack timeTrack);

    Break getActiveBreak(User user) throws NotFoundException;

    void deleteBreak(Break actualBreak);

    void updateBreak(Break actualBreak);

    void startBreak(User user) throws NotFoundException;

    void endBreak(Break actualBreak) throws TimeTrackException, UserException;

    void endBreak(User user) throws TimeTrackException, NotFoundException, UserException;

    /**
     *  ADD timeTracks and corresponding breaks  */
    void addTimeTrack(TimeTrack timeTrack) throws UserException;

    //void updateTimeTrack(TimeTrack timeTrack) throws UserException;
}
