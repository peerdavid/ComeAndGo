package business.timetracking;

import business.UserException;
import business.TimeTrackException;
import models.TimeTrack;
import javassist.NotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
interface TimeTrackingService {
    int come(int userId) throws UserException;

    void go(int userId) throws UserException, NotFoundException;

    boolean isActive(int userId) throws UserException;

    boolean takesBreak(int userId) throws UserException;

    void createBreak(int userId) throws UserException, NotFoundException;

    void endBreak(int userId) throws UserException, NotFoundException, TimeTrackException;

    TimeTrack readTimeTrackById(int id) throws Exception;

    List<TimeTrack> readTimeTracks(int userId) throws UserException;

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws UserException;

    /*  EDIT / DELETE / ADD TIMETRACKS AND BREAKS  */
    void addTimeTrack(TimeTrack timeTrack) throws UserException;

    void deleteTimeTrack(TimeTrack timeTrack);

    void updateTimeTrack(TimeTrack timeTrack) throws UserException;
}
