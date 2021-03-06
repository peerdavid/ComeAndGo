package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

/**
 * Created by Leonhard on 26.04.2016.
 */
public abstract class BasicViewModel implements NotificationViewModel {

    private int _notificationId;

    private String _message;
    private String _sender;
    private String _additionalInfo;

    protected TimeTracking _timeTracking;

    public BasicViewModel(int notificationId, String message, String sender, String additionalInfo, TimeTracking timeTracking) {
        _notificationId = notificationId;

        _message = message;
        _sender = sender;
        _additionalInfo = additionalInfo;

        _timeTracking = timeTracking;
    }

    public int getNotificationId(){
        return _notificationId;
    }

    public String getMessage(){
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    public String getAdditionalInformation(){
        return _additionalInfo;
    }

    public String getSender() {
        return _sender;
    }

    public void accept(int userid) throws Exception{}

    public void reject(int userId) throws Exception {
        throw new Exception(Messages.get("exceptions.notifications.invalid_action"));
    }

}
