package business.notification;

import business.usermanagement.UserNotFoundException;
import com.google.inject.Inject;
import infrastructure.NotificationRepository;
import infrastructure.InternalUserManagement;
import models.Notification;
import models.User;

import java.util.List;

/**
 * Created by david on 22.03.16.
 */
class NotificationReaderFacade implements NotificationReader {

    private final NotificationRepository _notificationRepository;
    private final InternalUserManagement _userManagement;

    @Inject
    public NotificationReaderFacade(NotificationRepository notificationRepository, InternalUserManagement userRepository) {
        _notificationRepository = notificationRepository;
        _userManagement = userRepository;
    }

    public List<Notification> getUnreadNotificationsForUser(int userId) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserNotFoundException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getUnreadNotificationsForUser(user);
    }

    @Override
    public void setNotificationAsRead(int notificationId) throws NotificationException {
        Notification notificationToChange = _notificationRepository.readNotificationById(notificationId);
        _notificationRepository.markAsRead(notificationToChange);
    }

    @Override
    public int getNumberOfUnreadNotifications(int userId) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserNotFoundException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getNumberOfUnreadNotifications(user);
    }

    @Override
    public List<Notification> getReadNotificationsForUser(int userId, int amount) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserNotFoundException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }


        return _notificationRepository.getReadNotificationsForUser(user, amount);
    }

    @Override
    public List<Notification> getSentNotifications(int userId, int amount) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserNotFoundException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getSentNotifications(user, amount);
    }

}
