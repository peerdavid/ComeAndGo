package infrastructure;

import business.usermanagement.UserNotFoundException;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface UserRepository {
    void createUser(User user);

    User readUser(String userName) throws UserNotFoundException;

    User readUser(int userId) throws UserNotFoundException;

    void deleteUser(User user);

    void updateUser(User user);

    List<User> readUsers();

}