package infrastructure;

import business.timetracking.TimeOffNullPointerException;
import business.timetracking.TimeTrackException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

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
    public List<TimeOff> readTimeOffFromUser(User user, DateTime from, DateTime to) throws TimeTrackException {
        List<TimeOff> timeOffs =
                Ebean.find(TimeOff.class)
                .where().eq("_user_id", user.getId())
                .where().or(
                        Expr.and(
                                Expr.le("start", from),
                                Expr.ge("end", from)
                        ),
                        Expr.and(
                                Expr.ge("end", to),
                                Expr.le("start", to)
                        )
                )
                .findList();

        if(timeOffs == null) {
            throw new TimeOffNullPointerException("no such timeOffs found");
        }
        return timeOffs;
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