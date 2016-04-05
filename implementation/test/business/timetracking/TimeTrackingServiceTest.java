package business.timetracking;

import business.notification.NotificationSender;
import infrastructure.TimeTrackingRepository;
import model.TimeTrack;
import model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Unit test example.
 */
public class TimeTrackingServiceTest {

    NotificationSender _notificationSenderMock;
    TimeTrackingRepository _timeTrackingRepository;
    TimeTrackingService _testee;
    User _testUser;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("", "", "", "", "", "user@paz.at", true);

        _notificationSenderMock = mock(NotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _testee = new TimeTrackingServiceImpl(_timeTrackingRepository, _notificationSenderMock);
    }


    @Test
    public void come_ForExistingUser_ShouldReturnIdAndCallRepository(){
        // Prepare
        when(_timeTrackingRepository.createTimeTrack(any(TimeTrack.class))).thenReturn(7); // If the tested function calls our repository mock, we always return 7
        int expected = 7;

        // Call
        int actual = _testee.come();    // Call the function, which should also call the repository

        // Validate
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(any()); // Check if the function really called our repository
        Assert.assertEquals(expected, actual);
    }
}
