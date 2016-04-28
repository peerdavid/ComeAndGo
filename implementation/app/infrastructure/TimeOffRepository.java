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

    void createTimeOff(TimeOff timeoff);

    TimeOff readTimeOff(int id) throws TimeTrackException;

    List<TimeOff> readTimeOffFromUser(User user, DateTime from, DateTime to) throws TimeTrackException;

    void deleteTimeOff(TimeOff timeoff);

    void updateTimeOff(TimeOff timeoff);
}
