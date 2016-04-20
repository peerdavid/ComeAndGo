package business.notification;

import business.UserException;
import com.google.inject.Inject;
import infrastructure.NotificationRepository;
import infrastructure.UserRepository;
import models.Notification;

/**
 * Created by david on 22.03.16.
 */
class NotificationService implements NotificationSender {

    private final NotificationRepository _notificationRepository;
    private final UserRepository _userRepository;

    @Inject
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        _notificationRepository = notificationRepository;
        _userRepository = userRepository;
    }

    @Override
    public void sendNotification(Notification notification) throws UserException {

        if (notification.getFromUser() == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }
        if (notification.getToUser() == null) {
            throw new UserException("exceptions.usermanagement.no_such_user");
        }

        _notificationRepository.createNotification(notification);

    }
}
