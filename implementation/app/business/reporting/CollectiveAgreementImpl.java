package business.reporting;

import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import models.*;
import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAgreementImpl implements CollectiveAgreement {


    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {

        long workMinutesWithoutBreak = timeTracks.stream().mapToLong(t -> (t.getTo().getMillis() - t.getFrom().getMillis()) / (1000 * 60)).sum();
        long breakMinutes = timeTracks.stream().mapToLong(
                t -> t.getBreaks().stream().mapToLong(
                        b -> (b.getTo().getMillis() - b.getFrom().getMillis()) / (1000 * 60)
                ).sum()
        ).sum();

        long holidayMinutes = 0;
        for (TimeOff t : timeOffs) {
            if (    (t.getType() == TimeOffType.HOLIDAY) &&
                    (t.getState() == RequestState.REQUEST_ACCEPTED) &&
                    (t.getFrom().isBeforeNow())) {
                holidayMinutes += TimeUnit.MILLISECONDS.toMinutes(t.getTo().getMillis() - t.getFrom().getMillis());
            }
        }


        long workMinutesShould = (long) (1000 * user.getHoursPerDay());

        return new ReportEntry(user, 1,2,3,4, workMinutesShould, (workMinutesWithoutBreak - breakMinutes), breakMinutes);
    }


    @Override
    public List<Notification> createForbiddenWorkTimeNotifications(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {
        // If user worked > 8h, send a notification to the boss
        return null;
    }
}
