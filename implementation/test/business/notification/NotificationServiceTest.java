package business.notification;

import business.UserException;
import business.usermanagement.UserService;
import infrastructure.NotificationRepository;
import infrastructure.UserRepository;
import models.Notification;
import models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by paz on 19.04.16.
 */
public class NotificationServiceTest {


    UserRepository _userRepository;
    NotificationRepository _notificationRepository;
    NotificationService _testee;
    Notification _testNotification;


    @Before
    public void SetUp() throws Exception {
        _userRepository = mock(UserRepository.class);
        _notificationRepository = mock(NotificationRepository.class);
        _testee = new NotificationService(_notificationRepository, _userRepository);
        _testNotification = new Notification(NotificationType.INFORMATION, "test", "admin", "admin");

    }

    @Test(expected = UserException.class)
    public void sendNotification_ToNonExistingUser_ShouldFail() throws UserException {
        // Prepare
        String notExistingUsername = "abcd";
        when(_userRepository.readUser(notExistingUsername)).thenReturn(null);


        _testNotification.setToUser(notExistingUsername);
        _testee.sendNotification(_testNotification);

    }

    @Test(expected = UserException.class)
    public void sendNotification_FromNonExistingUser_ShouldFail() throws UserException {
        // Prepare
        String notExistingUsername = "abcd";
        when(_userRepository.readUser(notExistingUsername)).thenReturn(null);


        _testNotification.setFromUser(notExistingUsername);
        _testee.sendNotification(_testNotification);
    }

    @Test(expected = UserException.class)
    public void sendNotification_WithTooLongMessage_ShouldFail() throws UserException {

        _testNotification.setMessage("mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$" +
                "mmmmmmmmmmmmmmmmmmm454353/%&§&$/§$§$§$$§$$§$");
        _testee.sendNotification(_testNotification);
    }
}
