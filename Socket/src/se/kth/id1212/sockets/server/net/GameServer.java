package se.kth.id1212.sockets.server.net;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class GameServer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private int portNo = 8080;
    
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.parseArguments(args);
        server.serve();
    }    

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while(true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            } 
        } catch(IOException e) {
            System.err.println("Server failure!");
        }
    }
    
    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(clientSocket);
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
    
    private void parseArguments(String[] arguments) {
        if(arguments.length > 0) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch(NumberFormatException e) {
                System.err.println("Invalid port number, using default");
            }
        }
    }
}
