package infrastructure;

import business.usermanagement.UserNotFoundException;
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
    public User readUser(String userName) throws UserNotFoundException {
        User result = Ebean.find(User.class)
                .where().eq("username", userName)
                .where().eq("active", true)
                .findUnique();

        if (result != null) {
            return result;
        }

        // We should never return null
        throw new UserNotFoundException("exceptions.usermanagement.could_not_find_timetrack");
    }


    @Override
    public User readUser(int userId) throws UserNotFoundException {
        User result = Ebean.find(User.class)
                .where().eq("id", userId)
                .where().eq("active", true)
                .findUnique();

        if (result != null) {
            return result;
        }

        // We should never return null
        throw new UserNotFoundException("exceptions.usermanagement.could_not_find_timetrack");
    }


    @Override
    public List<User> readUsers() {
        List<User> userList =
                Ebean.find(User.class)
                        .where().eq("active", true)
                        .findList();

        return userList;
    }


    @Override
    public List<User> readUsersOfBoss(int userId) throws UserNotFoundException {
        List<User> userList =
                Ebean.find(User.class)
                        .where().eq("active", true)
                        .where().eq("boss_id", userId)
                        .findList();

        return userList;
    }


    @Override
    public void deleteUser(User user) {
        user.setActive(false);
        updateUser(user);
    }


    @Override
    public void updateUser(User user) {
      Ebean.update(user);
    }

}


