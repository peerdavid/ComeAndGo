package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class HolidayPayoutRequestViewModel extends BasicViewModel {

    private int _payoutId;

    public HolidayPayoutRequestViewModel(int notificationId, int payoutId, String message, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, "", timeTracking);

        _payoutId = payoutId;

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holidaypayout");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holidaypayout");
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept(int userId) throws Exception {
        _timeTracking.acceptHolidayPayout(_payoutId, userId);
    }

    @Override
    public void reject(int userId) throws Exception {
        _timeTracking.rejectHolidayPayout(_payoutId, userId);
    }
}
