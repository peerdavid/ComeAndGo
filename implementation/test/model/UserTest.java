package model;

import business.UserException;
import business.usermanagement.SecurityRole;
import org.junit.Test;

/**
 * Created by paz on 14.04.16.
 */
public class UserTest {

    @Test(expected = UserException.class)
    public void createUserWithInvalidPasswort_ShouldFail() throws UserException {
        User newUser = new User("invalidUser", "1", SecurityRole.ROLE_USER, "1", "2", "asss@sdfs.at", true, "testBoss");
    }

    @Test(expected = UserException.class)
    public void createUserWithInvalidEmail_ShouldFail() throws UserException {
        User newUser = new User("invalidUser", "1322342342", SecurityRole.ROLE_USER, "1", "2", "asss", true, "testBoss");
    }

    @Test(expected = UserException.class)
    public void createUserWithInvalidUserName_ShouldFail() throws UserException {
        User newUser = new User("abc", "1322342342", SecurityRole.ROLE_USER, "1234234", "2234234", "asss@sdfs.at", true, "testBoss");
    }

}
