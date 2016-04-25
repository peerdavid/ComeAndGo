package models;

import business.usermanagement.UserException;
import business.usermanagement.SecurityRole;

import org.junit.Before;
import org.junit.Test;


/**
 * Created by paz on 14.04.16.
 */
public class UserTest {

    User _testUser;

    @Before
    public void SetUp() throws UserException {
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, "testBoss");
    }

    @Test(expected = UserException.class)
    public void setInvalidPasswort_ShouldFail() throws UserException {
        _testUser.setPassword("1");
    }

    @Test(expected = UserException.class)
    public void setInvalidEmail_ShouldFail() throws UserException {
        _testUser.setEmail("asdasd");
    }

    @Test(expected = UserException.class)
    public void setInvalidUserName_ShouldFail() throws UserException {
        _testUser.setUserName("1");
    }

    @Test(expected = UserException.class)
    public void setInvalidFirstName_ShouldFail() throws UserException {
        _testUser.setFirstName("1");
    }

    @Test(expected = UserException.class)
    public void setInvalidLastName_ShouldFail() throws UserException {
        _testUser.setLastName("1");
    }

    @Test(expected = UserException.class)
    public void setInvalidBossUserName_ShouldFail() throws UserException {
        _testUser.setUserNameBoss("1");
    }

    @Test(expected = UserException.class)
    public void setInvalidRole_ShouldFail() throws UserException {
        _testUser.setRole("abc");
    }

}
