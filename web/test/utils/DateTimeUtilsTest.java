package utils;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by stefan on 14.05.16.
 */

public class DateTimeUtilsTest {

   DateTime now;
   DateTime leapYear;

   @Before
   public void setUp() throws Exception {
      now = DateTime.now();

      // 2016 was leap year
      // let leapYear always point to February, 2016
      if(now.getMonthOfYear() == 1) {
         leapYear = now.plusMonths(1).minusYears(2016 - now.getYear());
      }
      else {
         leapYear = now.minusMonths(now.getMonthOfYear() - 2).minusYears(2016 - now.getYear());
      }
   }

   @Test
   public void getStartOfYear_ShouldResultInFirstJanuaryMidnight_ShouldSucceed() {
      DateTime result = DateTimeUtils.startOfActualYear();

      // check dd.mm.yy
      Assert.assertEquals(1, result.getDayOfMonth());
      Assert.assertEquals(1, result.getMonthOfYear());
      Assert.assertEquals(now.getYear(), result.getYear());

      // check hh:mm:ss
      Assert.assertEquals(0, result.getHourOfDay());
      Assert.assertEquals(0, result.getMinuteOfHour());
      Assert.assertEquals(0, result.getSecondOfMinute());
      Assert.assertEquals(0, result.getMillisOfSecond());
   }

   @Test
   public void getEndOfYear_ShouldResultIn31stOfDecemberOneMinuteBeforeMidnight_ShouldSucceed() {
      DateTime result = DateTimeUtils.endOfActualYear();

      // check dd.mm.yy
      Assert.assertEquals(31, result.getDayOfMonth());
      Assert.assertEquals(12, result.getMonthOfYear());
      Assert.assertEquals(now.getYear(), result.getYear());

      // check hh:mm:ss
      Assert.assertEquals(23, result.getHourOfDay());
      Assert.assertEquals(59, result.getMinuteOfHour());
      Assert.assertEquals(59, result.getSecondOfMinute());
      Assert.assertEquals(999, result.getMillisOfSecond());
   }

   @Test
   public void getStartOfDay_ShouldResultAtMidnightOnSameDate_ShouldSucceed() {
      for(int i = 0; i < 10; i++) {
         DateTime expected = now.plusMillis(i * 100);
         expected = expected.plusHours(i * 5);

         DateTime result = DateTimeUtils.startOfDay(expected);

         // check date
         Assert.assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
         Assert.assertEquals(expected.getMonthOfYear(), result.getMonthOfYear());
         Assert.assertEquals(expected.getYear(), result.getYear());

         // check time
         Assert.assertEquals(0, result.getHourOfDay());
         Assert.assertEquals(0, result.getMinuteOfHour());
         Assert.assertEquals(0, result.getSecondOfMinute());
      }
   }

   @Test
   public void getEndOfDay_ShouldResultAtOneMinuteBeforeMidnight_ShouldSucceed() {
      for(int i = 10; i < 20; i++) {
         DateTime expected = now.plusMillis(i * 200).plusMinutes(i * 3).plusHours(i * 4)
             .plusDays(i * 5).plusMonths(i * 6).plusYears(i * 7);

         DateTime result = DateTimeUtils.endOfDay(expected);

         // check date
         Assert.assertEquals(expected.getDayOfMonth(), result.getDayOfMonth());
         Assert.assertEquals(expected.getMonthOfYear(), result.getMonthOfYear());
         Assert.assertEquals(expected.getYear(), result.getYear());

         // check time
         Assert.assertEquals(23, result.getHourOfDay());
         Assert.assertEquals(59, result.getMinuteOfHour());
         Assert.assertEquals(59, result.getSecondOfMinute());
      }
   }

   @Test
   public void getStartOfMonth_ShouldAlwaysReturnFirstDayInMonthAtMidnight_ShouldSucceed() {
      for(int i = 1; i < 13; ++i) {
         DateTime expected = now.plusMonths(i);
         DateTime result = DateTimeUtils.startOfMonth(expected);

         // check date
         Assert.assertEquals(1, result.getDayOfMonth());
         Assert.assertEquals(expected.getMonthOfYear(), result.getMonthOfYear());
         Assert.assertEquals(expected.getYear(), result.getYear());

         // check time
         Assert.assertEquals(0, result.getHourOfDay());
         Assert.assertEquals(0, result.getMinuteOfHour());
         Assert.assertEquals(0, result.getSecondOfMinute());
         Assert.assertEquals(0, result.getMillisOfSecond());
      }
   }

