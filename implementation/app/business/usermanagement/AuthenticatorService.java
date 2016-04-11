package business.usermanagement;

import business.UserException;
import model.User;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;

/**
 * Created by david on 03.04.16.
 */
public interface AuthenticatorService extends UsernamePasswordAuthenticator {
    User readUser(String userName) throws UserException;

    boolean checkUserCredentials(String userName, String password) throws UserException;

    void registerNewUser(User userToRegister) throws UserException;

    void deleteUser(String userToDelete);
}
