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


        int usedHolidays = 0;
        int acceptedHolidays = 0;
        for (TimeOff t : timeOffs) {
            if (    (t.getType() == TimeOffType.HOLIDAY) &&
                    (t.getState() == RequestState.REQUEST_ACCEPTED)) {

                if (t.getFrom().isBeforeNow()) {
                    usedHolidays += t.getTo().getDayOfYear() - t.getFrom().getDayOfYear();
                }

                acceptedHolidays += t.getTo().getDayOfYear() - t.getFrom().getDayOfYear();
            }
        }


        long workMinutesShould = (long) ((getWorkdaysOfThisYear() * 24 * 60 * user.getHoursPerDay() * 60) - usedHolidays * user.getHoursPerDay() * 60);

        return new ReportEntry(user, user.getHoursPerDay(), usedHolidays,3,4, workMinutesShould, (workMinutesWithoutBreak - breakMinutes), breakMinutes);
    }


    @Override
    public List<Notification> createForbiddenWorkTimeNotifications(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {
        // If user worked > 8h, send a notification to the boss
        return null;
    }

    @Override
    public int getWorkdaysOfThisYear() {
        return 249;
    }
}
