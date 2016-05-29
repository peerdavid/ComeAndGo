package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 05.05.2016.
 */
public class OvertimePayoutRequestViewModel extends BasicViewModel {

    private int _payoutId;

    public OvertimePayoutRequestViewModel(int notificationId, int payoutId, String message, String additionalInfo, String sender, TimeTracking timeTracking) {

        super(notificationId, message, sender, additionalInfo, timeTracking);

        _payoutId = payoutId;

    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.overtimepayout");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.overtimepayout");
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept(int userId) throws Exception {
        _timeTracking.acceptOvertimePayout(_payoutId, userId);
    }

    @Override
    public void reject(int userId) throws Exception {
        _timeTracking.rejectOvertimePayout(_payoutId, userId);
    }
}
