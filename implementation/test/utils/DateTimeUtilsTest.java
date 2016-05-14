package utils;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by stefan on 14.05.16.
 */

public class DateTimeUtilsTest {

   DateTime now;

   @Before
   public void setUp() throws Exception {
      now = DateTime.now();
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
         DateTime expected = now.plusMillis(i * 100).plusMinutes(i * 2).plusHours(i * 3)
             .plusDays(i * 4).plusMonths(i * 5).plusYears(i * 6);

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


}
