package business.usermanagement;

import infrastructure.UserRepository;
import models.User;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Stefan on 30.05.2016.
 */
public class UserServiceImplTest {
    UserServiceImpl _service;
    UserRepository _repository;

    User _mockedTestUser;
    User _mockedTestBoss;
    User _mockedTestBossOfBoss;
    User _mockedTestPersonnellManager;
    User _mockedTestAdmin;

    @Before
    public void setUp() throws Exception {
        _repository = mock(UserRepository.class);
        _service = new UserServiceImpl(_repository, null);

        _mockedTestUser = mock(User.class);
        _mockedTestAdmin = mock(User.class);
        _mockedTestBoss = mock(User.class);
        _mockedTestPersonnellManager = mock(User.class);
        _mockedTestBossOfBoss = mock(User.class);

        when(_mockedTestUser.getId()).thenReturn(1);
        when(_mockedTestBoss.getId()).thenReturn(2);
        when(_mockedTestBossOfBoss.getId()).thenReturn(3);
        when(_mockedTestAdmin.getId()).thenReturn(4);
        when(_mockedTestPersonnellManager.getId()).thenReturn(5);

        when(_mockedTestUser.getRole()).thenReturn(SecurityRole.ROLE_USER);
        when(_mockedTestBoss.getRole()).thenReturn(SecurityRole.ROLE_USER);
        when(_mockedTestBossOfBoss.getRole()).thenReturn(SecurityRole.ROLE_BOSS);
        when(_mockedTestAdmin.getRole()).thenReturn(SecurityRole.ROLE_ADMIN);
        when(_mockedTestPersonnellManager.getRole()).thenReturn(SecurityRole.ROLE_PERSONNEL_MANAGER);

        when(_mockedTestUser.getBoss()).thenReturn(_mockedTestBoss);
        when(_mockedTestBoss.getBoss()).thenReturn(_mockedTestBossOfBoss);
        when(_mockedTestBossOfBoss.getBoss()).thenReturn(_mockedTestBossOfBoss);
        when(_mockedTestAdmin.getBoss()).thenReturn(_mockedTestBossOfBoss);
        when(_mockedTestPersonnellManager.getBoss()).thenReturn(_mockedTestBossOfBoss);

        // now make sure the correct users are returned
        when(_repository.readUser(1)).thenReturn(_mockedTestUser);
        when(_repository.readUser(2)).thenReturn(_mockedTestBoss);
        when(_repository.readUser(3)).thenReturn(_mockedTestBossOfBoss);
        when(_repository.readUser(4)).thenReturn(_mockedTestAdmin);
        when(_repository.readUser(5)).thenReturn(_mockedTestPersonnellManager);
    }



    @Test
    public void validateBossOfUser_WhereRequesterIsUserItself_ShouldSucceed() throws Exception {
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestUser.getId(), _mockedTestUser.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestBossOfBoss.getId(), _mockedTestBossOfBoss.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestAdmin.getId(), _mockedTestAdmin.getId());
    }

    @Test
    public void validateBossOfUser_WhereRequesterIsOneOfTheBossesOfUser_ShouldSucceed() throws Exception {
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestUser.getId(), _mockedTestBoss.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestUser.getId(), _mockedTestBossOfBoss.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestBoss.getId(), _mockedTestBossOfBoss.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestAdmin.getId(), _mockedTestBossOfBoss.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestPersonnellManager.getId(), _mockedTestBossOfBoss.getId());
    }

    @Test
    public void validateBossOfUser_WhereRequesterIsPersonnellManager_ShouldSucceedForAnyRequestedUser() throws Exception {
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestUser.getId(), _mockedTestPersonnellManager.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestBoss.getId(), _mockedTestPersonnellManager.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestBossOfBoss.getId(), _mockedTestPersonnellManager.getId());
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestAdmin.getId(), _mockedTestPersonnellManager.getId());
    }

    @Test(expected = UserException.class)
    public void validateBossOfUser_WhereRequesterIsEmployeeOfBoss_ShouldFail() throws Exception {
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestBoss.getId(), _mockedTestUser.getId());
    }

    @Test(expected = UserException.class)
    public void validateBossOfUser_WhereEmployeeRequestsAlertsFromPersonnellManager_ShouldFail() throws Exception {
        _service.validateBossOfUserOrPersonnelManagerOrUserItself(_mockedTestPersonnellManager.getId(), _mockedTestBoss.getId());
    }
}
