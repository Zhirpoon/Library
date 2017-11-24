package se.kth.id1212.sockets.client.net;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import se.kth.id1212.sockets.common.MsgType;
import se.kth.id1212.sockets.common.Constants;
import se.kth.id1212.sockets.common.MessageException;
import se.kth.id1212.sockets.common.MessageLengthValidator;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ServerConnection implements Runnable {
    private SocketChannel socketChannel;
    private InetSocketAddress serverAddress;
    private volatile boolean connected;
    private OutputHandler handler;
    private Selector selector;
    private final Queue<String> messagesToSend = new ArrayDeque<>();
    private final ByteBuffer messageFromServer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
    private boolean timeToSend = false;
    private final MessageLengthValidator validator = new MessageLengthValidator();
    
    public void connect(String host, int port, OutputHandler outputHandler) {
        serverAddress = new InetSocketAddress(host, port);
        handler = outputHandler;
        new Thread(this).start();
    }
    
    public void quit() throws IOException {
        connected = false;
        sendMsg(MsgType.DISCONNECT.toString());
        handler.handleMsg("You have disconnected from the server.");
    }
    
    private void disconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
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
        StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMITER);
        for (String part : parts) {
            joiner.add(part);
        }
        synchronized (messagesToSend) {
            messagesToSend.add(validator.prependLengthHeader(joiner.toString()));
        }
        timeToSend = true;
        selector.wakeup();
    }
    
    private ByteBuffer convertStringToByteBuffer(String message) {
        byte[] bytes = message.getBytes();
        ByteBuffer convertedMessage = ByteBuffer.wrap(bytes);
        return convertedMessage;
    }
    
    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
    }
    
    @Override
    public void run() {
        try {
            initConnection();
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);        
            while(connected || !messagesToSend.isEmpty()) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection(key);
                    } else if (key.isReadable()) {
                        receiveFromServer();
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lost connection");
            System.err.println(e);
        }
            
    }
    
    private void sendToServer(SelectionKey key) throws IOException {
        ByteBuffer message;
        synchronized (messagesToSend) {
            while(messagesToSend.peek() != null) {
                message = convertStringToByteBuffer(messagesToSend.peek());
                socketChannel.write(message);
                if(message.hasRemaining()) {
                    return;
                }
                messagesToSend.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }
    
    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            handleMsg("Connected to: " + remoteAddress);
        } catch (IOException couldNotGetRemAddrUsingDefaultInstead) {
            handleMsg("Connected default: " + serverAddress);
        }
    }
    
    private void receiveFromServer() throws IOException {
        extractMessageFromServerSocket();
        while(validator.hasMoreMessages()) {
            handleMsg(extractMsgBody(validator.nextMessage()));
        }
    }
    
    private void extractMessageFromServerSocket() throws IOException {
        messageFromServer.clear();
        int numberOfBytesRead;
        numberOfBytesRead = socketChannel.read(messageFromServer);
        if(numberOfBytesRead == -1) {
            throw new IOException("Client has closed connection.");
        }
        messageFromServer.flip(); // makes it so it reads from the start.
        int numberOfBytesInMessage = messageFromServer.remaining();
        byte[] bytesInMessage = new byte[numberOfBytesInMessage]; // Creates a byte array of the same size as messageFromClient
        messageFromServer.get(bytesInMessage); // binds the bytes from messageFromClient to the byte array
        validator.addChars(bytesInMessage);
    }
    
    private void handleMsg(String message) {
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                handler.handleMsg(message);
            }
        });
    }
        
    private String extractMsgBody(String msg) {
        String[] msgParts = msg.split(Constants.MSG_DELIMITER);
        if(MsgType.valueOf(msgParts[Constants.MSG_TYPE_INDEX].toUpperCase()) != MsgType.SERVER_RESPONSE) {
            throw new MessageException("Received corrupt message: " + msg);
        }
        return msgParts[Constants.MSG_BODY_INDEX];
    }
}
