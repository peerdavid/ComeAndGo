package business.usermanagement;

import models.User;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;

import java.util.List;

/**
 * Created by david on 03.04.16.
 */
interface UserService extends UsernamePasswordAuthenticator {
    User readUser(String userName) throws UserException;

    List<User> readUsers() throws UserException;

    void createUser(User userToRegister) throws UserException;

    void deleteUser(String userToDelete) throws UserException;

    void updateUser(String userName, User newUserData) throws UserException;

    boolean checkUserCredentials(String userName, String password) throws UserException;
}
