package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class HolidayPayoutAcceptViewModel extends BasicViewModel {

    public HolidayPayoutAcceptViewModel(int notificationId, String message, String additionalInfo, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, additionalInfo, timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holidaypayout.accept");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holidaypayout");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }

}
