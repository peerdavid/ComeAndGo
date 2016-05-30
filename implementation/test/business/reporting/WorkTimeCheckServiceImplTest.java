package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import models.Report;
import models.ReportEntry;
import models.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Stefan on 24.05.2016.
 */
public class WorkTimeCheckServiceImplTest {
    private WorkTimeCheckService _service;
    private ReportingService _reportingService;
    private CollectiveAgreement _collectiveAgreement;
    private InternalTimeTracking _internalTimeTracking;
    private InternalUserManagement _internalUserManagement;

    private User _testEmployee;
    private User _testBoss;
    private User _testBossOfBoss;
    private User _testPersonnellManager;
    private User _testOutstandingPerson;
    private List<User> _userList;
    private Report _report;
    private ReportEntry _reportEntry;
    private DateTime _now;
    private DateTime _startOfActualYear;

    @Before
    public void setUp() throws Exception {
        _now = DateTime.now();
        _startOfActualYear = DateTimeUtils.startOfActualYear();

        // create users and relationships between them
        _testEmployee = mock(User.class);
        _testBoss = mock(User.class);
        _testBossOfBoss = mock(User.class);
        _testPersonnellManager = mock(User.class);
        _testOutstandingPerson = mock(User.class);
        when(_testEmployee.getId()).thenReturn(1);
        when(_testBoss.getId()).thenReturn(2);
        when(_testBossOfBoss.getId()).thenReturn(3);
        when(_testPersonnellManager.getId()).thenReturn(4);
        when(_testOutstandingPerson.getId()).thenReturn(5);
        when(_testEmployee.getBoss()).thenReturn(_testBoss);
        when(_testBoss.getBoss()).thenReturn(_testBossOfBoss);
        when(_testBossOfBoss.getBoss()).thenReturn(_testBossOfBoss);
        when(_testPersonnellManager.getBoss()).thenReturn(_testPersonnellManager);
        when(_testOutstandingPerson.getBoss()).thenReturn(_testOutstandingPerson);
        when(_testEmployee.getEntryDate()).thenReturn(_startOfActualYear);
        when(_testBoss.getEntryDate()).thenReturn(_startOfActualYear);
        when(_testEmployee.getRole()).thenReturn(SecurityRole.ROLE_USER);
        when(_testBoss.getRole()).thenReturn(SecurityRole.ROLE_BOSS);
        when(_testBossOfBoss.getRole()).thenReturn(SecurityRole.ROLE_BOSS);
        when(_testPersonnellManager.getRole()).thenReturn(SecurityRole.ROLE_PERSONNEL_MANAGER);
        when(_testOutstandingPerson.getRole()).thenReturn(SecurityRole.ROLE_USER);
        _userList = new ArrayList<>();
        _userList.add(_testEmployee);
        _userList.add(_testBoss);

        _reportEntry = new ReportEntry(_testEmployee, 38.5 / 5, 0, 0, 0, (long)(4 * 38.5 * 60), (long)(4 * 38.5 * 55), 10, 0, 0, 20);
        List<ReportEntry> _entryList = new ArrayList<>();
        _entryList.add(_reportEntry);
        _report = new Report(_entryList, _reportEntry);

        _reportingService = mock(ReportingService.class);
        _collectiveAgreement = mock(CollectiveAgreement.class);
        _internalTimeTracking = mock(InternalTimeTracking.class);
        _internalUserManagement = mock(InternalUserManagement.class);
        _service = new WorkTimeCheckServiceImpl(_reportingService, _collectiveAgreement, _internalUserManagement);

        when(_reportingService.readHoursWorked(any(Integer.class), any(DateTime.class))).thenReturn(8.5);
        when(_reportingService.createEmployeeReport(any(Integer.class), any(DateTime.class), any(DateTime.class)))
                .thenReturn(_report);
        when(_internalUserManagement.readUser(1)).thenReturn(_testEmployee);
        when(_internalUserManagement.readUser(2)).thenReturn(_testBoss);
        when(_internalUserManagement.readUser(3)).thenReturn(_testBossOfBoss);
        when(_internalUserManagement.readUser(4)).thenReturn(_testPersonnellManager);
        when(_internalUserManagement.readUser(5)).thenReturn(_testOutstandingPerson);
    }

