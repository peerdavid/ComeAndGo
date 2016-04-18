package infrastructure;

import business.UserException;
import business.usermanagement.SecurityRole;
import com.avaje.ebean.Ebean;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
      _timeTrackRepository = new TimeTrackingRepositoryImpl();
   }

   @Test
   public void searchingTimeTrackList_WithFromDateAfterToDate_ShouldReturnEmptyList() {
      List<TimeTrack> testList = _timeTrackRepository.readTimeTracks(_testuser, DateTime.now(), DateTime.now().minusHours(1));
      int expected = 0;
      Assert.assertEquals(testList.size(), expected);
   }
}
