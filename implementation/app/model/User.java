package model;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by david on 21.03.16.
 */
@Entity
public class User extends Model {

    @Id
    private int id;

    @Constraints.MinLength(4)
    private String userName;

    @Constraints.MinLength(8)
    private String password;

    private String role;

    private boolean active;

    private String firstName;

    private String lastName;


    public User(String username, String password, String role, String firstname, String lastname, boolean active) {
        this.userName = username;
        this.password = password;
        this.active = active;
        this.firstName = firstname;
        this.lastName = lastname;
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }
}
