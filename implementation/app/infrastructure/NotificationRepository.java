package infrastructure;

import business.NotificationException;
import models.Notification;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface NotificationRepository {
    void createNotification(Notification notification);

    Notification readNotificationById(int notificationId) throws NotificationException;

    void deleteNotification(Notification toDelete);

    void updateNotification(Notification toUpdate);

    List<Notification> getUnreadNotificationsForUser(User user) throws NotificationException;

    List<Notification> getReadNotificationsForUser(User user, int amount) throws NotificationException;

    List<Notification> getSentNotifications(User user, int amount) throws NotificationException;
}
