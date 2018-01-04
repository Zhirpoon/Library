/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.websocketschatrooms.server.model;
/**
 *
 * @author Johan
 */
import static java.lang.String.format;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import se.kth.id1212.websocketschatrooms.common.MessageDecoder;
import se.kth.id1212.websocketschatrooms.common.MessageEncoder;
import se.kth.id1212.websocketschatrooms.common.Message;

@javax.websocket.server.ServerEndpoint(value = "/chat/{room}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatRoom {

    static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session session, @PathParam("room") final String room) {
        System.out.println(format("%s joined the chat room: " + room, session.getId()));
        session.getUserProperties().put("room", room);
    }

    @OnMessage
    public void onMessage(Message message, Session session) throws IOException, EncodeException {
        String room = (String) session.getUserProperties().get("room");
        if ("quit".equalsIgnoreCase(message.getContent())) {
            session.close();
        }
        if("leave".equalsIgnoreCase(message.getContent())) {
            session.close();
        }

        System.out.println(format("[%s:%s] %s", session.getId(), message.getReceived(), message.getContent()));
        try {
            for (Session peer : session.getOpenSessions()) {
                if (peer.isOpen() && room.equals(peer.getUserProperties().get("room"))) {
                    peer.getBasicRemote().sendObject(message);
                }
            }
        } catch (IOException | EncodeException e) {
			System.err.println("Could not send message");
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        System.out.println(format("%s left the chat room.", session.getId()));
        peers.remove(session);
        //notify peers about leaving the chat room
        for (Session peer : peers) {
            Message chatMessage = new Message();
            chatMessage.setSender("Server");
            chatMessage.setContent(format("%s left the chat room.", (String) session.getUserProperties().get("user")));
            chatMessage.setReceived(new Date());
            peer.getBasicRemote().sendObject(chatMessage);
        }
    }

}
