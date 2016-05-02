package controllers;

import business.notification.NotificationReader;
import business.timetracking.TimeTracking;
import com.google.inject.Inject;
import controllers.notification.NotificationViewModel;
import controllers.notification.NotificationViewModelFactory;
import models.Notification;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.mvc.Result;

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


        Notification temp = _notifReader.readNotification(notificationId);

        NotificationViewModelFactory
                .createNotificationViewModel(temp).accept(id);

        _notifReader.updateNotificationAsRead(notificationId);

        return redirect(routes.NotificationController.index());
    }

    @RequiresAuthentication(clientName = "default")
    public Result rejectNotification(int notificationId) throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());


        NotificationViewModelFactory
                .createNotificationViewModel(_notifReader.readNotification(notificationId)).reject(id);

        _notifReader.updateNotificationAsRead(notificationId);

        return redirect(routes.NotificationController.index());
    }


    @RequiresAuthentication(clientName = "default")
    public Result readNumberNewNotifications() throws Exception {
        CommonProfile profile = getUserProfile();
        int id = Integer.parseInt(profile.getId());

        int num = _notifReader.readNumberOfUnseenNotifications(id);
        return ok(String.valueOf(num));
    }
}
