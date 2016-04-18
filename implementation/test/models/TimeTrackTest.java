package models;

import business.UserException;
import business.usermanagement.SecurityRole;
import infrastructure.TimeTrackException;
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
    public void setFromTime_InEmptyObject_ShouldSucceed() throws TimeTrackException {
        TimeTrack testee = new TimeTrack(_testUser);
        testee.set_to(_testFromTime);
    }


    @Test(expected = TimeTrackException.class)
    public void setToTime_IsSmallerThanFromTime_ShouldFail() throws TimeTrackException{
        TimeTrack testee = new TimeTrack(_testUser);
        DateTime invalidTo = new DateTime(2016, 5, 17, 7, 0);

        testee.set_from(_testFromTime);
        testee.set_to(invalidTo);
    }


    @Test(expected = TimeTrackException.class)
    public void setFromTime_IsBiggerThanToTime_ShouldFail() throws TimeTrackException {
        TimeTrack testee = new TimeTrack(_testUser);
        DateTime invalidFrom = new DateTime(2016, 5, 17, 10, 0);

        testee.set_from(_testFromTime);
        testee.set_to(_testToTime);
        testee.set_from(invalidFrom);
    }
}