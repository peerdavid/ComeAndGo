package infrastructure;

import model.User;

/**
 * Created by david on 29.03.16.
 */
public class UserRepositoryImpl implements UserRepository {

    @Override
    public void createUser(User user) {
        user.save();
    }
}
