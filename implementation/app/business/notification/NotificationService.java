package business.notification;

import com.google.inject.Inject;
import infrastructure.NotificationRepository;
import infrastructure.InternalUserManagement;
import models.Notification;

/**
 * Created by david on 22.03.16.
 */
class NotificationService implements NotificationSender {

    private final NotificationRepository _notificationRepository;
    private final InternalUserManagement _userRepository;

    @Inject
    public NotificationService(NotificationRepository notificationRepository, InternalUserManagement userRepository) {
        _notificationRepository = notificationRepository;
        _userRepository = userRepository;
    }

    @Override
    public void sendNotification(Notification notification) throws NotificationException {
        _notificationRepository.createNotification(notification);
    }
}
