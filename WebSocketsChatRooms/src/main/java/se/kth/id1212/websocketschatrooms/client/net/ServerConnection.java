package se.kth.id1212.websocketschatrooms.client.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import org.glassfish.tyrus.client.ClientManager;
import se.kth.id1212.websocketschatrooms.common.Constants;
import se.kth.id1212.websocketschatrooms.client.model.ConnectedUser;
import static se.kth.id1212.websocketschatrooms.util.JsonUtil.formatMessage;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ServerConnection {
    
    private Session session;
    ClientManager client;
    OutputHandler outputHandler;
    ConnectedUser userInfo;
    
    public ServerConnection(OutputHandler outputHandler) {
        client = ClientManager.createClient();
        this.outputHandler = outputHandler;
    }
    
    public boolean connect(String username, String chatRoom) {
        boolean successfullConnection;
        try {    
            userInfo = new ConnectedUser(username, chatRoom);
            session = client.connectToServer(new ClientEndpoint(outputHandler), new URI(Constants.SERVERADDRESS + "/" + chatRoom));
            outputHandler.handleMsg("You are logged in as: " + userInfo.getUsername() + " in room: " + userInfo.getChatRoom());
            outputHandler.handleMsg("To send a message just type a message and press enter.\n"
                    + "To leave this chat room and enter another type \"" + Constants.CHANGE + "\".\n"
                    + "To list all rooms with users in them type \"" + Constants.LIST_ROOMS + "\".");
            successfullConnection = true;
            return successfullConnection;
        } catch (DeploymentException | URISyntaxException exception) {
            outputHandler.handleMsg("Could not connect to server.");
            outputHandler.handleMsg(exception.toString());
            successfullConnection = false;
            return successfullConnection;
        }
    }
    
    public void sendMessage(String message) throws IOException {
        if(userInfo == null) {
            outputHandler.handleMsg("You are not logged in with a username and to a chat room!");
        }
        session.getBasicRemote().sendText(formatMessage(message, userInfo.getUsername()));
    }
    
    public void quit() throws IOException {
        session.close();
        userInfo = null;
    }
}
