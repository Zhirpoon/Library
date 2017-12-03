package se.kth.id1212.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public interface FileClient extends Remote {
    public void handleMsg(String message) throws RemoteException;
    public void startSend() throws RemoteException;
    public void startReceive(FilePropertiesDTO fileP) throws RemoteException;
}
