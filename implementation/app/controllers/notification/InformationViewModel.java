package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 5/2/16.
 */
public class InformationViewModel extends BasicViewModel {

    public InformationViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {
        super(notificationId, message, sender, timeTracking);
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.information");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.information");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept(int userId) throws Exception {

    }
}
