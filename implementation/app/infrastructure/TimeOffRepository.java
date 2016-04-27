package infrastructure;

import business.timetracking.TimeTrackException;
import models.TimeOff;

/**
 * Created by paz on 25.04.16.
 */
public interface TimeOffRepository {

    void createTimeOff(TimeOff timeoff);

    TimeOff readTimeOff(int id) throws TimeTrackException;

    void deleteTimeOff(TimeOff timeoff);

    void updateTimeOff(TimeOff timeoff);
}
