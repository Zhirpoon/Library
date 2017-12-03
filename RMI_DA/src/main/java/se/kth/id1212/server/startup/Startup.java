package se.kth.id1212.server.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import se.kth.id1212.server.controller.Controller;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Startup {
    public static void main(String args[]) {
        try {
            new Startup().startRegistry();
            Naming.rebind(Controller.SERVER_NAME_IN_REGISTER, new Controller());
            System.out.println("Server is running!");
        } catch(MalformedURLException | RemoteException ex) {
            System.err.println("Could not start fileServer");
        }
    }
    
    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}
