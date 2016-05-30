package infrastructure;

import business.notification.NotificationException;
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

    void markAsRead(Notification read) throws NotificationException;

    Notification readNotification(int notificationId) throws NotificationException;

    int readNumberOfUnseenNotifications(User user) throws NotificationException;

    List<Notification> readUnseenNotifications(User user) throws NotificationException;

    List<Notification> readSeenNotifications(User user, int amount) throws NotificationException;

    List<Notification> readSentNotifications(User user, int amount) throws NotificationException;

    List<Notification> readSentNotifications(User user) throws NotificationException;
}
