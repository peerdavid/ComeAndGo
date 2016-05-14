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


}
