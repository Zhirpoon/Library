package se.kth.id1212.client.startup;

import java.rmi.RemoteException;
import se.kth.id1212.client.view.NonBlockingInterpreter;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Startup {
    public static void main(String args[]) {
        try {
            new NonBlockingInterpreter().start();
        } catch (RemoteException ex) {
            System.out.println("Could not start client!");
            System.out.println(ex);
        }
    }
}
