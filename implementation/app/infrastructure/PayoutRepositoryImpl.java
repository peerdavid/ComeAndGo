package infrastructure;

import business.timetracking.TimeTrackException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import models.Payout;
import models.User;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

/**
 * Created by paz on 25.04.16.
 */
public class PayoutRepositoryImpl implements PayoutRepository {

    @Override
    public int createPayout(Payout payout) {
        Ebean.save(payout);
        // refresh and return auto generated id
        Ebean.refresh(payout);
        return payout.getId();
    }


    @Override
    public Payout readPayout(int id) throws TimeTrackException {
        Payout payout = Ebean.find(Payout.class)
                .where().eq("id", id)
                .findUnique();
        if (payout == null) {
            throw new TimeTrackException("Payout entry not found");
        }
        return payout;
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
    public List<Payout> readPayoutsFromUser(User user, DateTime from, DateTime to) throws TimeTrackException {

        List<Payout> payouts =
                Ebean.find(Payout.class)
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

        if (payouts == null || payouts.isEmpty()) {
            return Collections.emptyList();
        }

        return payouts;
    }


    @Override
    public List<Payout> readPayouts(User user) throws TimeTrackException {
        List<Payout> payouts =
                Ebean.find(Payout.class)
                        .where().eq("user_id", user.getId())
                        .findList();

        if (payouts == null || payouts.isEmpty()) {
            throw new TimeTrackException("no such payouts found");
        }

        return payouts;
    }


    @Override
    public void deletePayout(Payout payout) {
        Ebean.delete(payout);
    }


    @Override
    public void updatePayout(Payout payout) {
        Ebean.update(payout);
    }
}
