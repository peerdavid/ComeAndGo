package infrastructure;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
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
                .where().eq("isfired", false)
                .findUnique();

        return result;
    }

    @Override
    public User readUser(int userId) {
        User result = Ebean.find(User.class)
            .where().eq("id", userId)
            .where().eq("isfired", false)
            .findUnique();

        return result;
    }

    @Override
    public void deleteUser(User user) {
        user.setFired(true);
        updateUser(user);
    }

    @Override
    public void updateUser(User user) {
      Ebean.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList =
                Ebean.find(User.class)
                .where().eq("isfired", false)
                .findList();

        return userList;
    }
}
