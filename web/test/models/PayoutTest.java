package models;

import business.timetracking.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by stefan on 12.05.16.
 */
public class PayoutTest {
   Payout _testee;
   User _testUser;

   @Before
   public void setUp() throws Exception {
      _testUser = mock(User.class);
      _testee = new Payout(_testUser, PayoutType.HOLIDAY_PAYOUT, 10, RequestState.REQUEST_SENT, "comment");
   }

   @Test(expected = TimeTrackException.class)
   public void createNewPayout_WithUserEqualsNull_ShouldFail() throws Exception {
      new Payout(null, PayoutType.HOLIDAY_PAYOUT, 10, RequestState.REQUEST_SENT, "comment");
   }

   @Test(expected = TimeTrackException.class)
   public void createNewPayout_WithTypeEqualsNull_ShouldFail() throws Exception {
      new Payout(_testUser, null, 10, RequestState.REQUEST_SENT, "comment");
   }

   @Test(expected = TimeTrackException.class)
   public void createNewPayout_WithStateEqualsNull_ShouldFail() throws Exception {
      new Payout(_testUser, PayoutType.HOLIDAY_PAYOUT, 10, null, "comment");
   }

   @Test(expected = TimeTrackException.class)
   public void setAmount_ToNegativeNumber_ShouldFail() throws Exception {
      _testee.setAmount(-1);
   }

   @Test
   public void setAmount_ToValidNumber_ShouldSucceed() throws Exception {
      _testee.setAmount(1);
   }
}
