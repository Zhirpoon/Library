/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.websocketschatrooms.client.net;

import static java.lang.String.format;
import java.text.SimpleDateFormat;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import se.kth.id1212.websocketschatrooms.common.Message;
import se.kth.id1212.websocketschatrooms.common.MessageDecoder;
import se.kth.id1212.websocketschatrooms.common.MessageEncoder;

/**
 *
 * @author Johan
 */
@javax.websocket.ClientEndpoint(encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ClientEndpoint {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    private final OutputHandler outputHandler;
    
    public ClientEndpoint(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
    }
    @OnOpen
    public void onOpen(Session session) {
        outputHandler.handleMsg(format("Connection established. session id: %s", session.getId()));
    }

    @OnMessage
    public void onMessage(Message message) {
        outputHandler.handleMsg(format("[%s:%s] %s", simpleDateFormat.format(message.getReceived()), message.getSender(), message.getContent()));
    }
    
    @OnError
    public void onError(Throwable t) {
        
    }
}
