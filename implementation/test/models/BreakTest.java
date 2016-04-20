package models;

import business.UserException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import sun.util.calendar.LocalGregorianCalendar;

import java.util.Date;

/**
 * Created by Stefan on 20.04.2016.
 */
public class BreakTest {
    Break _testBreak;

    @Before
    public void setUp() throws UserException {
        _testBreak = new Break(DateTime.now());
    }

    @Test
    public void creatingBreak_WithValidDateTimes_ShouldSucceed() throws UserException {
        _testBreak = new Break(DateTime.now(), DateTime.now().plusMillis(1));
    }

    @Test(expected = UserException.class)
    public void creatingBreak_WithInvalidDateTimes_ShouldThrowUserException() throws UserException {
        _testBreak = new Break(DateTime.now(), DateTime.now().minusMillis(1));
    }

    @Test(expected = UserException.class)
    public void setFrom_InBreakWithFromAfterTo_ShouldThrowUserException() throws UserException {
        _testBreak = new Break(DateTime.now(), DateTime.now().plusMinutes(10));
        _testBreak.setFrom(DateTime.now().plusMinutes(20));
    }

    @Test(expected = UserException.class)
    public void setTo_InBreakWithFromAfterTo_ShouldThrowUserException() throws UserException {
        _testBreak = new Break(DateTime.now(), DateTime.now().plusMinutes(10));
        _testBreak.setTo(DateTime.now().minusHours(1));
    }



}
