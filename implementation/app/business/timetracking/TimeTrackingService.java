package business.timetracking;

import business.UserException;
import infrastructure.TimeTrackException;
import models.Break;
import models.TimeTrack;
import javassist.NotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
interface TimeTrackingService {
    int come(int userId) throws UserException, TimeTrackException;

    void go(int userId) throws NotFoundException, UserException, TimeTrackException;

    boolean isActive(int userId) throws UserException, NotFoundException;

    boolean takesBreak(int userId) throws UserException, NotFoundException;

    void createBreak(int userId) throws UserException, NotFoundException, TimeTrackException;

    void endBreak(int userId) throws UserException, NotFoundException, TimeTrackException;

    List<TimeTrack> readTimeTracks(int userId) throws UserException, TimeTrackException;

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws UserException, TimeTrackException;

    /*  EDIT / DELETE / ADD TIMETRACKS AND BREAKS  */
    void addTimeTrack(TimeTrack timeTrack) throws UserException;

    void deleteTimeTrack(TimeTrack timeTrack);

    void updateTimeTrack(TimeTrack timeTrack);

    void addBreak(TimeTrack timeTrack, Break breakToInsert) throws UserException;

    void deleteBreak(Break breakToDelete) throws TimeTrackException;

    void updateBreak(Break breakToUpdate) throws TimeTrackException;
}
