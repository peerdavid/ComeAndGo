package business.notification;

import domain.Notification;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationSender {
    void sendNotification(Notification notification);
}
