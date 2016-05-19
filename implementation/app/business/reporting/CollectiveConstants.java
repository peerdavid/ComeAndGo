package business.reporting;

/**
 * Created by stefan on 14.05.16.
 */
class CollectiveConstants {
   // TODO: fill in correct constants!
   protected static final int MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR = 120;
   protected static final int MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR = 40;

   protected static final int MIN_HOURS_FREETIME_BETWEEN_WORKTIMES = 11;
   protected static final int MIN_HOURS_FREETIMG_WHEN_NEXT_10_DAYS_BALANCE = 10;

   // holiday consumption
   protected static final int MAX_NUMBER_OF_UNUSED_HOLIDAY = 40;

   // sick leave
   protected static final double TOLERATED_WORKTIME_TO_SICKLEAVE_RATIO = 0.25;   // ratio of sick days and work days tolerated

   // percentage a user can over and under use his breaks
   protected static final double TOLERATED_BREAK_UNDEROVERUSE_PERCENTAGE = 0.1;
   protected static final int WORKTIME_TO_BREAK_RATIO = 16;             // hours per day / break per day

   protected static final int MAX_HOURS_PER_DAY = 10;



}
