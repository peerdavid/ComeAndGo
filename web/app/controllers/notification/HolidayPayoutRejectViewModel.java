package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class HolidayPayoutRejectViewModel extends BasicViewModel {

    public HolidayPayoutRejectViewModel(int notificationId, String message, String additionalInfo, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, additionalInfo, timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holidaypayout.reject");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holidaypayout");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

    @Override
    public void accept(int userId) throws Exception {

    }
}
