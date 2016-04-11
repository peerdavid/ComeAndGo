package model;

import business.UserException;
import business.usermanagement.SecurityRole;
import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by david on 21.03.16.
 */
@Entity
public class User extends Model {

    @Id
    @Column(name = "id")
    private int _id;

    @Column(name = "username")
    @Constraints.MinLength(4)
    private String _userName;

    @Column(name = "password")
    @Constraints.MinLength(8)
    private String _password;

    @Column(name = "role")
    private String _role;

    @Column(name = "active")
    private boolean _active;

    @Column(name = "firstname")
    private String _firstName;

    @Column(name = "lastname")
    private String _lastName;

    @Column(name = "email")
    private String _email;

    @Column(name = "user_name_boss")
    private String _userNameBoss;

    public User(String username, String password, String role, String firstname, String lastname, String email, boolean active, String bossUserName) throws UserException {

        // Data Validation

        if (username.length() < 4 || username.length() > 20) {
            throw new UserException("exceptions.usermanagement.username_format");
        }

        if (password.length() < 8) {
            throw new UserException("exceptions.usermanagement.short_password");
        }

        if (!(new Constraints.EmailValidator().isValid(email))) {
            throw new UserException("exceptions.usermanagement.email_format");
        }

        if (firstname.length() < 2 || lastname.length() < 2 || firstname.length() > 50 || lastname.length() > 50) {
            throw new UserException("exceptions.usermanagement.short_password");
        }

        if (!role.equals(SecurityRole.ROLE_ADMIN) || !role.equals(SecurityRole.ROLE_USER) || !role.equals(SecurityRole.ROLE_PERSONNEL_MANAGER)) {
            throw new UserException("exceptions.usermanagement.invalid_role");
        }

        if (bossUserName.length() < 4 || username.length() > 20) {
            throw new UserException("exceptions.usermanagement.boss_username_format");
        }

        this._userName = username;
        this._password = generatePwd(password);
        this._email = email;
        this._firstName = firstname;
        this._lastName = lastname;
        this._role = role;
        this._userNameBoss = bossUserName;
        this._active = active;
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

    public String getFirstName() {
        return _firstName;
    }

    public String getLastName() {
        return _lastName;
    }

    private String generatePwd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

   public void setUserNameBoss(String name) {
        _userNameBoss = name;
   }

   public String getUserNameBoss() {
        return _userNameBoss;
   }

}
