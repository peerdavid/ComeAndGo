package business.notification;

import business.NotificationException;
import models.Notification;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationSender {
    void sendNotification(Notification notification) throws NotificationException;
}
