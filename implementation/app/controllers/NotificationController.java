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

    private NotificationViewModelFactory _notificationFactory;
    private TimeTracking _timeTracking;
    private NotificationReader _notifReader;

    @Inject
    public NotificationController(TimeTracking timeTracking, NotificationReader notifReader,
                                  NotificationViewModelFactory factory) {
        _timeTracking = timeTracking;
        _notifReader = notifReader;
        _notificationFactory = factory;
    }

    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());


        List<NotificationViewModel> readNotifications = _notificationFactory
                .createNotificationViewModelList(_notifReader.readSeenNotifications(id, 10));
        List<NotificationViewModel> unreadNotifications = _notificationFactory
                .createNotificationViewModelList(_notifReader.readUnseenNotifications(id));
        List<NotificationViewModel> sentNotifications = _notificationFactory
                .createNotificationViewModelList(_notifReader.readSentNotifications(id, 10));


        return ok(views.html.notification.render(profile, unreadNotifications, readNotifications, sentNotifications));
    }

    @RequiresAuthentication(clientName = "default")
    public Result acceptNotification(int notificationId) throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());


        Notification temp = _notifReader.readNotification(notificationId);

        _notificationFactory.createNotificationViewModel(temp).accept(id);

        _notifReader.updateNotificationAsRead(notificationId);

        return redirect(routes.NotificationController.index());
    }

    @RequiresAuthentication(clientName = "default")
    public Result rejectNotification(int notificationId) throws Exception {
        CommonProfile profile = getUserProfile();

        int id = Integer.parseInt(profile.getId());


        _notificationFactory.createNotificationViewModel(_notifReader.readNotification(notificationId)).reject(id);

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
