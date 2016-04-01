package controllers;

import model.UserLogin;
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
    public Result loginForm() throws TechnicalException {
        final FormClient formClient = (FormClient) config.getClients().findClient("default");
        return ok(views.html.loginForm.render(formClient.getCallbackUrl(), UserLogin.FORM));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result signUp() {
        Form<UserLogin> form = UserLogin.FORM;
        return ok(views.html.signupForm.render(form));
    }

    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result doSignUp() {
        Form form = UserLogin.FORM.bindFromRequest();

        if (UserLogin.addNewUser(form)){
            return ok(views.html.index.render(getUserProfile()));
        } else {
            return internalServerError();
        }
    }
}
