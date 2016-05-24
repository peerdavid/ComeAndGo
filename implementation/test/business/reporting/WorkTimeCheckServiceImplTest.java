package business.reporting;

import business.timetracking.InternalTimeTracking;
import business.usermanagement.InternalUserManagement;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserException;
import models.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private DateTime _now;

    @Before
    public void setUp() throws Exception {
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
        _userList = new ArrayList<>();
        _userList.add(_testEmployee);
        _userList.add(_testBoss);

        _now = DateTime.now();
        _reportingService = mock(ReportingService.class);
        _collectiveAgreement = mock(CollectiveAgreement.class);
        _internalTimeTracking = mock(InternalTimeTracking.class);
        _internalUserManagement = mock(InternalUserManagement.class);
        _service = new WorkTimeCheckServiceImpl(_reportingService, _collectiveAgreement, _internalUserManagement);
    }

    @Test(expected = UserException.class)
    public void readWorkTimeAlertsForExactlyOneUser_WithIncompatibleFromAndToDate_ShouldFail() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_testEmployee.getId(), _now, _now.minusMillis(2), _testBoss.getId());
    }

    @Test(expected = UserException.class)
    public void readWorkTimeAlertsForMoreThanOneUsers_WithIncompatibleFromAndToDate_ShouldFail() throws Exception {
        _service.readForbiddenWorkTimeAlerts(_userList, _now, _now.minusMillis(2), _testBoss.getId());
    }
}
