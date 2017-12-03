package se.kth.id1212.client.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import se.kth.id1212.client.net.ClientFileReceiver;
import java.util.Scanner;
import se.kth.id1212.client.net.ClientFileSender;
import se.kth.id1212.common.CommonConstants;
import se.kth.id1212.common.FileClient;
import se.kth.id1212.common.FilePropertiesDTO;
import se.kth.id1212.common.FileServer;
import se.kth.id1212.common.integration.FileReader;
import se.kth.id1212.common.integration.FileWriter;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean receivingCmds = false;
    private final FileClient fileClientOutput;
    private FileServer server;
    private int myIdAtServer;
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private final FileReader reader = new FileReader();
    
    public NonBlockingInterpreter() throws RemoteException {
        fileClientOutput = new ServerInvocation();
    }
    
    public void start() {
        if(receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        while(receivingCmds) {
            try {
                CmdLine cmdLine =  new CmdLine(readNextLine());
                String fileName;
                switch(cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        myIdAtServer = server.logout(myIdAtServer);
                        outMgr.println("You are now disconnected and logged out.");
                        outMgr.println("Program will now shut down.");
                        break;
                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            outMgr.println(command.toString().toLowerCase());
                        }
                        break;
                    case UPLOAD:
                        fileName = cmdLine.getParameter(0);
                        reader.readFile(fileName);
                        server.fileToServer(fileName, reader.getFileSize(), myIdAtServer);
                        outMgr.println("Successfully uploaded file: " + fileName + " to the server.");
                        break;
                    case CONNECT:
                        lookupServer(cmdLine.getParameter(0));
                        outMgr.println("You are now connected to the server.");
                        outMgr.println("To login/register type: <login> <username> <password>");
                        outMgr.println("If no account exists with that name a new one will be registered.");
                        break;
                    case LOGIN:
                        myIdAtServer = server.login(fileClientOutput, 
                                cmdLine.getParameter(0), 
                                cmdLine.getParameter(1));
                        break;
                    case UNREGISTER:
                        server.unregister(myIdAtServer);
                        outMgr.println("You have now unregistered this user from the server.");
                    case DOWNLOAD:
                        fileName = cmdLine.getParameter(0);
                        server.downloadFile(fileName, myIdAtServer);
                        outMgr.println("Successfully downloaded file: " + fileName + " from the server.");
                        break;
                    case UPDATE:
                        fileName = cmdLine.getParameter(0);
                        reader.readFile(fileName);
                        server.updateFile(fileName, reader.getFileSize(), myIdAtServer);
                        outMgr.println(("Successfully updated file: " + fileName));
                        break;
                    case LIST:
                        List<? extends FilePropertiesDTO> files = server.listFileProperties(myIdAtServer);
                        files.forEach((file) -> {
                            outMgr.println(file.toString());
                        });
                        break;
                    case LOGOUT:
                        myIdAtServer = server.logout(myIdAtServer);
                        break;
                    case DELETE:
                        fileName = cmdLine.getParameter(0);
                        server.deleteFile(fileName, myIdAtServer);
                        outMgr.println("Deleted file: " + fileName + " from the server.");
                        break;
                    case SETWRITE:
                        fileName = cmdLine.getParameter(0);
                        boolean write = Boolean.valueOf(cmdLine.getParameter(1));
                        server.updateFileWriteable(fileName, write, myIdAtServer);
                        outMgr.println("Successfully updated write access to file: " + fileName);
                        break;
                    case SETREAD:
                        fileName = cmdLine.getParameter(0);
                        boolean read  = Boolean.valueOf(cmdLine.getParameter(1));
                        server.updateFileReadable(fileName, read, myIdAtServer);
                        outMgr.println("Successfully updated read access to file: " + fileName);
                        break;
                    case NOTIFY:
                        fileName = cmdLine.getParameter(0);
                        boolean notify = Boolean.valueOf(cmdLine.getParameter(1));
                        server.setNotifyFileOwner(fileName, notify, myIdAtServer);
                        outMgr.println("SUccessfully set notifications on the file: " + fileName);
                        break;
                    default: 
                } 
            } catch(MalformedURLException | NotBoundException | RemoteException e) {
                outMgr.println("Operation failed!");
            } catch (Exception ex) {
                outMgr.println(ex.getMessage());
            }
        }
    }
    
    private void lookupServer(String host) throws NotBoundException, MalformedURLException,
            RemoteException {
        server = (FileServer) Naming.lookup("//" + host + "/" + FileServer.SERVER_NAME_IN_REGISTER);
    }
    
    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }
    
    private class ServerInvocation extends UnicastRemoteObject implements FileClient {
        
        public ServerInvocation() throws RemoteException {
            
        }

        @Override
        public void handleMsg(String msg) {
            outMgr.println((String) msg);
            outMgr.print(PROMPT);
        }

        @Override
        public void startSend() throws RemoteException {
            try {
                ClientFileSender sender = new ClientFileSender(CommonConstants.FILE_TRANSFER_PORT, reader);
                sender.transfer();
                sender.close();
            } catch (IOException ex) {
                System.err.println("Error when trying to start sending on client side");
            }
            
        }

        @Override
        public void startReceive(FilePropertiesDTO fileP) throws RemoteException {
            try {
                FileWriter writer = new FileWriter(fileP.getFileName());
                ClientFileReceiver receiver = new ClientFileReceiver(CommonConstants.FILE_TRANSFER_PORT, writer, fileP.getFileSize());
                receiver.transfer();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}