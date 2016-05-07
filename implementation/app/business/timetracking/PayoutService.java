package business.timetracking;

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
}
