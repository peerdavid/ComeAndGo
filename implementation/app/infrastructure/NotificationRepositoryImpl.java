package infrastructure;

import models.Notification;

import java.util.List;

/**
 * Created by paz on 19.04.16.
 */
public class NotificationRepositoryImpl implements NotificationRepository {
    @Override
    public void createNotification(Notification notification) {

    }

    @Override
    public Notification readNotification(int notificationId) {
        return null;
    }

    @Override
    public void deleteNotification(int notificationId) {

    }

    @Override
    public void updateNotification(int notificationId) {

    }

    @Override
    public List<Notification> getAllUnreadNotificationsForUser(String username) {
        return null;
    }
}
