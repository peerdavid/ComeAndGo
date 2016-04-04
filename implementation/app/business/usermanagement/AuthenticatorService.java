package business.usermanagement;

import model.User;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;

/**
 * Created by david on 03.04.16.
 */
public interface AuthenticatorService extends UsernamePasswordAuthenticator {
    User readUser(String userName);
    boolean checkUserCredentials(User user, String password);
    void registerNewUser(String userName, String password, String role, String firstName, String lastName, String email) throws Exception;
}
