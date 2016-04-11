package infrastructure;

import business.notification.NotificationSender;
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
   public void setUp() {
      _testuser = new User("abc", "mypw", "", "abc", "def", "asdf@jkli.at", false);
      _timetrack = new TimeTrack(_testuser);
      _timeTrackRepository = mock(TimeTrackingRepository.class);
   }

   @Test
   public void user_comes_twice_should_throw_timetrackexception() {
   }
}
