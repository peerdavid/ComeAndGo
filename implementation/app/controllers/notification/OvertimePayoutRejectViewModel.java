package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class OvertimePayoutRejectViewModel extends BasicViewModel {

    public OvertimePayoutRejectViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, "", timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.overtimepayout.reject");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.overtimepayout");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept(int userId) throws Exception {

    }
}
