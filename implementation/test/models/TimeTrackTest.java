package models;

import business.usermanagement.UserException;
import business.usermanagement.SecurityRole;
import business.timetracking.TimeTrackException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by david on 21.03.16.
 */
public class TimeTrackTest {


    DateTime _testFromTime;
    DateTime _testToTime;
    User _testUser;

    @Before
    public void setUp() throws UserException {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", false, "testBoss");
        _testFromTime = new DateTime(2016, 5, 17, 8, 0);
        _testToTime = new DateTime(2016, 5, 17, 9, 0);
    }


    @Test
    public void setFromTime_InEmptyObject_ShouldSucceed() throws UserException, TimeTrackException {
        TimeTrack testee = new TimeTrack(_testUser);
        testee.setTo(_testFromTime);
    }


    @Test(expected = UserException.class)
    public void setToTime_IsSmallerThanFromTime_ShouldFail() throws UserException, TimeTrackException{
        TimeTrack testee = new TimeTrack(_testUser);
        DateTime invalidTo = new DateTime(2016, 5, 17, 7, 0);

        testee.setFrom(_testFromTime);
        testee.setTo(invalidTo);
    }


    @Test(expected = UserException.class)
    public void setFromTime_IsBiggerThanToTime_ShouldFail() throws UserException, TimeTrackException {
        TimeTrack testee = new TimeTrack(_testUser);
        DateTime invalidFrom = new DateTime(2016, 5, 17, 10, 0);

        testee.setFrom(_testFromTime);
        testee.setTo(_testToTime);
        testee.setFrom(invalidFrom);
    }
}
