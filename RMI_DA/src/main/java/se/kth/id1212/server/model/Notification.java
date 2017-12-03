package se.kth.id1212.server.model;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Notification {
    private final UserAccount user;
    private final FileProperties file;
    private final char action;
    
    public Notification(UserAccount user, FileProperties file, char action) {
        this.user = user;
        this.file = file;
        this.action = action;
    }

    public UserAccount getUser() {
        return user;
    }

    public FileProperties getFile() {
        return file;
    }

    public char getAction() {
        return action;
    }
    
    public UserAccount getOwner() {
        return file.getOwner();
    }
    
    
}
