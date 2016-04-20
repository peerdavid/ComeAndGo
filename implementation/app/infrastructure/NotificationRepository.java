package infrastructure;

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

    List<Notification> getAllUnreadNotificationsToUser(User user) throws NotificationException;

    List<Notification> getAllReadNotificationsForUser(User user) throws NotificationException;
}
