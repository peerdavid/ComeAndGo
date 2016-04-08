package business.timetracking;

import business.UserException;
import model.TimeTrack;
import model.User;
import javassist.NotFoundException;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
interface TimeTrackingService {
    int come(int userId) throws UserException;

    void go(int userId) throws NotFoundException, UserException;

    // ToDo: We need a decision here: should the facade directly use the repository?
    List<TimeTrack> readTimeTracks(int userId) throws UserException;
}
