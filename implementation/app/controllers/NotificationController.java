package controllers;

import business.notification.NotificationType;
import business.usermanagement.SecurityRole;
import com.google.common.io.ByteStreams;
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

    @RequiresAuthentication(clientName = "default")
    public Result index() throws Exception{
        CommonProfile profile = getUserProfile();

        // sample notifications for frontend testing

        User klaus = new User("klaus","password,", SecurityRole.ROLE_USER,"klaus","huber","huber@mail.com",true,"boss");
        User boss = new User("boss","password,", SecurityRole.ROLE_BOSS,"boss","huber","boss@mail.com",true,"klaus");

        List<Notification> readNotification = new ArrayList();
        List<Notification> unreadNotification = new ArrayList();

        /*
        readNotification.add(new Notification(NotificationType.INFORMATION,"i am a read message",klaus,boss));
        readNotification.add(new Notification(NotificationType.HOLIDAY_ACCEPT,"happy holidays",klaus,boss));
*/
        unreadNotification.add(new Notification(NotificationType.INFORMATION,"i am a unread message",boss,klaus));
        unreadNotification.add(new Notification(NotificationType.BUSINESS_TRIP_INFORMATION,"i am a going trip message",klaus,boss));

        return ok(views.html.notification.render(profile,unreadNotification,readNotification));
    }
}
