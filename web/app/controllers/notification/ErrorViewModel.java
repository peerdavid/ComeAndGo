package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 5/2/16.
 */
public class ErrorViewModel extends BasicViewModel {


    public ErrorViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, "", timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.error");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.error");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

}
