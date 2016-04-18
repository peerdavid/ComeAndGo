package business.timetracking;

import business.UseCases;
import models.Break;
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

    List<TimeTrack> readTimeTracks(int userId) throws Exception;

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to) throws Exception;

    /**
     * adds the new timeTrack to repository
     * @param timeTrack
     */
    void addTimeTrack(TimeTrack timeTrack) throws Exception;

    /**
     * delete timeTrack
     * @param timeTrack
     */
    void deleteTimeTrack(TimeTrack timeTrack) throws Exception;

    void updateTimeTrack(TimeTrack timeTrack) throws Exception;

    /**
     * add breakToInsert which belongs to the specified timeTrack
     * @param timeTrack
     * @param breakToInsert
     */
    void addBreak(TimeTrack timeTrack, Break breakToInsert) throws Exception;

    void deleteBreak(Break breakToDelete) throws Exception;

    void updateBreak(Break breakToUpdate) throws Exception;
    
}
