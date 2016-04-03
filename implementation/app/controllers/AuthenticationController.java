package controllers;

import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import model.User;
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
    public AuthenticationController(UserManagement userManagement){
        _userManagement = userManagement;
    }

    public Result loginForm() throws TechnicalException {
        final FormClient formClient = (FormClient) config.getClients().findClient("default");
        return ok(views.html.login.render(formClient.getCallbackUrl(), FORM));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result signUp() {
        Form<User> form = FORM;
        return ok(views.html.signup.render(form));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result doSignUp() throws Exception {

        Form<User> form = FORM.bindFromRequest();
        String userName = form.data().get("userName");
        String password = form.data().get("password");
        String firstName = form.data().get("firstName");
        String lastName = form.data().get("lastName");
        String role = form.data().get("role");

        _userManagement.registerUser(userName, password, role, firstName, lastName);

        return ok(views.html.index.render(getUserProfile()));
    }
}
