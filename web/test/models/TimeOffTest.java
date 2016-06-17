package models;

import business.timetracking.RequestState;
import business.timetracking.TimeOffInvalidDateException;
import business.timetracking.TimeOffNotFoundException;
import business.timetracking.TimeOffType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;

import static org.mockito.Mockito.mock;

/**
 * Created by stefan on 11.05.16.
 */
public class TimeOffTest {
   TimeOff _testee;
   User _testUser;

   @Before
   public void setUp() throws Exception {
      _testUser = mock(User.class);
      _testee = new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(8),
          TimeOffType.BUSINESS_TRIP, RequestState.DONE, "comment");
   }

   @Test(expected = TimeOffNotFoundException.class)
   public void createNewTimeOff_WithUserEqualsNull_ShouldFail() throws Exception {
      new TimeOff(null, DateTime.now(), DateTime.now().plusHours(8),
          TimeOffType.EDUCATIONAL_LEAVE, RequestState.REQUEST_ACCEPTED, "comment");
   }

   @Test(expected = TimeOffNotFoundException.class)
   public void createNewTimeOff_WithTypeEqualsNull_ShouldFail() throws Exception {
      new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(8),
          null, RequestState.REQUEST_ACCEPTED, "comment");
   }

   @Test(expected = TimeOffNotFoundException.class)
   public void createNewTimeOff_WithStateEqualsNull_ShouldFail() throws Exception {
      new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(8),
          TimeOffType.EDUCATIONAL_LEAVE, null, "comment");
   }

   @Test(expected = TimeOffInvalidDateException.class)
   public void setFromTime_AfterToTime_ShouldFail() throws Exception {
      _testee.setFrom(DateTime.now().plusDays(10));
   }

   @Test
   public void setFromTime_BeforeToTime_ShouldSucceed() throws Exception {
      _testee.setFrom(DateTime.now().minusHours(10));
   }

   @Test(expected = TimeOffInvalidDateException.class)
   public void setToTime_BeforeFromTime_ShouldFail() throws Exception {
      _testee.setTo(DateTime.now().minusHours(10));
   }

   @Test
   public void setToTime_AfterFromTime_ShouldSucceed() throws Exception {
      _testee.setTo(DateTime.now().plusHours(5));
   }
}
