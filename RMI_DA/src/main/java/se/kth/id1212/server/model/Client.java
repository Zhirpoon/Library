package se.kth.id1212.server.model;

import java.rmi.RemoteException;
import se.kth.id1212.common.FileClient;
import se.kth.id1212.common.FilePropertiesDTO;
import se.kth.id1212.server.constants.Constants;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Client {
    private final UserAccount account;
    private final FileClient clientOut;
    private int clientHash;
    
    public Client(FileClient clientOut, UserAccount account) throws RemoteException {
        this.clientOut = clientOut;
        this.account = account;
        setHash();
        connected();
    }
    
    public void setHash() {
        clientHash = account.getPassword().hashCode();
        clientHash += account.getUsername().hashCode();
    }
    
    public UserAccount getAccount() {
        return account;
    }
    
    public String getUsername() {
        return account.getUsername();
    }
    public int getClientHash() {
        return clientHash;
    }
    
    public void sendMessage(String message) throws RemoteException {
        clientOut.handleMsg(message);
    }
    
    public void startSend() throws RemoteException {
        clientOut.startSend();
    }
    
    public final void connected() throws RemoteException {
        sendMessage("You are now logged in to the server with username: " + account.getUsername());
    }
    
    public void fileActionNotify(Notification notification) throws RemoteException {
        switch (notification.getAction()) {
            case Constants.READ_FILE:
                fileReadNotify(notification);
                break;
            case Constants.WRITTEN_FILE:
                fileWrittenNotify(notification);
                break;
            case Constants.DELETED_FILE:
                fileDeletedNotify(notification);
                break;
        }
    }
    
    private void fileReadNotify(Notification notification) throws RemoteException {
        sendMessage("User: " + notification.getUser().getUsername() 
                + " has read your file called: " 
                + notification.getFile().getFileName());
    }
    
    private void fileDeletedNotify(Notification notification) throws RemoteException {
        sendMessage("User: " + notification.getUser().getUsername() 
                + " has deleted your file called: " 
                + notification.getFile().getFileName());
    }
    
    private void fileWrittenNotify(Notification notification) throws RemoteException {
        sendMessage("User: " + notification.getUser().getUsername() 
                + " has updated your file called: " 
                + notification.getFile().getFileName());
    }

    public void startReceive(FilePropertiesDTO fileP) throws RemoteException {
        clientOut.startReceive(fileP);
    }
}
