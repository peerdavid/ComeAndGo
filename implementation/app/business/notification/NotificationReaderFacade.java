package business.notification;

import business.NotificationException;
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

        return _notificationRepository.getUnreadNotificationsForUser(user);
    }

    @Override
    public void setNotificationAsRead(int notificationId) throws NotificationException {
        Notification notificationToChange = _notificationRepository.readNotificationById(notificationId);
        _notificationRepository.markAsRead(notificationToChange);
    }

    @Override
    public int getNumberOfUnreadNotifications(int userId) throws NotificationException {
        User user = _userRepository.readUser(userId);
        return _notificationRepository.getNumberOfUnreadNotifications(user);
    }

    @Override
    public List<Notification> getReadNotificationsForUser(int userId, int amount) throws NotificationException {
        User user = _userRepository.readUser(userId);

        if (user == null) {
            throw new NotificationException("exceptions.usermanagement.no_such_user");
        }

        return _notificationRepository.getReadNotificationsForUser(user, amount);
    }

    @Override
    public List<Notification> getSentNotifications(int userId, int amount) throws NotificationException {
        User user = _userRepository.readUser(userId);
        return _notificationRepository.getSentNotifications(user, amount);
    }

    @Override
    public void accept(int notificationId) throws NotificationException {
        Notification actual = _notificationRepository.readNotificationById(notificationId);
        _notificationRepository.markAsAccepted(actual);

        // inform user about the acceptation
        Notification replyToUser =
            new Notification(getAcceptOppositeNotificationType(actual.getType()),
            actual.getReceiver(), actual.getSender(), actual.getRequestedStartDate(),
                actual.getRequestedEndDate());
        _notificationRepository.createNotification(replyToUser);
    }

    @Override
    public void reject(int notificationId) throws NotificationException {
        Notification actual = _notificationRepository.readNotificationById(notificationId);
        _notificationRepository.markAsRejected(actual);

        // inform user about the reject
        Notification replyToUser =
            new Notification(getRejectOppositeNotificationType(actual.getType()),
                actual.getReceiver(), actual.getSender(), actual.getRequestedStartDate(),
            actual.getRequestedEndDate());
        _notificationRepository.createNotification(replyToUser);
    }

    private NotificationType getAcceptOppositeNotificationType(NotificationType type) {
        switch(type) {
            case HOLIDAY_REQUEST:
                return NotificationType.HOLIDAY_ACCEPT;
            case HOLIDAY_PAYOUT_REQUEST:
                return NotificationType.HOLIDAY_PAYOUT_ACCEPT;
            case SPECIAL_HOLIDAY_REQUEST:
                // TODO: INSERT OTHER RETURN HERE
                return NotificationType.INFORMATION;
            case OVERTIME_PAYOUT_REQUEST:
                return NotificationType.OVERTIME_PAYOUT_ACCEPT;
            default:
                return NotificationType.INFORMATION;
        }
    }

    private NotificationType getRejectOppositeNotificationType(NotificationType type) {
        switch(type) {
            case HOLIDAY_REQUEST:
                return NotificationType.HOLIDAY_REJECT;
            case HOLIDAY_PAYOUT_REQUEST:
                return NotificationType.HOLIDAY_PAYOUT_REJECT;
            case SPECIAL_HOLIDAY_REQUEST:
                // TODO: INSERT OTHER RETURN HERE
                return NotificationType.INFORMATION;
            case OVERTIME_PAYOUT_REQUEST:
                return NotificationType.OVERTIME_PAYOUT_REJECT;
            default:
                return NotificationType.INFORMATION;
        }
    }
}
