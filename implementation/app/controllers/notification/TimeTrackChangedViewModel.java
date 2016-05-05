package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class TimeTrackChangedViewModel extends BasicViewModel {

    public TimeTrackChangedViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, "", timeTracking);

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.changedtimetrack");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.timetrack");
    }

    @Override
    public boolean isRejectable() {
        return false;
    }
}
