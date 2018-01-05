package se.kth.id1212.websocketschatrooms.client.model;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ConnectedUser {
    String username;
    String chatRoom;

    public ConnectedUser(String username, String chatRoom) {
        this.chatRoom = chatRoom;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }  
}
