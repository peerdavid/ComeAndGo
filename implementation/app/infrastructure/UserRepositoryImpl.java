package infrastructure;

import com.avaje.ebean.Ebean;
import models.User;

import java.util.List;

/**
 * Created by david on 29.03.16.
 */
class UserRepositoryImpl implements UserRepository {


    @Override
    public void createUser(User user) {
        user.save();
    }


    @Override
    public User readUser(String userName) {
        User result = Ebean.find(User.class)
                .where().eq("username", userName)
                .findUnique();

        return result;
    }

    @Override
    public User readUser(int userId) {
        User result = Ebean.find(User.class)
            .where().eq("id", userId)
            .findUnique();

        return result;
    }

    @Override
    public void deleteUser(String userName) {
        User user = Ebean.find(User.class)
                .where().eq("username", userName)
                .findUnique();
        Ebean.delete(User.class, user);
    }

    @Override
    public void updateUser(User user) {
      Ebean.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList =
            Ebean.find(User.class)
            .findList();

        return userList;
    }
}
