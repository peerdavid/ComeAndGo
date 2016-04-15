package controllers;

import business.usermanagement.SecurityRole;
import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import model.User;
import org.joda.time.DateTime;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;

/**
 * Created by sebastian on 3/28/16.
 */
public class AuthenticationController extends UserProfileController<CommonProfile> {

    private UserManagement _userManagement;
    public static final Form<User> FORM = Form.form(User.class);

    @Inject
    public AuthenticationController(UserManagement userManagement) {
        _userManagement = userManagement;
    }

    public Result loginForm() throws TechnicalException {
        final FormClient formClient = (FormClient) config.getClients().findClient("default");

        return ok(views.html.login.render(formClient.getCallbackUrl(), FORM));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result signUp() {
        Form<User> form = FORM;
        CommonProfile profile = getUserProfile();

        return ok(views.html.signup.render(form, profile));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result doSignUp() throws Exception {

        Form<User> form = FORM.bindFromRequest();
        String userName = form.data().get("username");
        String password = form.data().get("password");
        String firstName = form.data().get("firstname");
        String lastName = form.data().get("lastname");
        String role = form.data().get("role");
        String email = form.data().get("email");

        if (role.isEmpty()) {
            role = SecurityRole.ROLE_USER;
        }

        User userToRegister = new User(userName, password, role, firstName, lastName, email, true, "admin");

        _userManagement.registerUser(userToRegister);

        return redirect(routes.TimeTrackController.index());
    }
}
