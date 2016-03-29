package model;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.data.validation.Constraints;
import business.usermanagement.authorization.SecurityRole;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by sebastian on 3/28/16.
 */
@Entity
public class UserLogin extends Model {
    public static final Form<UserLogin> FORM = Form.form(UserLogin.class);
    public static final Model.Finder<Integer, UserLogin> FIND = new Model.Finder<>(UserLogin.class);

    @Id
    private int userId;

    @Constraints.MinLength(4)
    private String username;

    @Constraints.MinLength(8)
    private String password;

    private String securityRole = SecurityRole.ROLE_USER;

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getSecurityRole() {
        return securityRole;
    }

    public static boolean addNewUser(Form<UserLogin> form) {
        String username = form.data().get("username");

        UserLogin login = UserLogin.findByUsername(username);

        // user already exists
        if (login != null) return false;

        String password = form.data().get("password");
        String role = form.data().get("securityRole");

        User user = new User();
        user.save();

        password = generatePwd(password);

        login = new UserLogin();
        login.userId = user.getId();
        login.username = username;
        login.password = password;
        login.securityRole = role == null ? SecurityRole.ROLE_USER : role;

        login.save();

        return true;
    }

    public boolean checkPassword(@NotNull String candidate) {
        return checkPwd(password, candidate);
    }

    public void changeUsername(@NotNull String newUsername, @NotNull String password) {
        if (checkPassword(password)) this.username = newUsername;
    }

    public void changePassword(@NotNull String oldPassword, @NotNull String newPassword) {
        if (checkPassword(oldPassword)) this.password = generatePwd(newPassword);
    }

    private static String generatePwd(@NotNull String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean checkPwd(String hashed, @NotNull String candidate) {
        return BCrypt.checkpw(candidate, hashed);
    }

    public static UserLogin findByUserId(int id) {
        return FIND.byId(id);
    }

    public static UserLogin findByUsername(String username) {
        return FIND.where()
                .eq("username", username)
                .findUnique();
    }
}
