package model;

import business.UserException;
import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

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


        this._userName = username;
        this._password = generatePwd(password);
        this._active = active;
        this._firstName = firstname;
        this._lastName = lastname;
        this._role = role;

        // Check format of Email-adress
        if (new Constraints.EmailValidator().isValid(email)) {
            this._email = email;
        } else {
            throw new UserException("exceptions.usermanagement.email_format");
        }

        this._userNameBoss = bossUserName;

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
