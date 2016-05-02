package infrastructure;

import business.timetracking.TimeTrackException;
import models.Payout;
import models.TimeOff;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by paz on 25.04.16.
 */
public interface PayoutRepository {

    void createPayout(Payout payout);

    Payout readPayout(int id) throws TimeTrackException;

    List<Payout> readPayoutsFromUser(User user, DateTime from, DateTime to) throws TimeTrackException;

    List<Payout> readPayouts(User user) throws TimeTrackException;

    void deletePayout(Payout payout);

    void updatePayout(Payout payout);
}
