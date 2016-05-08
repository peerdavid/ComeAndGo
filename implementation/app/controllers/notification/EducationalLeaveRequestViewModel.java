package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class EducationalLeaveRequestViewModel extends BasicViewModel {

    private int _timeOffId;

    public EducationalLeaveRequestViewModel(int notificationId, int timeOffId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, "", timeTracking);

        _timeOffId = timeOffId;

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.educationalleave");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.educationalleave");
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept(int userId) throws Exception {
        _timeTracking.acceptEducationalLeave(_timeOffId, userId);
    }

    @Override
    public void reject(int userId) throws Exception {
        _timeTracking.rejectEducationalLeave(_timeOffId, userId);
    }
}
