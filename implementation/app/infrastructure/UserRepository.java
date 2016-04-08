package infrastructure;

import model.User;

/**
 * Created by david on 29.03.16.
 */
public interface UserRepository {
    void createUser(User user);

    User readUser(String userName);

    User readUser(int userId);
}
