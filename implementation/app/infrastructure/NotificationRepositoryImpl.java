package infrastructure;

import business.NotificationException;
import com.avaje.ebean.Ebean;
import models.Notification;
import models.User;

import java.util.List;

/**
 * Created by paz on 19.04.16.
 */
public class NotificationRepositoryImpl implements NotificationRepository {
    @Override
    public void createNotification(Notification notification) {
        Ebean.save(notification);
    }

    @Override
    public Notification readNotificationById(int notificationId) throws NotificationException {
        Notification notification =
                Ebean.find(Notification.class)
                .where().eq("id", notificationId)
                .findUnique();

        if(notification != null) {
            return notification;
        }
        throw new NotificationException("no notification with that id found");
    }

    @Override
    public void deleteNotification(Notification toDelete) {
        toDelete.setVisibleToUser(false);
        Ebean.update(toDelete);
    }

    @Override
    public void updateNotification(Notification toUpdate) {
        Ebean.update(toUpdate);
    }

    @Override
    public List<Notification> getAllUnreadNotificationsToUser(User user) throws NotificationException {
        List<Notification> result =
                Ebean.find(Notification.class)
                .where().eq("to_user", user.getId())
                .where().eq("vis_to_user", true)
                .where().eq("read", false).findList();
        if(result != null) {
            return result;
        }
        throw new NotificationException("no unread notifications here");
    }

    @Override
    public List<Notification> getAllReadNotificationsForUser(User user) throws NotificationException {
        List<Notification> result =
                Ebean.find(Notification.class)
                        .where().eq("to_user", user.getId())
                        .where().eq("vis_to_user", true)
                        .where().eq("read", true).findList();
        if(result != null) {
            return result;
        }
        throw new NotificationException("no unread notifications here");
    }
}
