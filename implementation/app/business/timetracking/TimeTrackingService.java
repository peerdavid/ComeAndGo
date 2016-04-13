package business.timetracking;

import business.UserException;
import infrastructure.TimeTrackException;
import model.TimeTrack;
import model.User;
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

    List<TimeTrack> readTimeTracks(int userId) throws UserException, NotFoundException;

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws UserException, NotFoundException;
}
