package model;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by david on 21.03.16.
 */
@Entity
public class User extends Model {

    @Id
    private int _id;

    @Constraints.MinLength(4)
    private String _userName;

    @Constraints.MinLength(8)
    private String _password;

    private String _role;

    private boolean _active;

    private String _firstName;

    private String _lastName;

    private String _email;


    public User(String username, String password, String role, String firstname, String lastname, String email, boolean active) {
        this._userName = username;
        this._password = generatePwd(password);
        this._active = active;
        this._firstName = firstname;
        this._lastName = lastname;
        this._role = role;
        this._email = email;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        this._password = generatePwd(password);
    }

    public int getId() {
        return _id;
    }

    public String getUserName() {
        return _userName;
    }

    public String getRole() {
        return _role;
    }

    private String generatePwd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }
}
