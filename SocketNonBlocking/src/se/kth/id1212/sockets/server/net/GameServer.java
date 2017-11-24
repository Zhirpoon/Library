package se.kth.id1212.sockets.server.net;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class GameServer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private int portNo = 8080;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;
    
    private GameServer () {
        try {
            selector = Selector.open();
            initListeningSocketChannel();
        } catch (Exception e) {
            System.err.println("Could not create server.");
        }       
    }
    
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.parseArguments(args);
        server.serve();
    }    

    private void serve() {
        try {
            while(true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    ClientHandler clientHandler = (ClientHandler) key.attachment();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    } else if (key.isReadable()) {
                        receiveFromClient(key);
                    } else if (key.isWritable()) {
                        sendToClient(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server failure.");
        }
    }
    
    private void sendToClient(SelectionKey key) throws IOException {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        try {
            clientHandler.sendMessages();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }
    
    private void receiveFromClient(SelectionKey key) throws IOException{
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        try {
            clientHandler.receiveFromClient();
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }
    
    private void removeClient(SelectionKey clientKey) {
        ClientHandler clientHandler = (ClientHandler) clientKey.attachment();
        clientHandler.disconnectClient();
        clientKey.cancel();
    }
    
    private void initListeningSocketChannel() throws IOException {
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(portNo));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    
    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler clientHandler = new ClientHandler(clientChannel, this);
        clientChannel.register(selector, SelectionKey.OP_WRITE, clientHandler);
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }
    
    void notifyServerMessagesToSend(SocketChannel channel) {
        channel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
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
