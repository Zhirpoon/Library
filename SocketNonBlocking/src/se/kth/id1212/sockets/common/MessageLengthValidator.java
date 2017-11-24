package se.kth.id1212.sockets.common;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class MessageLengthValidator {
    private final StringBuilder receivedChars = new StringBuilder();
    private final Queue<String> receivedMessages = new ArrayDeque<>();
    
    public String prependLengthHeader(String message) {
        return message.length() + Constants.MSG_LENGTH_DELIMITER + message;
    }
    
    public synchronized void addChars(byte[] charsTemp) {
        String chars = new String(charsTemp);
        receivedChars.append(chars);
        while(addCompleteMessageToQueue());
    }
    
    public synchronized boolean hasMoreMessages() {
        return receivedMessages.peek() != null;
    }
    
    public synchronized String nextMessage() {
        return receivedMessages.poll();
    }
    
    private synchronized boolean addCompleteMessageToQueue() {
        String allCharacters = receivedChars.toString();
        String[] messageParts = getParts(allCharacters);
        if(messageParts.length < 2) {
            return false;
        }
        String lengthHeader = messageParts[0];
        int lengthOfMessage = (int) Integer.parseInt(lengthHeader);
        if(isMessageComplete(messageParts[1], lengthOfMessage)) {
            receivedMessages.add(messageParts[1].substring(0, lengthOfMessage));
            receivedChars.delete(0, lengthHeader.length() + Constants.MSG_LENGTH_DELIMITER.length() + lengthOfMessage);
            return true;
        }
        return false;
    } 
    
    private boolean isMessageComplete(String message, int length) {
        return message.length() >= length;
    }
    
    private String[] getParts(String message) {
        return message.split(Constants.MSG_LENGTH_DELIMITER);
    }
}