    @Test(expected = UserException.class)
    public void readWorkTimeAlertsForExactlyOneUser_WithIncompatibleFromAndToDate_ShouldFail() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.minusMillis(2), _testBoss.getId());
    }

    @Test(expected = UserException.class)
    public void readWorkTimeAlertsForMoreThanOneUsers_WithIncompatibleFromAndToDate_ShouldFail() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_userList, _now, _now.minusMillis(2), _testBoss.getId());
    }

    @Test
    public void readWorkTimeAlertsFromUser_WhereRequesterIsBossOfEmployee_ShouldSucceed() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.plusDays(2), _testBoss.getId());
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManagerOrUserItself(any(Integer.class), any(Integer.class));
    }

    @Test
    public void readWorkTimeAlertsFromUser_WhereRequesterIsBossOfBossOfUser_ShouldSucceed() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.plusDays(2), _testBossOfBoss.getId());
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManagerOrUserItself(any(Integer.class), any(Integer.class));
    }

    @Test
    public void readWorkTimeAlertsFromUser_WhereRequesterIsPersonnelManager_ShouldSucced() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.plusDays(2), _testPersonnellManager.getId());
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManagerOrUserItself(any(Integer.class), any(Integer.class));
    }

    @Test
    public void readWorkTimeAlertsFromUser_WhereRequesterIsUserItself_ShouldSucceed() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.plusDays(2), _testEmployee.getId());
        Mockito.verify(_internalUserManagement, times(1)).validateBossOfUserOrPersonnelManagerOrUserItself(any(Integer.class), any(Integer.class));
    }

    @Test
    public void readWorkTimeAlertsFromUser_WhereFromDateIsBeforeEntryDate_ShouldCallReportingServiceWithEntryDate() throws Exception {
        DateTime to = _now.plusDays(2);

        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now.minusYears(2), to, _testBossOfBoss.getId());
        Mockito.verify(_reportingService, times(1)).createEmployeeReport(_testEmployee.getId(), _startOfActualYear, to);
    }

    @Test
    public void readWorkTimeAlertsFromUser_WhereFromDateIsNull_ShouldCallReportingServiceWithEntryDate() throws Exception {
        DateTime to = _now.plusDays(2);

        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), null, to, _testBossOfBoss.getId());
        Mockito.verify(_reportingService, times(1)).createEmployeeReport(_testEmployee.getId(), _startOfActualYear, to);
    }

    @Test
    public void readWorkTimeAlertsFromUser_WithValidParameters_ShouldCallCollectiveAgreement() throws Exception {
        final int NUM_OF_DAYS = 15;

        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.plusDays(NUM_OF_DAYS), _testBoss.getId());
        Mockito.verify(_collectiveAgreement, times(1)).createGeneralWorkTimeAlerts(any(ReportEntry.class), any(List.class));
        Mockito.verify(_collectiveAgreement, times(NUM_OF_DAYS)).createFreeTimeHoursOfDayAlerts(any(User.class), any(DateTime.class),
                any(List.class), any(List.class));
        Mockito.verify(_collectiveAgreement, times(NUM_OF_DAYS)).createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(any(User.class),
                any(DateTime.class), any(List.class), any(List.class));
        Mockito.verify(_collectiveAgreement, times(NUM_OF_DAYS)).createWorkHoursOfDayAlerts(any(User.class), any(Double.class),
                any(DateTime.class), any(List.class));
    }

    @Test
    public void readWorkTimeAlertsFromTwoUsers_WithValidParameters_ShouldCallCollectiveAgreementMultipleTimes() throws Exception {
        final int NUM_OF_DAYS = 15;

        _service.readForbiddenWorkTimeAlerts(_userList, _now, _now.plusDays(NUM_OF_DAYS), _testBossOfBoss.getId());
        Mockito.verify(_collectiveAgreement, times(2)).createGeneralWorkTimeAlerts(any(ReportEntry.class), any(List.class));
        Mockito.verify(_collectiveAgreement, times(2 * NUM_OF_DAYS)).createFreeTimeHoursOfDayAlerts(any(User.class), any(DateTime.class),
                any(List.class), any(List.class));
        Mockito.verify(_collectiveAgreement, times(2 * NUM_OF_DAYS)).createFreeTimeWorkdaysPerWeekAndChristmasAndNewYearClauseAlerts(any(User.class),
                any(DateTime.class), any(List.class), any(List.class));
        Mockito.verify(_collectiveAgreement, times(2 * NUM_OF_DAYS)).createWorkHoursOfDayAlerts(any(User.class), any(Double.class),
                any(DateTime.class), any(List.class));
    }
}
