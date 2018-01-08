package se.kth.id1212.websocketschatrooms.client.controller;

import java.io.IOException;
import se.kth.id1212.websocketschatrooms.client.net.OutputHandler;
import se.kth.id1212.websocketschatrooms.client.net.ServerConnection;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ClientController {
    ServerConnection serverConnection;
            
    public ClientController(OutputHandler outputHandler) {
        serverConnection = new ServerConnection(outputHandler);
    }
    
    public boolean connectToServer(String username, String room) {
        return serverConnection.connect(username.trim(), room.trim());
    }
    
    public void sendMessageToServer(String message) throws IOException {
        serverConnection.sendMessage(message);
    }
    
    public void leaveChatRoom() throws IOException {
        serverConnection.quit();
    }
}
