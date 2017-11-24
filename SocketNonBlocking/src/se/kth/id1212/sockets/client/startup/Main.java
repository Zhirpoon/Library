package se.kth.id1212.sockets.client.startup;

import se.kth.id1212.sockets.client.view.NonBlockingInterpreter;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Main {
    public static void main(String[] args) {
        new NonBlockingInterpreter().start();
    }
}