   @Test
   public void getEndOfMonth_ShouldAlwaysReturn30thOr31stAtOneMinuteBeforeMidnight_ShouldSucceed() {
      for(int i = 0; i < 13; ++i) {
         DateTime expected = leapYear.plusMonths(i);
         DateTime result = DateTimeUtils.endOfMonth(expected);

         int expectedNumberOfDays;
         switch(expected.getMonthOfYear()) {
            case 1:
               expectedNumberOfDays = 31;
               break;
            case 2: {
               // leap year
               int year = expected.getYear();
               if (isLeapYear(year))
                  expectedNumberOfDays = 29;
               else
                  expectedNumberOfDays = 28;
               break;
            }
            case 3:
               expectedNumberOfDays = 31;
               break;
            case 4:
               expectedNumberOfDays = 30;
               break;
            case 5:
               expectedNumberOfDays = 31;
               break;
            case 6:
               expectedNumberOfDays = 30;
               break;
            case 7:
               expectedNumberOfDays = 31;
               break;
            case 8:
               expectedNumberOfDays = 31;
               break;
            case 9:
               expectedNumberOfDays = 30;
               break;
            case 10:
               expectedNumberOfDays = 31;
               break;
            case 11:
               expectedNumberOfDays = 30;
               break;
            case 12:
               expectedNumberOfDays = 31;
               break;
            default:
               throw new RuntimeException();
         }

         // check date
         Assert.assertEquals(expectedNumberOfDays, result.getDayOfMonth());
         Assert.assertEquals(expected.getMonthOfYear(), result.getMonthOfYear());
         Assert.assertEquals(expected.getYear(), result.getYear());

         // check time
         Assert.assertEquals(23, result.getHourOfDay());
         Assert.assertEquals(59, result.getMinuteOfHour());
         Assert.assertEquals(59, result.getSecondOfMinute());
         Assert.assertEquals(999, result.getMillisOfSecond());
      }
   }

   @Test
   public void getStartOfWeek_WithDifferentStartDates_ShouldSucceed() {
      DateTime now = DateTime.now();

      for(int i = 0; i < 10; ++i) {
         DateTime startOfWeek = DateTimeUtils.startOfWeek(now.plusHours(i * 10).plusWeeks(i));
         Assert.assertEquals(DateTimeConstants.MONDAY, startOfWeek.getDayOfWeek());
         Assert.assertEquals(0, startOfWeek.getHourOfDay());
         Assert.assertEquals(0, startOfWeek.getMinuteOfHour());
         Assert.assertEquals(0, startOfWeek.getSecondOfMinute());
         Assert.assertEquals(0, startOfWeek.getMillisOfSecond());
      }
   }

   @Test
   public void getEndOfWeek_WithDifferentStartDates_ShouldSucceed() {
      DateTime now = DateTime.now();

      for(int i = 0; i < 15; ++i) {
         DateTime startOfWeek = DateTimeUtils.endOfWeek(now.plusHours(i * 5).plusWeeks(i * 2));

         Assert.assertEquals(DateTimeConstants.SUNDAY, startOfWeek.getDayOfWeek());
         Assert.assertEquals(23, startOfWeek.getHourOfDay());
         Assert.assertEquals(59, startOfWeek.getMinuteOfHour());
         Assert.assertEquals(59, startOfWeek.getSecondOfMinute());
         Assert.assertEquals(999, startOfWeek.getMillisOfSecond());
      }
   }

   @Test
   public void getDateTimeDifference_ForAFewDays_ShouldSucceed() {
      // MONDAY, 8am
      DateTime observedStart = DateTimeUtils.startOfWeek(now).plusHours(8);
      // MONDAY, 4am
      DateTime observedEnd = DateTimeUtils.endOfWeek(now).plusHours(4).plusMillis(1);
      // time difference is so 7days * 24h * 60min - 4h

      long expectedValue = ((7 * 24) - 4) * 60;
      long actualValue = DateTimeUtils.getDateTimeDifferenceInMinutes(observedEnd, observedStart);
      Assert.assertEquals(expectedValue, actualValue);
   }

   @Test
   public void getDateTimeDifference_ForAFewWeeks_ShouldSucceed() {
      // always starts at 25.12. - 12:00
      DateTime observedStart = DateTimeUtils.startOfActualYear().minusDays(7).plusHours(12);
      // always ends on 3.2. - 00:00
      DateTime observedEnd = DateTimeUtils.startOfActualYear().plusDays(33);
      // time difference is so 39 days and 12 hours
      long expectedValue = 39 * DateTimeConstants.MINUTES_PER_DAY + 12 * 60;
      long actualValue = DateTimeUtils.getDateTimeDifferenceInMinutes(observedEnd, observedStart);
      Assert.assertEquals(expectedValue, actualValue);
   }

   @Test
   public void getDateDifference_ForSameDatesButOtherYear_ShouldSucceed() {
      DateTime observedStart = now.minusYears(1);
      boolean addAdditionalDayForLeapYear = (isLeapYear(now.getYear()) && now.getMonthOfYear() > 3)
         || ((isLeapYear(now.getYear() - 1) && (now.getMonthOfYear() < 3)));
      long expectedValue = 365 * 24 * 60 + (addAdditionalDayForLeapYear ? DateTimeConstants.MINUTES_PER_DAY : 0);
      long actualValue = DateTimeUtils.getDateTimeDifferenceInMinutes(now, observedStart);
      Assert.assertEquals(expectedValue, actualValue);
   }

   @Test
   public void getDatetimeDifference_ForSameDates_ShouldResultIn0() {
      Assert.assertEquals(0, DateTimeUtils.getDateTimeDifferenceInMinutes(now, now));
   }


   private boolean isLeapYear(int year) {
      if(year % 4 == 0) {
         if(year % 100 == 0) {
            if(year % 400 == 0) {
               return true;
            }
            return false;
         }
         return true;
      }
      return false;
   }

}
