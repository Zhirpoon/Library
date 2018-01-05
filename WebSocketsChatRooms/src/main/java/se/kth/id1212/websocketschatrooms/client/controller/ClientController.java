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
    
    public void connectToServer(String username, String room) {
        serverConnection.connect(username, room);
    }
    
    public void sendMessageToServer(String message) throws IOException, Exception {
        serverConnection.sendMessage(message);
    }
    
    public void leaveChatRoom() throws IOException {
        serverConnection.quit();
    }
}
