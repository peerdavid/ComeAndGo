package infrastructure;

import com.avaje.ebean.Ebean;
import model.User;
import scala.xml.MetaData;

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


}
