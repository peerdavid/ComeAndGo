package business.timetracking;

import business.UseCases;
import model.TimeTrack;
import model.User;

import java.util.List;

/**
 * Created by david on 21.03.16.
 */
public interface TimeTracking extends UseCases {
    void come() throws Exception;
    void go();
    List<TimeTrack> readTimeTracks(User user);
}
