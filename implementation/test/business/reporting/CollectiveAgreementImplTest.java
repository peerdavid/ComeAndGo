package business.reporting;


import business.timetracking.RequestState;
import business.timetracking.TimeOffType;
import business.usermanagement.SecurityRole;
import models.*;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by paz on 09.05.16.
 */
public class CollectiveAgreementImplTest {

    User _testUser;
    CollectiveAgreement _testee;
    List<TimeOff> _timeoffs;
    List<TimeTrack> _timetracks;

    @Before
    public void SetUp() throws Exception {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, null, 8.0);
        _testUser.setEntryDate(new DateTime(2016, 1, 10, 0, 0));
        _testUser.setHolidays(25);
        _testee = new CollectiveAgreementImpl();
        _timeoffs = new ArrayList<>();
        _timetracks = new ArrayList<>();
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
        TimeOff holiday21days = new TimeOff(_testUser, new DateTime(2016, 2, 1, 0, 0), new DateTime(2016, 2, 20, 0, 0), TimeOffType.HOLIDAY, RequestState.REQUEST_ACCEPTED, "");
        _timeoffs.add(holiday21days);

        ReportEntry actual = _testee.createUserReport(_testUser, Collections.EMPTY_LIST, _timeoffs, Collections.EMPTY_LIST);

        Assert.assertEquals(10, actual.getNumOfUnusedHolidays());
        Assert.assertEquals(15, actual.getNumOfUsedHolidays());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * (
                DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now())
                - DateTimeUtils.getWorkdaysOfTimeInterval(_timeoffs.get(0).getFrom(), _timeoffs.get(0).getTo()));
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());
    }

    @Test
    public void createUserReport_With1DayWorkingWithoutBreaks_ShouldSucceed() throws Exception {
        DateTime date1 = new DateTime(2016, 1, 12, 7, 55);
        DateTime date2 = new DateTime(2016, 1, 12, 16, 57);

        TimeTrack work1dayWithoutBreaks = new TimeTrack(_testUser, date1, date2, Collections.EMPTY_LIST);
        _timetracks.add(work1dayWithoutBreaks);

        ReportEntry actual = _testee.createUserReport(_testUser, _timetracks, _timeoffs, Collections.EMPTY_LIST);

        Assert.assertEquals(0, actual.getBreakMinutes());

        int expectedWorkMinutesIs = (date2.getMinuteOfDay() - date1.getMinuteOfDay());
        Assert.assertEquals(expectedWorkMinutesIs, actual.getWorkMinutesIs());


    }

    @Test
    public void createUserReport_With1DayWorkingWith2Breaks_ShouldSucceed() throws Exception {
        DateTime date1 = new DateTime(2016, 1, 12, 7, 55);
        DateTime date2 = new DateTime(2016, 1, 12, 16, 57);

        List<Break> breaks = new ArrayList<>();
        breaks.add(new Break(new DateTime(2016, 1, 12, 9, 0), new DateTime(2016, 1, 12, 9, 15)));
        breaks.add(new Break(new DateTime(2016, 1, 12, 12, 0), new DateTime(2016, 1, 12, 12, 45)));

        TimeTrack work1dayWithoutBreaks = new TimeTrack(_testUser, date1, date2, breaks);
        _timetracks.add(work1dayWithoutBreaks);

        ReportEntry actual = _testee.createUserReport(_testUser, _timetracks, _timeoffs, Collections.EMPTY_LIST);

        Assert.assertEquals(60, actual.getBreakMinutes());

        int expectedWorkMinutesIs = date2.getMinuteOfDay() - date1.getMinuteOfDay() - 60;
        Assert.assertEquals(expectedWorkMinutesIs, actual.getWorkMinutesIs());

    }

    @Test
    public void createUserReport_With2DaysWorkingAnd1DaySpecialHoliday_ShouldSucceed() throws Exception {
        TimeOff specialHoliday = new TimeOff(_testUser, new DateTime(2016, 2, 1, 0, 0), new DateTime(2016, 2, 2, 0, 0), TimeOffType.SPECIAL_HOLIDAY, RequestState.REQUEST_ACCEPTED, "");
        _timeoffs.add(specialHoliday);

        DateTime date1 = new DateTime(2016, 1, 12, 7, 55);
        DateTime date2 = new DateTime(2016, 1, 12, 16, 57);

        TimeTrack work1dayWithoutBreaks = new TimeTrack(_testUser, date1, date2, Collections.EMPTY_LIST);
        _timetracks.add(work1dayWithoutBreaks);

        DateTime date3 = new DateTime(2016, 1, 13, 8, 1);
        DateTime date4 = new DateTime(2016, 1, 13, 17, 28);

        TimeTrack work1dayWithoutBreaks2 = new TimeTrack(_testUser, date3, date4, Collections.EMPTY_LIST);
        _timetracks.add(work1dayWithoutBreaks2);

        ReportEntry actual = _testee.createUserReport(_testUser, _timetracks, _timeoffs, Collections.EMPTY_LIST);

        Assert.assertEquals(25, actual.getNumOfUnusedHolidays());
        Assert.assertEquals(0, actual.getNumOfUsedHolidays());

        int expectedWorkMinutesShould = (int)_testUser.getHoursPerDay() * 60 * (
                DateTimeUtils.getWorkdaysOfTimeInterval(_testUser.getEntryDate(), DateTime.now())
                        - DateTimeUtils.getWorkdaysOfTimeInterval(_timeoffs.get(0).getFrom(), _timeoffs.get(0).getTo()));
        Assert.assertEquals(expectedWorkMinutesShould, actual.getWorkMinutesShould());

        int expectedWorkMinutesIs = date2.getMinuteOfDay() - date1.getMinuteOfDay() + (date4.getMinuteOfDay() - date3.getMinuteOfDay());
        Assert.assertEquals(expectedWorkMinutesIs, actual.getWorkMinutesIs());

    }

}
