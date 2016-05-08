package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class EducationalLeaveAcceptViewModel extends BasicViewModel {

    public EducationalLeaveAcceptViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, "", timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.educationalleave.accept");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.educationalleave");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }
}