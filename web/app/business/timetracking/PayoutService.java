package business.timetracking;

import models.Payout;
import models.User;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by stefan on 07.05.16.
 */
interface PayoutService {
   void requestOvertimePayout(int userId, int numberOfHours, String comment) throws Exception;

   void requestHolidayPayout(int userId, int numberOfDays, String comment) throws Exception;

   void acceptHolidayPayout(int payoutId, int bossId) throws Exception;

   void rejectHolidayPayout(int payoutId, int bossId) throws Exception;

   void acceptOvertimePayout(int payoutId, int bossId) throws Exception;

   void rejectOvertimePayout(int payoutId, int bossId) throws Exception;

   Payout readPayout(int payoutId) throws Exception;

   List<Payout> readPayoutsFromUser(int userId) throws Exception;

   List<Payout> readAcceptedPayoutsFromUser(int userId) throws Exception;

   void updatePayoutRequest(Payout payout) throws Exception;

   void deletePayout(int payoutId) throws Exception;
}
