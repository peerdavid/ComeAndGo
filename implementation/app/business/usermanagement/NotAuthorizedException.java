package business.usermanagement;

/**
 * Created by david on 01.05.16.
 */
public class NotAuthorizedException extends Exception {
    public NotAuthorizedException(String message){
        super(message);
    }
}
