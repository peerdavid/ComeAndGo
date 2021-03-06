package business.timetracking;

import business.usermanagement.InternalUserManagement;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import infrastructure.TimeOffRepository;
import infrastructure.TimeTrackingRepository;
import models.Break;
import models.TimeOff;
import models.TimeTrack;
import models.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Stefan on 02.05.2016.
 */
public class TimeOffValidationImplTest {
    TimeTrackingRepository _timeTrackingRepository;
    InternalUserManagement _userRepository;
    TimeOffRepository _timeOffRepository;
    User _testUser;
    Break _testBreak;
    TimeOffValidationImpl _validation;
    DateTime _MIDNIGHT;
    DateTime _MIDDAY;

    @Before
    public void setUp() throws UserException {
        _testUser = mock(User.class);
        when(_testUser.getFirstName()).thenReturn("Stefan");
        when(_testUser.getLastName()).thenReturn("TesterStefan");
        when(_testUser.getId()).thenReturn(1);
        when(_testUser.getEntryDate()).thenReturn(DateTimeUtils.BIG_BANG);
        when(_testUser.getRole()).thenReturn(SecurityRole.ROLE_USER);
        _testBreak = new Break(DateTime.now());

        _timeTrackingRepository = mock(TimeTrackingRepository.class);
        _userRepository = mock(InternalUserManagement.class);
        _timeOffRepository = mock(TimeOffRepository.class);
        _validation = new TimeOffValidationImpl(_timeOffRepository, _timeTrackingRepository);

        _MIDNIGHT  = new DateTime(2016, 5, 3, 0, 0, 0);
        _MIDDAY = _MIDNIGHT.plusHours(12);
    }

    @Test
    public void validateTimeOffInsert_WithNoClashingTimeOffs_ShouldSucceed() throws Exception {
        when(_timeOffRepository.readTimeOffsFromUser(_testUser, _MIDNIGHT, _MIDDAY)).thenThrow(TimeTrackException.class);
        _validation.validateTimeOff(_testUser, _MIDNIGHT, _MIDDAY);
    }

    @Test (expected = UserException.class)
    public void validateTimeOffInsert_WithAlreadyACCEPTEDTimeOffsAtSameTime_ShouldThrowUserException() throws UserException, TimeTrackException {
        // prepare
        TimeOff _firstTimeOff = new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(5), TimeOffType.HOLIDAY, RequestState.REQUEST_ACCEPTED, "nothing to say");
        TimeOff _secondTimeOff = new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(5), TimeOffType.PARENTAL_LEAVE, RequestState.REQUEST_REJECTED, "nothing to say");
        List<TimeOff> list = new ArrayList<>();
        list.add(_firstTimeOff);
        list.add(_secondTimeOff);
        when(_timeOffRepository.readTimeOffsFromUser(any(User.class), any(DateTime.class), any(DateTime.class))).thenReturn(list);

        _validation.validateTimeOff(_testUser, _MIDNIGHT, _MIDDAY);
    }

    @Test
    public void validateTimeOffInsert_WithNoOtherACCEPTEDTimeOffsAtSameTime_ShouldSucceed() throws UserException, TimeTrackException {
        // prepare
        TimeOff _firstTimeOff = new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(6), TimeOffType.SPECIAL_HOLIDAY, RequestState.REQUEST_SENT, "nothing to say");
        TimeOff _secondTimeOff = new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(6), TimeOffType.PARENTAL_LEAVE, RequestState.REQUEST_REJECTED, "nothing to say");
        List<TimeOff> testList = new ArrayList<>();
        testList.add(_firstTimeOff);
        testList.add(_secondTimeOff);
        when(_timeOffRepository.readTimeOffsFromUser(any(User.class), any(DateTime.class), any(DateTime.class))).thenReturn(testList);

        // test
        _validation.validateTimeOff(_testUser, _MIDNIGHT, _MIDDAY);
    }

    @Test(expected = UserException.class)
    public void validateTimeOffInsert_WithAlreadyDONETimeOffAtSameTime_ShouldThrowUserException() throws UserException, TimeTrackException {
        TimeOff timeOff = new TimeOff(_testUser, DateTime.now(), DateTime.now().plusHours(3), TimeOffType.PARENTAL_LEAVE, RequestState.DONE, "");
        List<TimeOff> testList = new ArrayList<>();
        testList.add(timeOff);
        when(_timeOffRepository.readTimeOffsFromUser(any(User.class), any(DateTime.class), any(DateTime.class))).thenReturn(testList);

        _validation.validateTimeOff(_testUser, _MIDNIGHT, _MIDDAY);
    }

    @Test(expected = UserException.class)
    public void validateCome_WhileTimeOffIsTaken_ShouldFail() throws Exception {
        DateTime now = DateTime.now();
        when(_timeOffRepository.readTimeOffsFromUser(any(User.class), any(DateTime.class), any(DateTime.class)))
            .thenThrow(UserException.class);

        _validation.validateComeForDate(_testUser, now);
        Mockito.verify(_validation, times(1)).validateTimeOff(any(User.class), any(DateTime.class), any(DateTime.class));
    }

    @Test
    public void validateCome_WhileNoTimeOffIsTaken_ShouldSucceed() throws Exception {
        DateTime now = DateTime.now();
        _validation.validateComeForDate(_testUser, now);
    }

    @Test(expected = UserException.class)
    public void validateTimeOff_WhichIsBeforeUsersEntry_ShouldFail() throws Exception {
        when(_testUser.getEntryDate()).thenReturn(DateTime.now());
        _validation.validateTimeOff(_testUser, DateTimeUtils.BIG_BANG, DateTimeUtils.BIG_BANG.plusHours(18));
    }


}
