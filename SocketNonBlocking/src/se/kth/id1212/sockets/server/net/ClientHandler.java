package se.kth.id1212.sockets.server.net;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinPool;
import se.kth.id1212.sockets.common.MsgType;
import se.kth.id1212.sockets.common.Constants;
import se.kth.id1212.sockets.common.MessageException;
import se.kth.id1212.sockets.common.MessageLengthValidator;
import se.kth.id1212.sockets.server.controller.Controller;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ClientHandler implements Runnable {
    public final SocketChannel clientSocketChannel;
    private final Controller controller;
    private boolean connected;
    private final Queue<String> receivedMessages = new ArrayDeque<>();
    private final Queue<String> messagesToSend = new ArrayDeque<>();
    private final ByteBuffer messageFromClient = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
    public boolean timeToSend = false;
    GameServer server;
    MessageLengthValidator validator = new MessageLengthValidator();
    
    ClientHandler(SocketChannel clientSocketChannel, GameServer server) {
        controller = new Controller();
        this.server = server;
        this.clientSocketChannel = clientSocketChannel;
        connected = true;
        addToSendQueue("To start type start.");
    }
    
    @Override
    public void run() {
        while(validator.hasMoreMessages() && connected) {
            Message msg = new Message(validator.nextMessage());
            switch(msg.msgType) {
                case START:
                    controller.startGame();
                    addToSendQueue("Type <guess> followed by a letter to guess a letter.");
                    addToSendQueue("Type <solve> followed by a word to guess the word.");
                    addToSendQueue(controller.getGameState().toString());
                    break;
                case SOLVE:
                    char[] word = msg.msgBody.toCharArray();
                    controller.solveWord(word);
                    addToSendQueue(controller.getGameState().toString());
                    break;
                case GUESS:
                    char[] letter = msg.msgBody.toCharArray();
                    controller.guessLetter(letter[0]);
                    addToSendQueue(controller.getGameState().toString());
                    break;
                case DISCONNECT:
                    connected = false;
                    break;
                default:
                    throw new MessageException("Received corrupt message: " + msg.recievedString);
            }
        }
        doneRunning();
    }
    
    private void doneRunning() {
        synchronized (messagesToSend) {
            if (!messagesToSend.isEmpty()) {
                server.notifyServerMessagesToSend(clientSocketChannel);
            }
        }
    }
    
    void addToSendQueue(String message) {
        StringJoiner joiner = new StringJoiner(Constants.MSG_DELIMITER);
        joiner.add(MsgType.SERVER_RESPONSE.toString());
        joiner.add(message);
        String messageWithLengthHeader = validator.prependLengthHeader(joiner.toString());
        synchronized (messagesToSend) {
            messagesToSend.add(messageWithLengthHeader);
        }
        timeToSend = true;
    }
    
    void sendMessages() throws IOException {
        ByteBuffer message = null;
        synchronized (messagesToSend) {
            while(messagesToSend.peek() != null) {
                message = convertStringToByteBuffer(messagesToSend.peek());
                clientSocketChannel.write(message);
                if (message.hasRemaining()) {
                    throw new MessageException("Could not send message");
                }
                messagesToSend.remove();
            }
        }
    }
    
    private ByteBuffer convertStringToByteBuffer(String message) {
        byte[] bytes = message.getBytes();
        ByteBuffer convertedMessage = ByteBuffer.wrap(bytes);
        return convertedMessage;
    }
    
    void receiveFromClient() throws IOException {
        extractMessageFromClientSocket();
        ForkJoinPool.commonPool().execute(this); // Starts the clienthandler thread       
    }
    
    
    
    private void extractMessageFromClientSocket() throws IOException {
        messageFromClient.clear();
        int numberOfBytesRead;
        numberOfBytesRead = clientSocketChannel.read(messageFromClient);
        if(numberOfBytesRead == -1) {
            throw new IOException("Client has closed connection.");
        }
        messageFromClient.flip(); // makes it so it reads from the start.
        int numberOfBytesInMessage = messageFromClient.remaining();
        byte[] bytesInMessage = new byte[numberOfBytesInMessage]; // Creates a byte array of the same size as messageFromClient
        messageFromClient.get(bytesInMessage); // binds the bytes from messageFromClient to the byte array
        validator.addChars(bytesInMessage);
    }
    
    void disconnectClient() {
        try {
            clientSocketChannel.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
    }
    
    boolean isConnected() {
        return connected;
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
                String[] msgTokens = strToParse.split(Constants.MSG_DELIMITER);
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
