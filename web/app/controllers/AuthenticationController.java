package controllers;

import business.usermanagement.UserException;
import business.usermanagement.SecurityRole;
import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import models.User;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;

import java.util.List;

/**
 * Created by sebastian on 3/28/16.
 */
public class AuthenticationController extends UserProfileController<CommonProfile> {

    public static final Form<User> FORM = Form.form(User.class);
    private UserManagement _userManagement;

    @Inject
    public AuthenticationController(UserManagement userManagement) {
        _userManagement = userManagement;
    }

    public Result loginForm() throws TechnicalException {
        final FormClient formClient = (FormClient) config.getClients().findClient("default");

        return ok(views.html.login.render(formClient.getCallbackUrl(), FORM));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result doCreateUser() throws Exception {

        Form<User> form = FORM.bindFromRequest();
        String userName = form.data().get("username");
        String password = form.data().get("password");
        String firstName = form.data().get("firstname");
        String lastName = form.data().get("lastname");
        String role = form.data().get("role");
        String email = form.data().get("email");
        String userNameBoss = form.data().get("boss");
        double hoursPerDay = Double.parseDouble(form.data().get("hours-per-day"));

        if (role.isEmpty()) {
            role = SecurityRole.ROLE_USER;
        }

        if (userNameBoss == null || userNameBoss.isEmpty()) {
            List<User> bosses = _userManagement.readBosses();
            if(bosses.size() == 0) {
                List<User> admins = _userManagement.readAdmins();
                if (admins.size() > 0) userNameBoss = admins.get(0).getUsername();
                // there must always exist at least 1 admin -> no other cases needed
            }
        }

        User boss = _userManagement.readUser(userNameBoss);

        User userToRegister = new User(userName, password, role, firstName, lastName, email, true, boss, hoursPerDay);

        _userManagement.createUser(userToRegister);

        return redirect(routes.UserManagementController.updateUser());
    }
}
