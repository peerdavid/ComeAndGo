package business.usermanagement;

import business.UserException;
import model.User;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;

import java.util.List;

/**
 * Created by david on 03.04.16.
 */
public interface UserService extends UsernamePasswordAuthenticator {
    User readUser(String userName) throws UserException;

    boolean checkUserCredentials(String userName, String password) throws UserException;

    void registerNewUser(User userToRegister) throws UserException;

    void deleteUser(String userToDelete) throws UserException;

    void changeUser(String userName, User newUserData) throws UserException;

    List<User> getListOfUsers() throws UserException;
}
