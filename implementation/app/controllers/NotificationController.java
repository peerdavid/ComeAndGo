package controllers;

import business.notification.NotificationReader;
import business.notification.NotificationType;
import business.timetracking.TimeTracking;
import business.usermanagement.SecurityRole;
import com.google.inject.Inject;
import controllers.notification.*;
import models.Notification;
import models.User;
import net.sf.ehcache.search.expression.Not;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.i18n.Messages;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonhard on 20.04.2016.
 */
public class NotificationController extends UserProfileController {

    private TimeTracking _timeTracking;
    private NotificationReader _notifReader;

    @Inject
    public NotificationController(TimeTracking timeTracking, NotificationReader notifReader) {
        _timeTracking = timeTracking;
        _notifReader = notifReader;
    }

    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());


        List<NotificationViewModel> readNotifications = NotificationViewModelFactory
                .createNotificationViewModelList(_notifReader.readSeenNotifications(id, 10));
        List<NotificationViewModel> unreadNotifications = NotificationViewModelFactory
                .createNotificationViewModelList(_notifReader.readUnseenNotifications(id));
        List<NotificationViewModel> sentNotifications = NotificationViewModelFactory
                .createNotificationViewModelList(_notifReader.readSentNotifications(id, 10));


        return ok(views.html.notification.render(profile, unreadNotifications, readNotifications, sentNotifications));
    }

    @RequiresAuthentication(clientName = "default")
    public Result acceptNotification(int notificationId) throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());

        NotificationViewModelFactory
                .createNotificationViewModel(_notifReader.readNotification(notificationId)).accept(id);

        return redirect(routes.NotificationController.index());
    }

    @RequiresAuthentication(clientName = "default")
    public Result rejectNotification(int notificationId) throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());

        NotificationViewModelFactory
                .createNotificationViewModel(_notifReader.readNotification(notificationId)).reject(id);



        return redirect(routes.NotificationController.index());
    }
}
