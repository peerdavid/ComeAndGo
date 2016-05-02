package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class SickLeaveViewModel extends BasicViewModel {

    public SickLeaveViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.sickleave");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.sickleave");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    public void accept(int userId) {

    }
}
