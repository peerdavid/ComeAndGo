package business.timetracking;

import business.UserException;
import business.notification.NotificationSender;
import business.usermanagement.SecurityRole;
import infrastructure.TimeTrackException;
import infrastructure.TimeTrackingRepository;
import infrastructure.UserRepository;
import javassist.NotFoundException;
import model.Break;
import model.TimeTrack;
import model.User;
import org.joda.time.DateTime;
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
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, "testBoss");

        _notificationSenderMock = mock(NotificationSender.class);
        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _userRepository = mock(UserRepository.class);

        _testee = new TimeTrackingServiceImpl(_timeTrackingRepository, _notificationSenderMock, _userRepository);
    }


    @Test
    public void come_ForExistingUser_ShouldReturnIdAndCallRepository() throws UserException, TimeTrackException {
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

    @Test(expected = UserException.class)
    public void come_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws UserException, TimeTrackException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(null);
        int userId = 8;

        // Call
        _testee.come(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId); // Check if the function really called our repository
    }

    @Test
    public void go_WithComeCalledBefore_ShouldSetToInTimeTrackAndCallRepository() throws NotFoundException, UserException {
        // prepare
        TimeTrack timeTrack = new TimeTrack(_testUser);

        timeTrack.set_from(new DateTime(2016, 4, 7, 8, 0));
        when(_timeTrackingRepository.getActiveTimeTrack(any(User.class))).thenReturn(timeTrack);
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        int userId = 8;

        // Call
        _testee.go(userId);

        // Validate
        Mockito.verify(_timeTrackingRepository, times(1)).getActiveTimeTrack(_testUser);
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertNotEquals(timeTrack.get_to(), null);
    }

    @Test(expected = NotFoundException.class)
    public void go_WithoutComeCalledBefore_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_timeTrackingRepository.getActiveTimeTrack(any(User.class))).thenThrow(NotFoundException.class);
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        int userId = 8;

        // Call
        _testee.go(userId);

        // Validate
        Mockito.verify(_timeTrackingRepository, times(1)).getActiveTimeTrack(_testUser);
        Mockito.verify(_userRepository, times(1)).readUser(userId);
    }

    @Test(expected = UserException.class)
    public void go_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(null);

        int userId = 8;

        // Call
        _testee.go(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
    }

    @Test
    public void isActive_ForInactiveUser_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(_testUser);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _testee.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void isActive_ForInactiveUserButWasActiveBefore_ShouldCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(_testUser);
        Break testBreak = new Break(DateTime.now());
        when(_timeTrackingRepository.getActiveBreak(any(User.class))).thenReturn(testBreak);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _testee.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }

    @Test(expected = UserException.class)
    public void isActive_ForUnregisteredUser_ShouldThrowExceptionAndCallRepository() throws NotFoundException, UserException {
        // Prepare
        when(_userRepository.readUser(8)).thenReturn(null);

        int userId = 8;
        boolean expected = false;

        // Call
        boolean result = _testee.isActive(userId);

        // Validate
        Mockito.verify(_userRepository, times(1)).readUser(userId);
        Assert.assertEquals(result, expected);
    }
}
