package business.notification;

import business.usermanagement.InternalUserManagement;
import com.google.inject.Inject;
import infrastructure.NotificationRepository;
import models.Notification;

/**
 * Created by david on 22.03.16.
 */
class NotificationService implements NotificationSender {

    private final NotificationRepository _notificationRepository;

    @Inject
    public NotificationService(NotificationRepository notificationRepository) {
        _notificationRepository = notificationRepository;
    }

    @Override
    public void sendNotification(Notification notification) throws NotificationException {
        _notificationRepository.createNotification(notification);
    }
}
