package business.notification;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.UserException;
import com.google.inject.Inject;
import infrastructure.NotificationRepository;
import models.Notification;
import models.User;
import utils.aop.NoLogging;
import utils.aop.NoTransaction;

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


    @Override
    public Notification readNotification(int notificationId) throws NotificationException {
        return _notificationRepository.readNotification(notificationId);
    }


    public List<Notification> readUnseenNotifications(int userId) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.readUnseenNotifications(user);
    }


    @Override
    public void updateNotificationAsRead(int notificationId) throws NotificationException {
        Notification notificationToChange = _notificationRepository.readNotification(notificationId);
        _notificationRepository.markAsRead(notificationToChange);
    }


    @Override
    @NoLogging
    @NoTransaction
    public int readNumberOfUnseenNotifications(int userId) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.readNumberOfUnseenNotifications(user);
    }


    @Override
    public List<Notification> readSeenNotifications(int userId, int amount) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }


        return _notificationRepository.readSeenNotifications(user, amount);
    }


    @Override
    public List<Notification> readSentNotifications(int userId, int amount) throws NotificationException {
        User user;
        try {
            user = _userManagement.readUser(userId);
        } catch (UserException e) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.readSentNotifications(user, amount);
    }

}
