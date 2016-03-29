package business.usermanagement.authentication;

import model.UserLogin;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.http.profile.HttpProfile;
import play.data.Form;
import business.usermanagement.authorization.SecurityRole;

/**
 * Created by sebastian on 3/18/16.
 */
public class CustomUsernamePasswordAuthenticator implements UsernamePasswordAuthenticator {
    @Override
    public void validate(UsernamePasswordCredentials credentials) {
        if (credentials == null) throw new RuntimeException("No credential");
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        UserLogin login = UserLogin.findByUsername(username);

        // TODO: remove admin workaround
        if (username.equals("admin") && login == null) {
            Form<UserLogin> form = UserLogin.FORM;
            form.data().put("username", "admin");
            form.data().put("password", "admin");
            form.data().put("securityRole", SecurityRole.ROLE_ADMIN);
            UserLogin.addNewUser(form);
            login = UserLogin.findByUsername(username);
        }

        if (login == null) throwsException("user does not exist");

        if (!login.checkPassword(password)) throwsException("wrong password");

        final HttpProfile profile = new HttpProfile();
        profile.setId(login.getUserId());
        profile.addAttribute("username", username);
        profile.addRole(login.getSecurityRole());
        profile.setRemembered(true);
        credentials.setUserProfile(profile);
    }

    protected void throwsException(final String message) {
        throw new CredentialsException(message);
    }
}
