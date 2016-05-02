package infrastructure;

import business.timetracking.TimeOffNotFoundException;
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


    /**
     * The following things can happen, when reading timetracks from the db which are in range:
     *
     *      +-------------+
     *      |Request range|
     *      +-------------+
     *      .             .
     *  +---------+       .
     *  |  Case1  |       .
     *  +---------+       .
     *      .        +--------+
     *      .        | Case2  |
     *      .        +--------+
     *      .             .
     * +-----------------------+
     * |       Case3           |
     * +-----------------------+
     *      .             .
     *      . +---------+ .
     *      . |  Case4  | .
     *      . +---------+ .
     */
    @Override
    public List<TimeOff> readTimeOffsFromUser(User user, DateTime from, DateTime to) throws TimeTrackException {

        List<TimeOff> timeOffs =
                Ebean.find(TimeOff.class)
                        .where().and(
                        Expr.eq("user_id", user.getId()),
                        Expr.or(
                                Expr.or(
                                        Expr.between("start", "end", from),     // Case 1
                                        Expr.between("start", "end", to)),      // Case 2
                                Expr.or(
                                        Expr.and(                               // Case 3
                                                Expr.lt("start", from),
                                                Expr.gt("end", to)
                                        ),
                                        Expr.and(                               // Case 4
                                                Expr.gt("start", from),
                                                Expr.lt("end", to)
                                        )
                                )
                        )
                ).findList();

        if(timeOffs == null || timeOffs.isEmpty()) {
            throw new TimeOffNotFoundException("no such timeOffs found");
        }

        return timeOffs;
    }


    @Override
    public List<TimeOff> readTimeOffs(User user) throws TimeTrackException {
        List<TimeOff> timeOffs =
                Ebean.find(TimeOff.class)
                        .where().eq("user_id", user.getId())
                        .findList();

        if(timeOffs == null || timeOffs.isEmpty()) {
            throw new TimeOffNotFoundException("no such timeOffs found");
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
