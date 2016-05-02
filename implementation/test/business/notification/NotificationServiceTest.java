package business.notification;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.SecurityRole;
import infrastructure.NotificationRepository;
import models.Notification;
import models.User;
import org.junit.Before;

import static org.mockito.Mockito.mock;

/**
 * Created by paz on 19.04.16.
 */
public class NotificationServiceTest {


    InternalUserManagement _userRepository;
    NotificationRepository _notificationRepository;
    NotificationService _testee;
    Notification _testNotification;
    User _toTestUser;
    User _fromTestUser;

    @Before
    public void SetUp() throws Exception {
        _userRepository = mock(InternalUserManagement.class);
        _notificationRepository = mock(NotificationRepository.class);
        _testee = new NotificationService(_notificationRepository, _userRepository);
        _toTestUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 1200);
        _fromTestUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 1200);
        _testNotification = new Notification(NotificationType.INFORMATION, "test", _fromTestUser, _toTestUser);

    }



}
