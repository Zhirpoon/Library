package se.kth.id1212.server.model;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.common.FileClient;
import se.kth.id1212.server.constants.Constants;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ClientManager {
    private final List<Client> clientList = new ArrayList<>();
    
    public int newClient(FileClient clientOut, UserAccount account) throws RemoteException {
        Client client = new Client(clientOut, account); 
        clientList.add(client);
        return client.getClientHash();
    }
    
    public void sendNotification(Notification notification) {
        for(Client client : clientList) {
            if(client.getUsername().equals(notification.getOwner().getUsername())) {
                try {
                    client.fileActionNotify(notification);
                    return;
                } catch (RemoteException ex) {
                    System.err.println("Could not send notification");
                }
            }
        }
    }
    
    public void removeClient(int clientToken) throws RemoteException {
        for(int i= 0;i<clientList.size();i++) {
            if(clientList.get(i).getClientHash() == clientToken) {
                Client client = clientList.get(i);
                client.sendMessage("You have been logged out.");
                clientList.remove(i);
                return;
            }
        }
    }
    
    public Client getClientByName(String name) {
        for(Client client : clientList) {
            if(name.equals(client.getUsername())) {
                return client;
            }
        }
        return null;
    }

    public Client getClientByHash(int clientToken) {
        for(Client client : clientList) {
            if(clientToken == client.getClientHash()) {
                return client;
            }
        }
        return null;
    }

    public void sendMessage(int clientToken, String message) {
        Client client = getClientByHash(clientToken);
        if(client != null) {
            try {
                client.sendMessage(message);
            } catch (RemoteException ex) {
                System.err.println("Couldn't send message");
            }
        }
    }
    
    public void notifyClient(int clientToken, FileProperties file, char action) {
        Client client = getClientByHash(clientToken);
        Notification notification = new Notification(client.getAccount(), file, action);
        sendNotification(notification);
    }

    public void notifyRead(int clientToken, FileProperties file) {
        notifyClient(clientToken, file, Constants.READ_FILE);
    }
    
    public void notifyWrite(int clientToken, FileProperties file) {
        notifyClient(clientToken, file, Constants.WRITTEN_FILE);
    }
    
    public void notifyDelete(int clientToken, FileProperties file) {
        notifyClient(clientToken, file, Constants.DELETED_FILE);
    }
}
