package business.reporting;

import models.*;

import java.util.List;

/**
 * Created by david on 02.05.16.
 */
class CollectiveAggreementImpl implements CollectiveAggreement {


    @Override
    public ReportEntry createUserReport(User user, List<TimeTrack> timeTracks, List<TimeOff> timeOffs, List<Payout> payouts) {
        return new ReportEntry(user, 1,2,3,4,5,6);
    }
}
