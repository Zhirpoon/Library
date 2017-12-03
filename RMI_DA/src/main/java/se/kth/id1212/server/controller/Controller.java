package se.kth.id1212.server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import se.kth.id1212.common.FileClient;
import se.kth.id1212.common.FilePropertiesDTO;
import se.kth.id1212.common.UserAccountDTO;
import se.kth.id1212.server.integration.DBA;
import se.kth.id1212.server.model.Client;
import se.kth.id1212.server.model.ClientManager;
import se.kth.id1212.server.model.FileProperties;
import se.kth.id1212.server.model.UserAccount;
import se.kth.id1212.server.net.FileTransferService;
import se.kth.id1212.common.FileServer;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Controller extends UnicastRemoteObject implements FileServer {
    private final DBA dba;
    private final ClientManager clientManager;
    public Controller() throws RemoteException{
        dba = new DBA();
        clientManager = new ClientManager();
    }
    
    @Override
    public void createUserAccount(String username, String password) throws Exception {
        try {
            if(dba.findUserAccountByName(username) != null) {
                throw new Exception("Username already taken!");
            }
            dba.createUserAccount(new UserAccount(username, password));
        } catch (Exception ex) {
            throw new Exception("Couldn't create userAccount", ex);
        }
    }
    
    @Override
    public void setNotifyFileOwner(String fileName, boolean notifyOwner, int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in to set notify on file!");
        }
        FileProperties fileToUpdate = null;
        try {
            FileProperties file = getFilePropertiesByFileName(fileName);
            Client client = clientManager.getClientByHash(clientToken);
            if(file != null && client.getUsername().equals(file.getOwner().getUsername())) {
                fileToUpdate = dba.findFilePropertyByFileName(file.getFileName(), false);
                fileToUpdate.setNotifyOwner(notifyOwner);
                dba.updateFileProperties(); 
            } else {
                throw new Exception("File does not exist or you are not the owner of the file:" + fileName);
            }
        } catch(Exception e) {
            throw new Exception("Could not update notifyOwner on file.", e);
        }
    }
    
    
    // This needs to be fixed
    public void createFileProperties(FileProperties fileProperties) {
        try {
            if(dba.findFilePropertyByFileName(fileProperties.getFileName(), true) == null) {
                
            }
            dba.createFileProperties(fileProperties);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
      
    @Override
    public FileProperties getFilePropertiesByFileName(String fileName) throws Exception {
        try {
            FileProperties file = dba.findFilePropertyByFileName(fileName, true);
            
            return file; 
        } catch (Exception e) {
            throw new Exception("Could not search for FileProperties.", e);
        }     
    }
    
    public void readFileProperties(String fileName, int clientToken) throws Exception {
        try {
            FileProperties file = getFilePropertiesByFileName(fileName);
            if(file == null) {
                clientManager.sendMessage(clientToken, "could not find file with name: " + fileName);
            } else {
                if(file.getNotifyOwner()) {
                    Client owner = clientManager.getClientByName(file.getOwner().getUsername());
                    clientManager.notifyRead(owner.getClientHash(), file);
                }
            }
        } catch (Exception e) {
            throw new Exception("Something went wrong when reading the file properties of file: " + fileName, e);
        }
        
    }
    
    @Override
    public List<? extends FilePropertiesDTO> listFileProperties(int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in to list available files!");
        }
        Client client = clientManager.getClientByHash(clientToken);
        List<FileProperties> filteredFiles = new ArrayList<>();
        List<FileProperties> allFiles = dba.findAllFileProperties();
        for(FileProperties fileProperties : allFiles) {
            if(fileProperties.getIsPublicReadable() || fileProperties.getIsPublicWriteable() || fileProperties.getOwner().getUsername().equals(client.getUsername())) {
                filteredFiles.add(fileProperties);
            }
        }
        return filteredFiles;
    }
    
    @Override
    public void downloadFile(String fileName, int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in to download files!");
        }
        Client client = clientManager.getClientByHash(clientToken);
        FileProperties file = getFilePropertiesByFileName(fileName);
        if(client != null && file != null) {
            if(client.getUsername().equals(file.getOwner().getUsername()) || file.getIsPublicReadable()) {
                FileTransferService transferService = new FileTransferService(file, client);
                transferService.sendFile();
                if(file.getNotifyOwner()) {
                    Client owner = clientManager.getClientByName(file.getOwner().getUsername());
                    clientManager.notifyRead(owner.getClientHash(), file);
                }
            } else {
                throw new Exception("You do not have enough access to download file: " + fileName);
            }
        } else {
            throw new Exception("Could not download file.");
        }
    }
    
    @Override
    public void updateFile(String fileName, long size, int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in to update files!");
        }
        Client client = clientManager.getClientByHash(clientToken);
        FileProperties file = getFilePropertiesByFileName(fileName);
        if(client != null && file != null) {
            if(client.getUsername().equals(file.getOwner().getUsername()) || file.getIsPublicWriteable()) {
                FileTransferService transferService = new FileTransferService(file, client);
                transferService.deleteFile();
                deleteFileProperties(fileName);
                fileToServer(fileName, size, clientToken);
                deleteFileProperties(fileName);
                file.setFileSize(size);
                createFileProperties(file);
            } else {
                throw new Exception("You do not have  enough access to update the file: " + fileName);
            }
        } else {
            throw new Exception("Could not update file: " + fileName);
        }
    }
    
    private boolean isLoggedIn(int clientToken) throws Exception{
        return clientToken != 0;
    }
    
    @Override
    public void deleteFile(String fileName, int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in to delete files!");
        }
        Client client = clientManager.getClientByHash(clientToken);
        FileProperties file = getFilePropertiesByFileName(fileName);
        if(client != null && file != null) {
            if(client.getUsername().equals(file.getOwner().getUsername()) || file.getIsPublicWriteable()) {
                FileTransferService transferService = new FileTransferService(file, client);
                transferService.deleteFile();
                deleteFileProperties(fileName);
            } else {
                throw new Exception("You do not have enough access to delete the file:" + fileName);
            }
        } else {
            throw new Exception("Could not delete file: " + fileName);
        }
    }
    
    @Override
    public void updateFileReadable(String fileName, boolean isReadable, int clientToken) throws Exception {
        FileProperties fileToUpdate = null;
        try {
            FileProperties file = getFilePropertiesByFileName(fileName);
            Client client = clientManager.getClientByHash(clientToken);
            if(file != null && client.getUsername().equals(file.getOwner().getUsername())) {
                fileToUpdate = dba.findFilePropertyByFileName(file.getFileName(), false);
                fileToUpdate.setPublicReadable(isReadable);
                dba.updateFileProperties();
            }
        } catch (Exception e) {
            throw new Exception("Could not update readability on FileProperties: " + fileName, e);
        }
    }
    
    @Override
    public void fileToServer(String fileName, long size, int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in upload files!");
        }
        Client client = clientManager.getClientByHash(clientToken);
        FileProperties file = getFilePropertiesByFileName(fileName);
        if(file != null) {
            throw new Exception("File: " + fileName + " already exists on the server!");
        } else {
            file = new FileProperties(fileName, size, client.getAccount());
            FileTransferService transferService = new FileTransferService(file, client);
            //CompletableFuture.runAsync(() -> transferService.receiveFile());
            transferService.receiveFile();
            dba.createFileProperties(file);            
        }
    }
    
    @Override
    public void updateFileWriteable(String fileName, boolean isWritable, int clientToken) throws Exception {
        if(!isLoggedIn(clientToken)) {
            throw new Exception("You are not logged in, please log in to update read access to files!");
        }
        FileProperties fileToUpdate = null;
        try {
            FileProperties file = getFilePropertiesByFileName(fileName);
            Client client = clientManager.getClientByHash(clientToken);
            if(file != null && client.getUsername().equals(file.getOwner().getUsername())) {
                fileToUpdate = dba.findFilePropertyByFileName(file.getFileName(), false);
                fileToUpdate.setPublicWriteable(isWritable);
                dba.updateFileProperties();
            } else {
                throw new Exception("File does not exist or you do not have enough rights to update the file.");
            }
        } catch (Exception e) {
            throw new Exception("Could not update writeability on FileProperties: " + fileName, e);
        }
    }
    
    public void deleteFileProperties(String fileName) throws Exception {
        try {
            dba.deleteFilePropertiesByFileName(fileName);
        } catch (Exception e) {
            throw new Exception("Could not delete FileProperties for: " + fileName, e);
        }
    }
    
    @Override
    public UserAccountDTO getUserAccount(String username) throws Exception {
        try {
            return dba.findUserAccountByName(username);
        } catch (Exception ex) {
            throw new Exception("Could not find userAccount: " + username, ex);
        }
    }

    @Override
    public int login(FileClient clientOut, String username, String password) throws Exception {
        UserAccount account = null;
        try {
            account = dba.findUserAccountByName(username);
            if(account == null) {
                account = new UserAccount(username, password);
                dba.createUserAccount(account);    
            }
            if (account.getPassword().equals(password)) {
                return clientManager.newClient(clientOut, account);
            } else {
                clientOut.handleMsg("Wrong password for username: " + username);
            }
        } catch (Exception e) {
            throw new Exception("Could not connect!", e);
        }
        return 0;
        
    }
    
    @Override
    public void unregister(int clientToken) throws Exception {
        Client client = clientManager.getClientByHash(clientToken);
        dba.deleteFilePropertiesByOwner(client.getUsername());
        dba.deleteUserAccount(client.getUsername());
        client.sendMessage("UserAccount: " + client.getUsername() + " has been unregistered, you are now being logged out.");
        clientManager.removeClient(clientToken);
    }

    @Override
    public int logout(int clientToken) throws RemoteException {
        clientManager.removeClient(clientToken);
        return 0;
    }
}
