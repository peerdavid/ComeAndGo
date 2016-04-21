package business.notification;

import business.NotificationException;
import business.UserException;
import com.google.inject.Inject;
import infrastructure.NotificationRepository;
import infrastructure.UserRepository;
import models.Notification;
import models.User;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
public class NotificationReaderFacade implements NotificationReader {

    private final NotificationRepository _notificationRepository;
    private final UserRepository _userRepository;

    @Inject
    public NotificationReaderFacade(NotificationRepository notificationRepository, UserRepository userRepository) {
        _notificationRepository = notificationRepository;
        _userRepository = userRepository;
    }

    public List<Notification> getUnreadNotifictionsForUser(String userName) throws NotificationException {
        User user = _userRepository.readUser(userName);

        if (user == null) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getAllUnreadNotificationsToUser(user);
    }

    @Override
    public void setNotificationAsRead(int NotificationId) throws NotificationException {

    }
}
