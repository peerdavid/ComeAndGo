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

    void go(int userId);

    void startBreak(int userId);

    void endBreak(int userId);

    TimeTrackState getState(int userId);

    List<TimeTrack> readTimeTracks(int userId);

    List<TimeTrack> readTimeTracks(int userId, DateTime from, DateTime to);

    /**
     * adds the new timeTrack to repository
     * @param timeTrack
     */
    void addTimeTrack(TimeTrack timeTrack);

    /**
     * delete timeTrack
     * @param timeTrack
     */
    void deleteTimeTrack(TimeTrack timeTrack);

    void updateTimeTrack(TimeTrack timeTrack);

    /**
     * add breakToInsert which belongs to the specified timeTrack
     * @param timeTrack
     * @param breakToInsert
     */
    void addBreak(TimeTrack timeTrack, Break breakToInsert);

    void deleteBreak(Break breakToDelete);

    void updateBreak(Break breakToUpdate);
    
}
