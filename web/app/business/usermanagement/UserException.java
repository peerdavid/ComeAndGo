package business.usermanagement;

import java.lang.reflect.Array;

/**
 * Created by paz on 07.04.16.
 */
public class UserException extends Exception {
    private String[] _arguments = null;

    public UserException() { super(); }
    public UserException(String message) { super(message); }
    public UserException(String message, String... arguments) {
        super(message);

        _arguments = arguments;
    }
    public UserException(String message, Throwable cause) { super(message, cause); }
    public UserException(Throwable cause) { super(cause); }

    public String[] getArguments(){
        return _arguments;
    }
}