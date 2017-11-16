package se.kth.id1212.sockets.client.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringJoiner;
import se.kth.id1212.sockets.common.MsgType;
import se.kth.id1212.sockets.common.Constants;
import se.kth.id1212.sockets.common.MessageException;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;
    
    public void connect(String host, int port, OutputHandler broadcastHandler) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        boolean autoflush = true;
        toServer = new PrintWriter(socket.getOutputStream(), autoflush);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(new Listener(broadcastHandler)).start();
    }
    
    public void disconnect() throws IOException {
        sendMsg(MsgType.DISCONNECT.toString());
        socket.close();
        socket = null;
        connected = false;
    }
    
    public void startGame() {
        sendMsg(MsgType.START.toString());
    }
    
    public void solveWord(String word) {
        sendMsg(MsgType.SOLVE.toString(), word);
    }
    
    public void guessLetter(char chr) {
        sendMsg(MsgType.GUESS.toString(), String.valueOf(chr));
    }
    
    private void sendMsg(String... parts) {
        StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMETER);
        for (String part : parts) {
            joiner.add(part);
        }
        toServer.println(joiner.toString());
    }
    
    private class Listener implements Runnable {
        private final OutputHandler outputHandler;
        
        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }
        
        @Override
        public void run() {
            try {
                for(;;) {
                    outputHandler.handleMsg(extractMsgBody(fromServer.readLine()));
                } 
            } catch(Throwable throwable) {
                if(connected) {
                    outputHandler.handleMsg("Lost connection!");
                }
            }
        }
        
        private String extractMsgBody(String msg) {
            String[] msgParts = msg.split(Constants.MSG_DELIMETER);
            if(MsgType.valueOf(msgParts[Constants.MSG_TYPE_INDEX].toUpperCase()) != MsgType.SERVER_RESPONSE) {
                throw new MessageException("Received corrupt message: " + msg);
            }
            return msgParts[Constants.MSG_BODY_INDEX];
        }
    }
}
