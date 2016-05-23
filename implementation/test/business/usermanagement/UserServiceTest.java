package business.usermanagement;


import business.notification.InternalNotificationSender;
import infrastructure.UserRepository;
import javassist.NotFoundException;
import models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by david on 02.04.16.
 */
public class UserServiceTest {

    UserRepository _userRepositoryMock;
    InternalNotificationSender _notificationSenderMock;
    UserService _testee;
    User _testUser;
    User _testBoss;
    User _testAdmin;

    @Before
    public void SetUp() throws Exception {
        _userRepositoryMock = mock(UserRepository.class);
        _notificationSenderMock = mock(InternalNotificationSender.class);
        _testee = new UserServiceImpl(_userRepositoryMock, _notificationSenderMock);
        _testBoss = new User("testBoss", "test1234", SecurityRole.ROLE_BOSS, "Big", "boss", "boss@kleber.at", true, null, 1200);
        _testBoss.setBoss(_testBoss);
        _testBoss.setId(1);
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, _testBoss, 1200);
        _testAdmin = new User("testAdmin", "admin1234", SecurityRole.ROLE_ADMIN, "Ad", "Min", "admin@kleber.at", true, _testBoss, 1200);
    }


    @Test
    public void registerUser_ShouldCallRepository() throws UserException, NotFoundException {
        // Prepare
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenThrow(UserNotFoundException.class);

        _testee.createUser(_testUser);

        // Check if a new User is created in repo
        Mockito.verify(_userRepositoryMock, times(1)).createUser(any()); // Check if the function really called our repository
    }

    @Test(expected = UserException.class)
    public void registerUser_WithAlreadyExistingUser_ShouldFail() throws UserException, NotFoundException {
        // Prepare
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(_testUser);
        when(_userRepositoryMock.readUser(_testAdmin.getUsername())).thenReturn(_testAdmin);

        _testee.createUser(_testUser);

    }

    @Test(expected = UserException.class)
    public void changeUser_ForUnregisteredUser_ShouldFail() throws UserException {
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(null);

        _testee.updateUser(_testUser.getUsername(), _testUser);
    }

    @Test(expected = UserException.class)
    public void changeUser_WithPasswordLengthTooSmall_ShouldFail() throws UserException {
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(_testUser);

        _testUser.setPassword("test123");

        _testee.updateUser(_testUser.getUsername(), _testUser);
    }

    @Test(expected = UserException.class)
    public void readUser_ForNotExistingUser_ShouldFail() throws UserException {
        // Prepare
        String notExistingUsername = "abc";
        when(_userRepositoryMock.readUser(notExistingUsername)).thenReturn(null);

        User expected = null;

        User actual = _testee.readUser(notExistingUsername);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkUserCredentials_ForWrongPassword_ShouldSucceed() throws UserException {
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(_testUser);

        boolean expected = false;

        boolean result = _testee.checkUserCredentials(_testUser.getUsername(), "falschesPW");

        Assert.assertEquals(expected, result);
    }

    @Test(expected = UserException.class)
    public void checkUserCredentials_ForUnregisteredUser_ShouldFail() throws UserException {
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(null);

        boolean expected = false;

        boolean result = _testee.checkUserCredentials(_testUser.getUsername(), "falschesPW");

        Assert.assertEquals(expected, result);
    }

    @Test
    public void checkUserCredentials_WithActualPassword_ShouldSucceed() throws UserException {
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(_testUser);

        boolean expected = true;

        boolean result = _testee.checkUserCredentials(_testUser.getUsername(), "test1234");
        Assert.assertEquals(expected, result);
    }

    @Test(expected = UserException.class)
    public void deleteUser_ForLastRemainingAdmin_ShouldFail() throws Exception {
        //Prepare
        List<User> userList = new ArrayList<User>();
        userList.add(_testAdmin);
        when(_userRepositoryMock.readUser(_testAdmin.getUsername())).thenReturn(_testAdmin);
        when(_userRepositoryMock.readUsers()).thenReturn(userList);

        _testee.deleteUser(_testAdmin.getUsername(), _testAdmin.getUsername());
    }

    @Test
    public void deleteUser_ForExistingUser_ShouldSucceed() throws Exception {
        //Prepare
        List<User> userList = new ArrayList<User>();
        userList.add(_testUser);
        userList.add(_testAdmin);
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(_testUser);
        when(_userRepositoryMock.readUsers()).thenReturn(userList);

        _testee.deleteUser(_testAdmin.getUsername(), _testUser.getUsername());

        // Check if a User is deleted in repo
        Mockito.verify(_userRepositoryMock, times(1)).deleteUser(any()); // Check if the function really called our repository
    }


    @Test
    public void deleteUser_ForExistingUser_ShouldSendNotificationToTheBoss() throws Exception {
        //Prepare
        List<User> userList = new ArrayList<User>();
        userList.add(_testUser);
        userList.add(_testAdmin);
        when(_userRepositoryMock.readUser(_testUser.getUsername())).thenReturn(_testUser);
        when(_userRepositoryMock.readUsers()).thenReturn(userList);

        _testee.deleteUser(_testAdmin.getUsername(), _testUser.getUsername());

        // Check if a User is deleted in repo
        Mockito.verify(_notificationSenderMock, times(1)).sendNotification(any());
    }


}
