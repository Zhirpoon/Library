package se.kth.id1212.sockets.client.net;

import java.net.InetSocketAddress;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public interface OutputHandler {
    public void handleMsg(String msg);
}
