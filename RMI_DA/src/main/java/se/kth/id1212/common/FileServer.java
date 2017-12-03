package se.kth.id1212.common;

import java.rmi.Remote;
import java.util.List;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public interface FileServer extends Remote {
    
    public static final String SERVER_NAME_IN_REGISTER = "FileServer";
    public UserAccountDTO getUserAccount(String username) throws Exception;
    public FilePropertiesDTO getFilePropertiesByFileName(String fileName) throws Exception;
    public void createUserAccount(String username, String Password) throws Exception;
    public int login(FileClient clientOut, String username, String password) throws Exception;
    public void unregister(int myIdAtServer) throws Exception;
    public void fileToServer(String fileName, long size, int clientToken) throws Exception;
    public void updateFile(String fileName, long size, int clientToken) throws Exception;
    public void deleteFile(String fileName, int clientToken) throws Exception;
    public void downloadFile(String fileName, int clientToken) throws Exception;
    public List<? extends FilePropertiesDTO> listFileProperties(int clientToken) throws Exception;
    public int logout(int clientToken) throws Exception;
    public void updateFileWriteable(String fileName, boolean isWritable, int clientToken) throws Exception;
    public void updateFileReadable(String fileName, boolean isReadable, int clientToken) throws Exception;
    public void setNotifyFileOwner(String fileName, boolean notifyOwner, int clientToken) throws Exception;
    
}
