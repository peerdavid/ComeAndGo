package infrastructure;

import business.notification.NotificationException;
import com.avaje.ebean.Ebean;
import models.Notification;
import models.User;

import java.util.Collections;
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
    public Notification readNotification(int notificationId) throws NotificationException {
        Notification notification =
                Ebean.find(Notification.class)
                .where().eq("id", notificationId)
                .findUnique();

        if(notification != null) {
            return notification;
        }
        // We should never return null
        throw new NotificationException("exceptions.notification.could_not_find_notification");
    }

    @Override
    public void deleteNotification(Notification toDelete) {
        Ebean.delete(Notification.class, toDelete);
    }

    @Override
    public void updateNotification(Notification toUpdate) {
        Ebean.update(toUpdate);
    }

    @Override
    public List<Notification> readUnseenNotifications(User user) throws NotificationException {
        List<Notification> result =
                Ebean.find(Notification.class)
                .where().eq("_receiver_id", user.getId())   // filter notifications for user
                .where().eq("read", false)              // filter for only unread notes
                .orderBy("created desc")                    // the newer the higher in the list
                .findList();
        if(result != null) {
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public List<Notification> readSeenNotifications(User user, int amount) throws NotificationException {
        List<Notification> result =
                Ebean.find(Notification.class)
                        .where().eq("_receiver_id", user.getId())
                        .where().eq("read", true)
                        .orderBy("created desc")
                        .setMaxRows(amount)
                        .findList();
        if(result != null) {
            return result;
        }
        // We should never return null
        throw new NotificationException("exceptions.notification.could_not_find_notification");
    }

    @Override
    public List<Notification> readSentNotifications(User user, int amount) throws NotificationException {
        List<Notification> result =
            Ebean.find(Notification.class)
            .where().eq("_sender_id", user.getId())
            .orderBy("created desc")
            .setMaxRows(amount)
            .findList();

        if(result != null) {
            return result;
        }
        // We should never return null
        throw new NotificationException("exceptions.notification.could_not_find_notification");
    }


    @Override
    public void markAsRead(Notification read) throws NotificationException {
        read.setRead(true);
        updateNotification(read);
    }

    @Override
    public int readNumberOfUnseenNotifications(User user) throws NotificationException {
        return Ebean.find(Notification.class)
            .where().eq("_receiver_id", user.getId())
            .where().eq("read", false)
            .findRowCount();
    }
}
