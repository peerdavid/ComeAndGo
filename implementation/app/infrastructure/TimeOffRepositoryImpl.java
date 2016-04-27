package infrastructure;

import business.timetracking.TimeTrackException;
import com.avaje.ebean.Ebean;
import models.TimeOff;

/**
 * Created by paz on 25.04.16.
 */
public class TimeOffRepositoryImpl implements TimeOffRepository {

    @Override
    public void createTimeOff(TimeOff timeoff) {
        Ebean.save(timeoff);
    }

    @Override
    public TimeOff readTimeOff(int id) throws TimeTrackException {
        TimeOff timeOff = Ebean.find(TimeOff.class)
                .where().eq("id", id)
                .findUnique();
        if(timeOff == null) {
            throw new TimeTrackException("TimeOff entry not found");
        }
        return timeOff;
    }

    @Override
    public void deleteTimeOff(TimeOff timeoff) {
        Ebean.delete(timeoff);
    }

    @Override
    public void updateTimeOff(TimeOff timeoff) {
        Ebean.update(timeoff);
    }
}
