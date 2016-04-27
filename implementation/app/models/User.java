package models;

import business.usermanagement.UserException;
import business.usermanagement.SecurityRole;
import com.avaje.ebean.Model;
import javassist.NotFoundException;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;

/**
 * Created by david on 21.03.16.
 */
@Entity
public class User extends Model {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _id;

    @Column(name = "username", unique = true)
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

        // Data Validation in Setters

        this.setUserName(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setFirstName(firstname);
        this.setLastName(lastname);
        this.setRole(role);
        this.setUserNameBoss(bossUserName);
        this._active = active;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) throws UserException {
        if (password.length() < 8) {
            throw new UserException("exceptions.usermanagement.short_password");
        }
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

    public String getEmail() {
        return _email;
    }

    public void setUserName(String username) throws UserException {
        if (username.length() < 4 || username.length() > 20) {
            throw new UserException("exceptions.usermanagement.username_format");
        }
        this._userName = username;
    }

    public void setFirstName(String name) throws UserException {
        if (name.length() < 2 || name.length() > 50) {
            throw new UserException("exceptions.usermanagement.name_format");
        }

        this._firstName = name;
    }

    public void setLastName(String name) throws UserException {
        if (name.length() < 2 || name.length() > 50) {
            throw new UserException("exceptions.usermanagement.name_format");
        }

        this._lastName = name;
    }

    public void setEmail(String email) throws UserException {
        if (!(new Constraints.EmailValidator().isValid(email))) {
            throw new UserException("exceptions.usermanagement.email_format");
        }

        this._email = email;
    }

    private String generatePwd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

    public void setRole(String role) throws UserException {
        if (    !role.equals(SecurityRole.ROLE_ADMIN) &&
                !role.equals(SecurityRole.ROLE_USER) &&
                !role.equals(SecurityRole.ROLE_PERSONNEL_MANAGER) &&
                !role.equals(SecurityRole.ROLE_BOSS)) {
            throw new UserException("exceptions.usermanagement.invalid_role");
        }

        this._role = role;
    }

    public void setUserNameBoss(String name) throws UserException {
        if (name.length() < 4 || name.length() > 20) {
            throw new UserException("exceptions.usermanagement.boss_username_format");
        }
        _userNameBoss = name;
    }

    public String getUserNameBoss() throws NotFoundException {
        if(_userNameBoss == null) {
            throw new NotFoundException("boss not found");
        }
        return _userNameBoss;
    }

    public boolean getActive() {
        return _active;
    }

    public void setActive(boolean active) {
        _active = active;
    }


}
