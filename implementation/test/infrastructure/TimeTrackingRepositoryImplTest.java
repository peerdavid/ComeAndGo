package infrastructure;

import business.UserException;
import business.notification.NotificationSender;
import business.usermanagement.SecurityRole;
import com.avaje.ebean.Ebean;
import junit.framework.Assert;
import model.Break;
import model.TimeTrack;
import model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.NotFoundException;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
      _timeTrackRepository = mock(TimeTrackingRepository.class);
   }

   @Test
   public void user_comes_twice_should_throw_timetrackexception() {
   }
}
