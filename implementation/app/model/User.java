package model;

import com.avaje.ebean.Model;
import play.data.Form;
import play.data.format.Formats;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by david on 21.03.16.
 */
@Entity
public class User extends Model {
    public static final Form<User> FORM = Form.form(User.class);
    public static final Model.Finder<Integer, User> FIND = new Model.Finder<>(User.class);

    @Id
    private int id;

    private boolean active;

    private String firstname;

    private String lastname;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLogin;

    public int getId() {
        return id;
    }

    public UserLogin getLogin() {
        return UserLogin.findByUserId(id);
    }

    public static User findById(int id) {
        return FIND.byId(id);
    }

    public static User findByUsername(@NotNull String username) {
        UserLogin login = UserLogin.findByUsername(username);
        return FIND.byId(login.getUserId());
    }
}
