package models;

import business.usermanagement.UserException;
import business.usermanagement.SecurityRole;
import com.avaje.ebean.Model;
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
    private Integer id;

    @Column(name = "username", unique = true)
    @Constraints.MinLength(4)
    private String username;

    @Column(name = "password")
    @Constraints.MinLength(8)
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "active")
    private boolean active;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email")
    private String email;

    @Column(name = "_boss_id")
    @ManyToOne()
    private User boss;

    @Column(name = "salary")
    private double salary;

    public User(String username, String password, String role, String firstname, String lastname,
                String email, boolean active, User boss, double salary) throws UserException {

        // Data Validation in Setters

        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setFirstName(firstname);
        this.setLastName(lastname);
        this.setRole(role);
        this.setBoss(boss);
        this.setSalary(salary);
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws UserException {
        if (password.length() < 8) {
            throw new UserException("exceptions.usermanagement.short_password");
        }
        this.password = generatePwd(password);
    }

    public int getId() {
        return id;
    }

    public void setId(Integer _id) {
        this.id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) throws UserException {
        if (username.length() < 4 || username.length() > 20) {
            throw new UserException("exceptions.usermanagement.username_format");
        }
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) throws UserException {
        if (    !role.equals(SecurityRole.ROLE_ADMIN) &&
                !role.equals(SecurityRole.ROLE_USER) &&
                !role.equals(SecurityRole.ROLE_PERSONNEL_MANAGER) &&
                !role.equals(SecurityRole.ROLE_BOSS)) {
            throw new UserException("exceptions.usermanagement.invalid_role");
        }

        this.role = role;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String name) throws UserException {
        if (name.length() < 2 || name.length() > 50) {
            throw new UserException("exceptions.usermanagement.name_format");
        }

        this.firstname = name;
    }

    public String getLastName() {
        return lastname;
    }

    public void setLastName(String name) throws UserException {
        if (name.length() < 2 || name.length() > 50) {
            throw new UserException("exceptions.usermanagement.name_format");
        }

        this.lastname = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws UserException {
        if (!(new Constraints.EmailValidator().isValid(email))) {
            throw new UserException("exceptions.usermanagement.email_format");
        }

        this.email = email;
    }

    private String generatePwd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }

    public User getBoss() {
        return boss;
    }

    // Do not rename Setter and Getter, DataBinder needs exactly this method names!!! Weird!
    public void setBoss(User boss) {
        this.boss = boss;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) throws UserException {
        if(salary < 0){
            throw new UserException("exceptions.usermanagement.invalid_salary");
        }

        this.salary = salary;
    }
}
