package se.kth.id1212.sockets.server.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.StringJoiner;
import se.kth.id1212.sockets.common.MsgType;
import se.kth.id1212.sockets.common.Constants;
import se.kth.id1212.sockets.common.MessageException;
import se.kth.id1212.sockets.server.controller.Controller;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ClientHandler implements Runnable {
    private static final String JOIN_MESSAGE = " Welcome to the game! To start type start.";
    private final Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private final Controller controller;
    private boolean connected;
    
    ClientHandler(Socket clientSocket) {
        controller = new Controller();
        this.clientSocket = clientSocket;
        connected = true;
    }
    
    @Override
    public void run() {
        try {
            boolean autoFlush = true;
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);
        } catch(IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
/*
        for(String entry : conversationWhenStarting) {
            sendMsg(entry);
        }
*/
        sendMsg("To start type start.");
        while(connected) {
            try {
                Message msg = new Message(fromClient.readLine());
                switch(msg.msgType) {
                    case START:
                        controller.startGame();
                        sendMsg(controller.getGameState().toString());
                        break;
                    case SOLVE:
                        char[] word = msg.msgBody.toCharArray();
                        controller.solveWord(word);
                        sendMsg(controller.getGameState().toString());
                        break;
                    case GUESS:
                        char[] letter = msg.msgBody.toCharArray();
                        controller.guessLetter(letter[0]);
                        sendMsg(controller.getGameState().toString());
                        break;
                    case DISCONNECT:
                        disconnectClient();
                        break;
                    default:
                        throw new MessageException("Received corrupt message: " + msg.recievedString);
                }
            } catch (IOException ioe) {
                disconnectClient();
                throw new MessageException(ioe);
            }
        }
    }
    
    void sendMsg(String msg) {
        StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMETER);
        joiner.add(MsgType.SERVER_RESPONSE.toString());
        joiner.add(msg);
        toClient.println(joiner.toString());
    }
    
    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        //server.removeHandler(this);
    }
    
    private static class Message {
        private MsgType msgType;
        private String msgBody;
        private String recievedString;
        
        private Message(String recievedString) {
            parse(recievedString);
            this.recievedString = recievedString;
        }
        
        private void parse(String strToParse) {
            try {
                String[] msgTokens = strToParse.split(Constants.MSG_DELIMETER);
                msgType = MsgType.valueOf(msgTokens[Constants.MSG_TYPE_INDEX].toUpperCase());
                if(hasBody(msgTokens)) {
                    msgBody = msgTokens[Constants.MSG_BODY_INDEX];
                }
            } catch(Throwable throwable) {
                throw new MessageException(throwable);
            }
        }
        
        private boolean hasBody(String[] msgTokens) {
            return msgTokens.length > 1;
        }
    }
}
