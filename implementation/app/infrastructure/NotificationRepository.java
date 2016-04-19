package infrastructure;

import models.Notification;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface NotificationRepository {
    void createNotification(Notification notification);

    Notification readNotification(int notificationId);

    void deleteNotification(int notificationId);

    void updateNotification(int notificationId);

    List<Notification> getAllUnreadNotificationsForUser(String username);

}
