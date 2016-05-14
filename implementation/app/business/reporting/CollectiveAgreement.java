package business.reporting;

import models.*;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
interface CollectiveAgreement {

    ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts, DateTime to);

    List<ForbiddenWorkTimeAlert> createForbiddenWorkTimeAlerts(ReportEntry entry);
}
