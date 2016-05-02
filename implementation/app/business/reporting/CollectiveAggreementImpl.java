package business.reporting;

import models.*;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAggreementImpl implements CollectiveAggreement {


    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {

        long workMinutesWithoutBreak = timeTracks.stream().mapToLong(t -> (t.getTo().getMillis() - t.getFrom().getMillis()) / (1000 * 60)).sum();
        long breakMinutes = timeTracks.stream().mapToLong(
                t -> t.getBreaks().stream().mapToLong(
                        b -> (b.getTo().getMillis() - b.getFrom().getMillis()) / (1000 * 60)
                ).sum()
        ).sum();

        long workMinutesShould = (long) (1000 * user.getHoursPerDay());

        return new ReportEntry(user, 1,2,3,4, workMinutesShould, (workMinutesWithoutBreak - breakMinutes), breakMinutes);
    }


    @Override
    public List<Notification> createForbiddenWorkTimeNotifications(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {
        // If user worked > 8h, send a notification to the boss
        return null;
    }
}
