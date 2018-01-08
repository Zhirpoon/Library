/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.websocketschatrooms.server.net;
/**
 *
 * @author Johan
 */
import static java.lang.String.format;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import se.kth.id1212.websocketschatrooms.common.Constants;
import se.kth.id1212.websocketschatrooms.common.MessageDecoder;
import se.kth.id1212.websocketschatrooms.common.MessageEncoder;
import se.kth.id1212.websocketschatrooms.common.Message;

@javax.websocket.server.ServerEndpoint(value = "/chat/{room}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatRoom {

    @OnOpen
    public void onOpen(Session session, 
            @PathParam("room") final String room) {
        System.out.println(format("%s joined the chat room: " + room, session.getId()));
        session.getUserProperties().put("room", room);
        Message joinMessage = createServerMessage("A user has joined the chat room: " + room);
        int count = 0;
        for (Session peer : session.getOpenSessions()) {
            if(peer.isOpen() && peer.getUserProperties().get("room").equals(room)) {
                try {
                    count++;
                    peer.getBasicRemote().sendObject(joinMessage);
                } catch (IOException | EncodeException ex) {
                    System.err.println("Couldn't send out join message to users in the room.");
                    System.err.println(ex.toString());
                }
            }
        }
        sendAmountOfClientsConnectedToChatRoom(count, session, room);
    }
    
    private Message createServerMessage(String text) {
        Message serverMessage = new Message();
        serverMessage.setSender("Server");
        serverMessage.setContent(text);
        serverMessage.setReceived(new Date());
        return serverMessage;
    }
    
    private void sendAmountOfClientsConnectedToChatRoom(int amountOfClients, Session session, String room) {
        Message countMessage = createServerMessage("There are " + amountOfClients + " connected to the room: " + room);
        try {
            session.getBasicRemote().sendObject(countMessage);
        } catch (IOException | EncodeException ex) {
            System.err.println("Couldn't send countMessage to user.");
            System.err.println(ex.toString());
        } 
    }

    @OnMessage
    public void onMessage(Message message, Session session) throws IOException, EncodeException {
        if(checkIfUsernameIsNull(session)) {
            session.getUserProperties().put("user", message.getSender());
        }
        switch(message.getContent()) {
            case Constants.QUIT:
                session.close();
                break;
            case Constants.CHANGE:
                session.close();
                break;
            case Constants.LIST_ROOMS:
                sendRoomsList(session);
                break;
            default:
                String room = (String) session.getUserProperties().get("room");
                System.out.println(format("[%s:%s] %s", session.getId(), message.getReceived(), message.getContent()));
                try {
                    for (Session peer : session.getOpenSessions()) {
                        if (peer.isOpen() && room.equals(peer.getUserProperties().get("room"))) {
                            peer.getBasicRemote().sendObject(message);
                        }
                    }
                } catch (IOException | EncodeException ex) {
                    System.err.println("Could not send message to users in specified room: " + room);
                    System.err.println(ex.toString());
                }
                break;
        }     
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        System.out.println(format("%s left the chat room.", session.getId()));
        String room = (String) session.getUserProperties().get("room"); 
        for (Session peer : session.getOpenSessions()) {
            if (peer.isOpen() && room.equals(peer.getUserProperties().get("room"))) {
                Message chatMessage = createServerMessage(format("%s left the chat room.", (String) session.getUserProperties().get("user")));
                peer.getBasicRemote().sendObject(chatMessage);
            }
        }
        session.close();
            
    }
    
    @OnError
    public void onError(Session session, Throwable t) {
        try {
            session.close();
            System.err.println("User has been removed due to faulty connection.");
            System.err.println(t.toString());
        } catch (IOException ex) {
            System.err.println("Something went wrong when removing user.");
            System.err.println(ex.toString());
        }
    }
    
    private void sendRoomsList(Session session) throws IOException, EncodeException {
        ArrayList<String> rooms = new ArrayList<>();
        for (Session peer : session.getOpenSessions()) {
            String room = (String) peer.getUserProperties().get("room");
            if(!rooms.contains(room)) {
                rooms.add(room);
            }
        }
        Message message =  createServerMessage("");
        for(String room : rooms) {    
            message.setContent(room);
            session.getBasicRemote().sendObject(message);
        }
    }
    
    private boolean checkIfUsernameIsNull(Session session) {
        return session.getUserProperties().get("user") == null;
    }
}
