package model;

/**
 * Created by david on 22.03.16.
 */
public class Notification {
    private int id;
    private String text;
    private User fromUser;
    private User toUser;
    //private String link; // Check if this is a good idea -> better use a enum with a type
    private boolean read;

    public Notification(String text, User fromUser, User toUser, boolean read) {
        this.text = text;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.read = read;
    }

    public Notification(int id, String text, User fromUser, User toUser, boolean read) {
        this(text, fromUser, toUser, read);
        this.id = id;
    }
}
