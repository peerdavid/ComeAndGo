package controllers.notification;

import business.timetracking.TimeTracking;
import models.TimeTrack;
import play.i18n.Messages;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class HolidayRequestViewModel extends BasicViewModel implements NotificationViewModel {

    private TimeTracking _timeTracking;

    private int notificationId;
    private int timeOffId;

    private String message;
    private String sender;

    private boolean read;


    public HolidayRequestViewModel(TimeTracking timeTracking, int notificationId, int timeOffId, String message, String sender, boolean read){

        super(notificationId,message,sender,read);

        this.timeOffId = timeOffId;

        _timeTracking = timeTracking;

    }

    @Override
    public int getTimeOffId() {
        return timeOffId;
    }

    @Override
    public String getIcon() {
        return Messages.get("notifications.icons.holidayrequest");
    }

    @Override
    public String getHeader() {
        return Messages.get("notifications.holiday");
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept() {
        this.read = true;
        //_timeTracking.acceptHolidayRequest();
    }

    public void reject() {
        this.read = true;
        //_timeTracking.rejectHolidayRequest();
    }
}
