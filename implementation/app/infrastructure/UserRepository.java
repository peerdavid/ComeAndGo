package infrastructure;

import model.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
public interface UserRepository {
    void createUser(User user);

    User readUser(String userName);

    User readUser(int userId);

    void deleteUser(String userName);

    void updateUser(User user);

    List<User> getAllUsers();

}
