package controllers;

import business.notification.NotificationReader;
import business.notification.NotificationType;
import business.usermanagement.SecurityRole;
import com.google.inject.Inject;
import models.Notification;
import models.User;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonhard on 20.04.2016.
 */
public class NotificationController extends UserProfileController {

    private NotificationReader _notifReader;

    @Inject
    public NotificationController(NotificationReader notifReader) {
        _notifReader = notifReader;
    }

    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception{
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());

        List<Notification> readNotifications = _notifReader.getReadNotificationsForUser(id,10);
        List<Notification> unreadNotifications = _notifReader.getUnreadNotificationsForUser(id);
        List<Notification> sentNotifications = _notifReader.getSentNotifications(id,10);

        return ok(views.html.notification.render(profile,unreadNotifications,readNotifications,sentNotifications));
    }

    @RequiresAuthentication(clientName = "default")
    public Result acceptNotification(int notificationId) throws Exception{


        //_notifReader.accept(notificationId);


        return redirect(routes.NotificationController.index());
    }

    @RequiresAuthentication(clientName = "default")
    public Result rejectNotification(int notificationId) throws Exception{


        //_notifReader.reject(notificationId);


        return redirect(routes.NotificationController.index());
    }
}
