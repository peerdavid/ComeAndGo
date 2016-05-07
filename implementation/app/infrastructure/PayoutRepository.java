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

    int createPayout(Payout payout);

    Payout readPayout(int id) throws TimeTrackException;

    List<Payout> readPayoutsFromUser(int userId, DateTime from, DateTime to) throws TimeTrackException;

    List<Payout> readPayouts(int userId) throws TimeTrackException;

    List<Payout> readAcceptedPayoutsFromUser(int userId, DateTime from, DateTime to) throws TimeTrackException;

    void deletePayout(Payout payout);

    void updatePayout(Payout payout);
}
