package business.usermanagement;


import infrastructure.UserRepository;

import model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

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
        _testUser = new User("paz", "mysecurepw", SecurityRole.ROLE_USER, "Patrick", "Summerer", "user@paz.at", true);
        _testAdmin = new User("admin2", "mysecurepw", SecurityRole.ROLE_ADMIN, "Ad", "Min", "user@paz.at", true);
    }


    @Test
    public void registerUser_ShouldCallRepository() {

        try {
            _testee.registerNewUser(_testUser);
        } catch (business.UserException e) {
            e.printStackTrace();
        }


        // Check if a new User is created in repo
        Mockito.verify(_userRepository, times(1)).createUser(any()); // Check if the function really called our repository
    }


}
