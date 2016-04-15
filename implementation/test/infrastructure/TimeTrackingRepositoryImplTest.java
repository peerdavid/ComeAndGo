package infrastructure;

import business.UserException;
import business.usermanagement.SecurityRole;
import models.TimeTrack;
import models.User;
import org.junit.Before;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Created by stefan on 11.04.16.
 */
public class TimeTrackingRepositoryImplTest {

   private TimeTrackingRepository _timeTrackRepository;
   private User _testuser;
   private TimeTrack _timetrack;

   @Before
   public void setUp() throws UserException {
      _testuser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", false, "testBoss");
      _timetrack = new TimeTrack(_testuser);
      _timeTrackRepository = mock(TimeTrackingRepository.class);
   }


/*   @Test(expected = TimeTrackException.class)
   public void searchingTimeTrackList_ForNoUserGiven_ShouldThrowTimeTrackException() throws TimeTrackException {
      _timeTrackRepository.readTimeTracks(null);
   }*/


/*   @Test(expected = TimeTrackException.class)
   public void searchingTimeTrackList_WithNoResults_ShouldThrowTimeTrackException() throws TimeTrackException {
      // prepare
      when(Ebean.find(TimeTrack.class)).thenReturn(null);

      _timeTrackRepository.readTimeTracks(_testuser);
   }*/

/*   @Test(expected = TimeTrackException.class)
   public void searchingTimeTrackList_WithFromDateAfterToDate_ShouldThrowTimeTrackException() throws TimeTrackException {
      _timeTrackRepository.readTimeTracks(_testuser, DateTime.now(), DateTime.now().minusHours(1));
   }*/

/*   @Test(expected = TimeTrackException.class)
   public void searchingTimeTrackList_WithCorrectDatesButNoResults_ShouldThrowTimeTrackException() throws TimeTrackException {
      //prepare
      when(Ebean.find(TimeTrack.class).findList()).thenReturn(null);

      _timeTrackRepository.readTimeTracks(_testuser, DateTime.now(), DateTime.now().plusHours(1));
   }

   @Test
   public void searchingTimeTrackList_WithCorrectDatesAndResult_ShouldSucceed() throws TimeTrackException {
      //prepare
      when(Ebean.find(TimeTrack.class).findList()).thenReturn(Collections.emptyList());

      _timeTrackRepository.readTimeTracks(_testuser, DateTime.now(), DateTime.now().plusHours(1));
   }*/
}
