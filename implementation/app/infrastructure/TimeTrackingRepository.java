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

    int createTimeTrack(TimeTrack timeTrack, User user) throws TimeTrackException;

    TimeTrack readTimeTrack(int id) throws TimeTrackException;

    TimeTrack getActiveTimeTrack(User user) throws NotFoundException;

    List<TimeTrack> readTimeTracks(User user) throws TimeTrackException;

    List<TimeTrack> readTimeTracks(User user, DateTime from, DateTime to) throws TimeTrackException;

    void updateTimeTrack(TimeTrack timeTrack);

    void deleteTimeTrack(TimeTrack timeTrack);

    Break getActiveBreak(User user) throws NotFoundException;

    void deleteBreak(Break actualBreak) throws TimeTrackException;

    void updateBreak(Break actualBreak) throws TimeTrackException;

    void startBreak(User user) throws TimeTrackException, NotFoundException;

    void endBreak(Break actualBreak) throws TimeTrackException;

    void endBreak(User user) throws TimeTrackException, NotFoundException;

    /*  EDIT / DELETE / ADD timeTracks and breaks  */
    void addTimeTrack(TimeTrack timeTrack) throws UserException;

    void addBreak(TimeTrack timeTrack, Break breakToInsert) throws UserException;
}
