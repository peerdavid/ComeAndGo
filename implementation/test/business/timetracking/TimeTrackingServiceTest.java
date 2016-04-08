package business.timetracking;

import business.UserException;
import business.notification.NotificationSender;
import infrastructure.TimeTrackingRepository;
import infrastructure.UserRepository;
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
    UserRepository _userRepository;
    TimeTrackingService _testee;
    User _testUser;


    @Before
    public void SetUp() throws Exception {
        _testUser = new User("", "", "", "", "", "user@paz.at", true);

        _notificationSenderMock = mock(NotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _userRepository = mock(UserRepository.class);

        _testee = new TimeTrackingServiceImpl(_timeTrackingRepository, _notificationSenderMock, _userRepository);
    }


    @Test
    public void come_ForExistingUser_ShouldReturnIdAndCallRepository() throws UserException {
        // Prepare
        when(_timeTrackingRepository.createTimeTrack(any(TimeTrack.class), any(User.class))).thenReturn(7); // If the tested function calls our repository mock, we always return 7
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        int expected = 7;
        int userId = 8;

        // Call
        int actual = _testee.come(userId);    // Call the function, which should also call the repository

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId); // Check if the function really called our repository
        Mockito.verify(_timeTrackingRepository, times(1)).createTimeTrack(any(TimeTrack.class), any(User.class)); // Check if the function really called our repository
        Assert.assertEquals(expected, actual);
    }
}
