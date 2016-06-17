package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class BusinessTripInformationViewModel extends BasicViewModel {

    public BusinessTripInformationViewModel(int notificationId, String message, String sender, String date, TimeTracking timeTracking) {

        super(notificationId, message, sender, date, timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.businesstripp");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.businesstrip");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }
}
