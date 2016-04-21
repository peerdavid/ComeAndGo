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

    public List<Notification> getUnreadNotificationsForUser(int userId) throws NotificationException {
        User user = _userRepository.readUser(userId);

        if (user == null) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getAllUnreadNotificationsToUser(user);
    }

    public List<Notification> getReadNotificationsForUser(int userId) throws NotificationException {
        User user = _userRepository.readUser(userId);

        if (user == null) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getAllReadNotificationsForUser(user);
    }

    @Override
    public void setNotificationAsRead(int NotificationId) throws NotificationException {
        Notification notificationToChange = _notificationRepository.readNotificationById(NotificationId);

        notificationToChange.setRead(true);
        _notificationRepository.updateNotification(notificationToChange);

    }

    @Override
    public int getNumberOfUnreadNotifications(int userId) throws NotificationException {
        return 0;
    }

    @Override
    public List<Notification> getReadNotificationsForUser(int userId, int amount) throws NotificationException {
        return null;
    }

    @Override
    public List<Notification> getSentNotifications(int userId, int amount) throws NotificationException {
        return null;
    }

    @Override
    public void accept(int notificationId) throws NotificationException {

    }

    @Override
    public void reject(int notificationId) throws NotificationException {

    }
}
