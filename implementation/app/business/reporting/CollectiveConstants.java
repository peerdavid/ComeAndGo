package business.reporting;

/**
 * Created by stefan on 14.05.16.
 */
class CollectiveConstants {
   protected static final double MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR = 154;
   protected static final double MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR = -38.5 / 2;

   protected static final int WORK_DAYS_PER_YEAR = 320;

   protected static final int MIN_HOURS_FREETIME_BETWEEN_WORKTIMES = 11;
   protected static final int MAX_DAYS_OF_WORK_PER_WEEK = 5;
   protected static final int AVERAGE_AMOUNT_WORK_DAYS_PER_MONTH = 20;
   // holiday consumption
   protected static final int MAX_NUMBER_OF_UNUSED_HOLIDAY_PER_YEAR = 20;
   // sick leave
   protected static final double TOLERATED_SICKLEAVE_DAYS_PER_MONTH = 2.5;   // ratio of sick days and work days tolerated

   // percentage a user can over and under use his breaks
   protected static final double TOLERATED_BREAK_MISSUSE_PERCENTAGE = 0.1;    // 0.1 is 10%
   protected static final int BREAKMINUTES_PER_DAY = 30;

   protected static final int MAX_HOURS_PER_DAY = 10;



}
