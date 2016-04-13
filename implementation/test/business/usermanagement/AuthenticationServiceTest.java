package business.usermanagement;


import infrastructure.UserRepository;

import model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by david on 02.04.16.
 */
public class AuthenticationServiceTest {

    UserRepository _userRepository;
    AuthenticatorService _testee;
    User _testUser;
    User _testAdmin;

    @Before
    public void SetUp() throws Exception {
        _userRepository = mock(UserRepository.class);
        _testee = new AuthenticatorServiceImpl(_userRepository);
        _testUser = new User("testUser", "test1234", SecurityRole.ROLE_USER, "Klaus", "Kleber", "klaus@kleber.at", true, "testBoss");
        _testAdmin = new User("testAdmin", "admin1234", SecurityRole.ROLE_ADMIN, "Ad", "Min", "admin@kleber.at", true, "testBoss");
    }


    @Test
    public void registerUser_ShouldCallRepository() {
        // Prepare
        when(_userRepository.readUser(_testUser.getUserName())).thenReturn(null);
        when(_userRepository.readUser(_testAdmin.getUserNameBoss())).thenReturn(_testAdmin);

        try {
            _testee.registerNewUser(_testUser);
        } catch (business.UserException e) {
            e.printStackTrace();
        }


        // Check if a new User is created in repo
        Mockito.verify(_userRepository, times(1)).createUser(any()); // Check if the function really called our repository
    }


}
