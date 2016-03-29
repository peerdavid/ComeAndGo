package infrastructure;

import domain.User;

/**
 * Created by david on 29.03.16.
 */
public interface UserRepository {
    void createUser(User user);
}
