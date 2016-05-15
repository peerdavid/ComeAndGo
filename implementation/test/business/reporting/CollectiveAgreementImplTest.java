package business.reporting;


import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import business.usermanagement.SecurityRole;
import models.ReportEntry;
import models.TimeOff;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static org.mockito.Mockito.mock;

/**
 * Created by paz on 09.05.16.
 */
public class CollectiveAgreementImplTest {

    User _testUser;
    CollectiveAgreement _testee;
    List<TimeOff> _holiday;

    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 8.0);
        _testUser.setEntryDate(new DateTime(2016, 01, 10, 0, 0));
        _testUser.setHolidays(25);
        _testee = new CollectiveAgreementImpl();

        TimeOff holiday21days = new TimeOff(_testUser, new DateTime(2016, 2, 1, 0, 0), new DateTime(2016, 2, 20, 0, 0), TimeOffType.HOLIDAY, RequestState.REQUEST_ACCEPTED, "");
        _holiday = new ArrayList<>();
        _holiday.add(holiday21days);
    }

    @Test
    public void createUserReport_WithNoTimeTracksAndTimeOffs_ShouldSucceed() throws Exception {

        ReportEntry actual = _testee.createUserReport(_testUser, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        Assert.assertEquals(0, actual.getNumOfSickDays());
        Assert.assertEquals(25, actual.getNumOfUnusedHolidays());
        Assert.assertEquals(0, actual.getNumOfUsedHolidays());
        Assert.assertEquals(0, actual.getBreakMinutes());
        Assert.assertEquals(0, actual.getWorkMinutesIs());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now());
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());
    }

    @Test
    public void createUserReport_WithHoliday_ShouldSucceed() throws Exception {
        ReportEntry actual = _testee.createUserReport(_testUser, Collections.EMPTY_LIST, _holiday, Collections.EMPTY_LIST);

        Assert.assertEquals(0, actual.getNumOfSickDays());
        Assert.assertEquals(10, actual.getNumOfUnusedHolidays());
        Assert.assertEquals(15, actual.getNumOfUsedHolidays());
        Assert.assertEquals(0, actual.getBreakMinutes());
        Assert.assertEquals(0, actual.getWorkMinutesIs());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * (
                DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now())
                - DateTimeUtils.getWorkdaysOfTimeInterval(_holiday.get(0).getFrom(), _holiday.get(0).getTo()));
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());
    }

}
