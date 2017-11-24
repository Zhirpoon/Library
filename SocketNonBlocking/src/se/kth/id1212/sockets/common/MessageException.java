package se.kth.id1212.sockets.common;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class MessageException extends RuntimeException {
    public MessageException(String msg) {
        super(msg);
    }
    
    public MessageException(Throwable rootCause) {
        super(rootCause);
    }
}