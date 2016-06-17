package infrastructure;

import business.timetracking.RequestState;
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

    @Override
    public List<Payout> readAcceptedPayoutsFromUser(int userId) throws TimeTrackException {
        List<Payout> payouts =
            Ebean.find(Payout.class)
                .where().and(
                    Expr.eq("user_id", userId),
                    Expr.eq("state", RequestState.REQUEST_ACCEPTED)
                ).findList();

        if (payouts == null || payouts.isEmpty()) {
            return Collections.emptyList();
        }

        return payouts;
    }

    @Override
    public List<Payout> readPayouts(int userId) throws TimeTrackException {
        List<Payout> payouts =
                Ebean.find(Payout.class)
                        .where().eq("user_id", userId)
                        .findList();

        if (payouts == null || payouts.isEmpty()) {
            return Collections.emptyList();
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
