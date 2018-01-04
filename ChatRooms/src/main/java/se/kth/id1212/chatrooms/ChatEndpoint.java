/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.chatrooms;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Johan
 */
@ServerEndpoint("/chat")
public class ChatEndpoint {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    
    @OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }
    
    @OnClose 
    public void onClose(Session peer) {
        peers.remove(peer);
    }
    
    @OnMessage
    public void broadcastFigure(String string, Session session) throws IOException, EncodeException {
        System.out.println("broadcastText: " + string);
        for (Session peer : peers) {
            if (!peer.equals(session)) {
                peer.getBasicRemote().sendText(string);
            }
        }
    }
    
}
