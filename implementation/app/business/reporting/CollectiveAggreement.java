package business.reporting;

import models.*;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface CollectiveAggreement {

    ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts);

    List<Notification> createForbiddenWorkTimeNotifications(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts);
}
