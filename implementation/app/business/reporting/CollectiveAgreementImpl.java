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

    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {

        long workMinutesWithoutBreak = timeTracks.stream().mapToLong(t -> (t.getTo().getMillis() - t.getFrom().getMillis()) / (1000 * 60)).sum();
        long breakMinutes = timeTracks.stream().mapToLong(
                t -> t.getBreaks().stream().mapToLong(
                        b -> (b.getTo().getMillis() - b.getFrom().getMillis()) / (1000 * 60)
                ).sum()
        ).sum();


        int usedHolidayDays = 0;
        int acceptedHolidayDays = 0;
        int sickDays = 0;
        for (TimeOff t : timeOffs) {
            if (    (t.getType() == TimeOffType.HOLIDAY) &&
                    (t.getState() == RequestState.REQUEST_ACCEPTED)) {

                if (t.getFrom().isBeforeNow()) {
                    usedHolidayDays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
                }

                acceptedHolidayDays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }

            if (t.getType() == TimeOffType.SICK_LEAVE) {
                sickDays += getWorkdaysOfTimeInterval(t.getFrom(), t.getTo());
            }
        }


        long workMinutesShould = (long) ((getWorkdaysOfThisYearUptoNow(user.getEntryDate()) * 60 * user.getHoursPerDay())
                                    - usedHolidayDays * user.getHoursPerDay() * 60);

        return new ReportEntry(
                user,
                user.getHoursPerDay(),
                acceptedHolidayDays,
                user.getHolidays() - acceptedHolidayDays,
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

    private static int getWorkdaysOfThisYearUptoNow(DateTime entryDate) {
        DateTime january1st = new DateTime(DateTime.now().getYear(), 1, 1, 0, 0);

        // If user joined company this year
        if (entryDate.getYear() == DateTime.now().getYear()) {
            return getWorkdaysOfTimeInterval(january1st, DateTime.now()) - entryDate.getDayOfYear();
        }
        return getWorkdaysOfTimeInterval(january1st, DateTime.now());
    }

    private static int getWorkdaysOfThisYear() {
        DateTime january1st = new DateTime(DateTime.now().getYear(), 1, 1, 0, 0);
        DateTime december31th = new DateTime(DateTime.now().getYear(), 12, 31, 23, 59);

        return getWorkdaysOfTimeInterval(january1st, december31th);
    }

    // This function only counts real work days, not the weekend
    // ToDo: Consider Bank holiday too!
    private static int getWorkdaysOfTimeInterval(DateTime from, DateTime to) {
        int workdays = 0;
        for (int i = 0; i < to.getDayOfYear() - from.getDayOfYear(); i++) {
            if (from.plusDays(i).getDayOfWeek() != 6 && from.plusDays(i).getDayOfWeek() != 7) {
                workdays++;
            }
        }
        return workdays;
    }
}
