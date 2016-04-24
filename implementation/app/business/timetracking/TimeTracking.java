package business.timetracking;

import business.UseCases;
import models.TimeOff;
import models.TimeTrack;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public interface TimeTracking extends UseCases {
    void come(int userId) throws Exception;

    void go(int userId) throws Exception;

    void startBreak(int userId) throws Exception;

    void endBreak(int userId) throws Exception;

    TimeTrackState getState(int userId) throws Exception;

    TimeTrack readTimeTrackById(int id) throws Exception;

    List<TimeTrack> readTimeTracks(int userId) throws Exception;

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception;

    /**
     * adds the new timeTrack and corresponding breaks to repository
     */
    void addTimeTrack(int userId, DateTime from, DateTime to) throws Exception;

    /**
     * delete whole timeTrack plus corresponding breaks
     * @param timeTrack
     */
    void deleteTimeTrack(TimeTrack timeTrack) throws Exception;

    /**
     * use this method to update timeTracks and the breaks inside this timeTrack
     * this method can also be used to delete breaks, specify times of breaks and so on
     * @param timeTrack
     * @throws Exception
     */
    void updateTimeTrack(TimeTrack timeTrack) throws Exception;

}
