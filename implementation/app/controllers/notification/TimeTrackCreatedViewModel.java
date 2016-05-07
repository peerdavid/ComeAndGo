package controllers.notification;

import business.timetracking.TimeTracking;
import play.i18n.Messages;

import javax.persistence.Basic;

/**
 * Created by stefan on 07.05.16.
 */
public class TimeTrackCreatedViewModel extends BasicViewModel {
   public TimeTrackCreatedViewModel(int notificationId, String message, String sender, TimeTracking timeTracking) {

      super(notificationId, message, sender, "", timeTracking);

   }

   @Override
   public String getIcon() {
      return Messages.get("notifications.icons.createdtimetrack");
   }

   @Override
   public String getHeader() {
      return Messages.get("notifications.timetrack.created");
   }

   @Override
   public boolean isRejectable() {
      return false;
   }
}
