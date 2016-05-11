package business.reporting;

import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import models.*;
import org.joda.time.DateTime;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAgreementImpl implements CollectiveAgreement {

    // ToDo: How to update this amount?
    private static final int WORKDAYS_OF_YEAR = 249;

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
        int sickDays = 0;
        for (TimeOff t : timeOffs) {
            if (    (t.getType() == TimeOffType.HOLIDAY) &&
                    (t.getState() == RequestState.REQUEST_ACCEPTED)) {

                if (t.getFrom().isBeforeNow()) {
                    usedHolidays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
                }

                acceptedHolidays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }

            if (t.getType() == TimeOffType.SICK_LEAVE) {
                sickDays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }
        }


        long workMinutesShould = (long) ((getWorkdaysOfThisYearUptoNow(user.getEntryDate()) * 24 * 60 * user.getHoursPerDay() * 60)
                                    - usedHolidays * user.getHoursPerDay() * 60);

        return new ReportEntry(
                user,
                user.getHoursPerDay(),
                acceptedHolidays,
                user.getHolidays() - acceptedHolidays,
                sickDays,
                workMinutesShould,
                workMinutesWithoutBreak - breakMinutes,
                breakMinutes);
    }


    @Override
    public List<Notification> createForbiddenWorkTimeNotifications(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {
        // If user worked > 8h, send a notification to the boss
        return null;
    }

    private int getWorkdaysOfThisYearUptoNow(DateTime entryDate) {
        // If user joined company this year
        if (entryDate.getYear() == DateTime.now().getYear()) {
            return WORKDAYS_OF_YEAR - entryDate.getDayOfYear();
        }
        return WORKDAYS_OF_YEAR;
    }

    // This function only counts real work days, not the weekend
    private int getWorkdaysOfTimeInterval(DateTime from, DateTime to) {
        int workdays = 0;
        for (int i = 0; i < to.getDayOfYear() - from.getDayOfYear(); i++) {
            if (    from.plusDays(i).getDayOfWeek() != 6
                    && from.plusDays(i).getDayOfWeek() != 7) {
                workdays++;
            }
        }
        return workdays;
    }
}
