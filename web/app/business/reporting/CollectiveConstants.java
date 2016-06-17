package business.reporting;

/**
 * Created by stefan on 14.05.16.
 */
class CollectiveConstants {
   // general
   protected static final double MAX_PLUS_SALDO_OF_FLEXTIME_PER_YEAR = 154;
   protected static final double MAX_MINUS_SALDO_OF_FLEXTIME_PER_YEAR = -38.5 / 2;
   protected static final int MAX_DAYS_OF_WORK_PER_WEEK = 5;

   // special holiday
   protected static final int MAX_NUMBER_OF_UNUSED_HOLIDAY_PER_YEAR = 20;
   protected static final double TOLERATED_SICKLEAVE_DAYS_PER_MONTH = 2.5;   // ratio of sick days and work days tolerated

   // timeTrack
   protected static final int MAX_HOURS_PER_DAY = 10;
   protected static final int MIN_HOURS_FREETIME_BETWEEN_WORKTIMES = 11;

   // break
   protected static final int BREAK_MINUTES_PER_DAY = 30;
   protected static final double TOLERATED_BREAK_OVERUSE_PERCENTAGE = 1;    // 0.5 means users are allowed to take break BREAK_MINUTES_PER_DAY + 50%
   protected static final double TOLERATED_BREAK_UNDERUSE_PERCENTAGE = 0;    // 0 means at least BREAK_MINUTES_PER_DAY - 0%
   protected static final int NUMBER_OF_HOURS_TO_TAKE_BREAK = 6;
}
