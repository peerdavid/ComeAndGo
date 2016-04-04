package business.notification;

import business.UseCases;
import model.Notification;

/**
 * Created by david on 22.03.16.
 */
public interface NotificationReader extends UseCases {
    Notification getNotifictionForUser(int userId);
}
