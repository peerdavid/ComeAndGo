package controllers.notification;

import business.timetracking.TimeTracking;
import models.TimeTrack;

/**
 * Created by csaq5996 on 4/25/16.
 */
public class HolidayRequestViewModel implements NotificationViewModel {

    private TimeTracking _timeTracking;

    public HolidayRequestViewModel(TimeTracking timeTracking){

        _timeTracking = timeTracking;
    }

    @Override
    public int getTimeOffId() {
        return 0;
    }

    @Override
    public int getNotificationId() {
        return 0;
    }

    @Override
    public boolean hasRead() {
        return true;
    }

    @Override
    public String getIcon() {
        return "sun";
    }

    @Override
    public String getHeader() {
        return "HOLIDAY";
    }

    @Override
    public String getMessage() {
        return "das ist ein super cooler urlaub...";
    }

    @Override
    public boolean isRejectable() {
        return true;
    }

    @Override
    public void accept() {
        //_timeTracking.acceptHolidayRequest();
    }
}
