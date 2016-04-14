package controllers;

import business.UserException;
import business.usermanagement.UserManagement;
import com.google.inject.Inject;
import model.User;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.play.java.RequiresAuthentication;
import org.pac4j.play.java.UserProfileController;
import play.data.Form;
import play.mvc.Result;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by Leonhard on 13.04.2016.
 */
public class UserManagementController extends UserProfileController{

    private UserManagement _userManagement;
    public static final Form<User> FORM = Form.form(User.class);

    @Inject
    public UserManagementController(UserManagement userManagement){
        _userManagement=userManagement;
    }

    @RequiresAuthentication(clientName = "default")
    public Result indexEditUser() throws Exception{
        CommonProfile profile = getUserProfile();

        return ok(views.html.edituser.render(profile,_userManagement.getAllUsers()));
    }


    /*
    Username remains unchangable because the authenticator
     */
    @RequiresAuthentication(clientName = "default", authorizerName = "admin")
    public Result editUser() throws Exception{
        CommonProfile profile = getUserProfile();

        List<User> userList = _userManagement.getAllUsers();

        Form<User> form = FORM.bindFromRequest();

        int userId = Integer.parseInt(form.data().get("id"));
        String firstName = form.data().get("firstname");
        String lastName = form.data().get("lastname");
        String email = form.data().get("email");
        String password = form.data().get("password");
        String repeatPassword = form.data().get("repeat_password");

        User changingUser = null;

        for(User u : userList){
            if(u.getId() == userId){
                changingUser = u;
                break;
            }
        }
        if(!firstName.equals("")){
            changingUser.setFirstName(firstName);
        }
        if(!lastName.equals("")){
            changingUser.setLastName(lastName);
        }
        if(!email.equals("")){
            changingUser.setEmail(email);
        }
        if((!password.isEmpty())&&(!repeatPassword.isEmpty())&&password.equals(repeatPassword)){
            changingUser.setPassword(password);
        }

        _userManagement.changeUserData(changingUser.getUserName(),changingUser);

        return ok(views.html.edituser.render(profile,_userManagement.getAllUsers()));
    }
}
