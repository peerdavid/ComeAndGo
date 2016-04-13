package business.timetracking;

import business.UseCases;
import com.avaje.ebean.config.JsonConfig;
import model.TimeTrack;
import model.User;
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
}
