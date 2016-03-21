package domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import play.api.data.validation.Invalid;

import java.security.InvalidParameterException;

/**
 * Created by david on 21.03.16.
 */
public class TimeTrackTest {


    DateTime _testFromTime;
    DateTime _testToTime;

    @Before
    public void setUp(){
        _testFromTime = new DateTime(2016, 5, 17, 8, 0);
        _testToTime = new DateTime(2016, 5, 17, 9, 0);
    }


    @Test
    public void setFromTime_InEmptyObject_ShouldSucceed(){
        TimeTrack testee = new TimeTrack();
        testee.setTo(_testFromTime);
    }


    @Test(expected = InvalidParameterException.class)
    public void setToTime_IsSmallerThanFromTime_ShouldFail(){
        TimeTrack testee = new TimeTrack();
        DateTime invalidTo = new DateTime(2016, 5, 17, 7, 0);

        testee.setFrom(_testFromTime);
        testee.setTo(invalidTo);
    }


    @Test(expected = InvalidParameterException.class)
    public void setFromTime_IsBiggerThanToTime_ShouldFail(){
        TimeTrack testee = new TimeTrack();
        DateTime invalidFrom = new DateTime(2016, 5, 17, 10, 0);

        testee.setFrom(_testFromTime);
        testee.setTo(_testToTime);
        testee.setFrom(invalidFrom);
    }
}
