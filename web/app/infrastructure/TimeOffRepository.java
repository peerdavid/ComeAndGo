package infrastructure;

import business.timetracking.TimeTrackException;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by paz on 25.04.16.
 */
public interface TimeOffRepository {

    int createTimeOff(TimeOff timeoff);

    TimeOff readTimeOff(int id) throws TimeTrackException;

    List<TimeOff> readTimeOffsFromUser(User user, DateTime from, DateTime to) throws TimeTrackException;

    List<TimeOff> readTimeOffs(User user) throws TimeTrackException;

    void updateTimeOff(TimeOff timeoff);

    void deleteTimeOff(TimeOff timeoff);
}
