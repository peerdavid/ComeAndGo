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

    void deleteNotification(Notification toDelete);

    void updateNotification(Notification toUpdate);

    void markAsAccepted(Notification accept) throws NotificationException;

    void markAsRejected(Notification reject) throws NotificationException;

    void markAsRead(Notification read) throws NotificationException;

    Notification readNotificationById(int notificationId) throws NotificationException;

    int getNumberOfUnreadNotifications(User user) throws NotificationException;

    List<Notification> getUnreadNotificationsForUser(User user) throws NotificationException;

    List<Notification> getReadNotificationsForUser(User user, int amount) throws NotificationException;

    List<Notification> getSentNotifications(User user, int amount) throws NotificationException;
}
