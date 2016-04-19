package business.usermanagement;


import business.UserException;
import infrastructure.UserRepository;

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

    UserRepository _userRepository;
    UserService _testee;
    User _testUser;
    User _testAdmin;

    @Before
    public void SetUp() throws Exception {
        _userRepository = mock(UserRepository.class);
        _testee = new UserServiceImpl(_userRepository);
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, "testBoss");
        _testAdmin = new User("testAdmin", "admin1234", SecurityRole.ROLE_ADMIN, "Ad", "Min", "admin@kleber.at", true, "testBoss");
    }


    @Test
    public void registerUser_ShouldCallRepository() throws UserException{
        // Prepare
        when(_userRepository.readUser(_testUser.getUserName())).thenReturn(null);
        when(_userRepository.readUser(_testAdmin.getUserNameBoss())).thenReturn(_testAdmin);


        _testee.registerNewUser(_testUser);

        // Check if a new User is created in repo
        Mockito.verify(_userRepository, times(1)).createUser(any()); // Check if the function really called our repository
    }

    @Test(expected = UserException.class)
    public void registerUser_WithAlreadyExistingUser_ShouldFail() throws UserException{
        // Prepare
        when(_userRepository.readUser(_testUser.getUserName())).thenReturn(_testUser);
        when(_userRepository.readUser(_testAdmin.getUserNameBoss())).thenReturn(_testAdmin);

        _testee.registerNewUser(_testUser);

    }


    @Test(expected = UserException.class)
    public void readUser_ForNotExistingUser_ShouldFail() throws  UserException {
        // Prepare
        String notExistingUsername = "abc";
        when(_userRepository.readUser(notExistingUsername)).thenReturn(null);

        User expected = null;

        User actual = _testee.readUser(notExistingUsername);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void checkUserCredentials_ForWrongPassword_ShouldFail() throws  UserException {
        when(_userRepository.readUser(_testUser.getUserName())).thenReturn(_testUser);

        boolean expected = false;

        boolean result = _testee.checkUserCredentials(_testUser.getUserName(), "falschesPW");

        Assert.assertEquals(expected, result);
    }

    @Test
    public void checkUserCredentials_WithActualPassword_ShouldSucceed() throws  UserException {
        when(_userRepository.readUser(_testUser.getUserName())).thenReturn(_testUser);

        boolean expected = true;

        boolean result = _testee.checkUserCredentials(_testUser.getUserName(), "test1234");
        Assert.assertEquals(expected, result);
    }

    @Test(expected = UserException.class)
    public void deleteUser_ForLastRemainingAdmin_ShouldFail() throws UserException {
        //Prepare
        List<User> userList = new ArrayList<User>();
        userList.add(_testAdmin);
        when(_userRepository.readUser(_testAdmin.getUserName())).thenReturn(_testAdmin);
        when(_userRepository.getAllUsers()).thenReturn(userList);

        _testee.deleteUser(_testAdmin.getUserName());
    }

    @Test
    public void deleteUser_ForExistingUser_ShouldSucceed() throws UserException {
        //Prepare
        List<User> userList = new ArrayList<User>();
        userList.add(_testUser);
        userList.add(_testAdmin);
        when(_userRepository.readUser(_testUser.getUserName())).thenReturn(_testUser);
        when(_userRepository.getAllUsers()).thenReturn(userList);

        _testee.deleteUser(_testUser.getUserName());

        // Check if a User is deleted in repo
        Mockito.verify(_userRepository, times(1)).deleteUser(any()); // Check if the function really called our repository
    }





}
