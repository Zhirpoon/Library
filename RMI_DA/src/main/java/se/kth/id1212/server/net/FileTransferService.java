package se.kth.id1212.server.net;

import java.io.File;
import java.io.IOException;
import se.kth.id1212.common.CommonConstants;
import se.kth.id1212.common.integration.FileReader;
import se.kth.id1212.common.integration.FileWriter;
import se.kth.id1212.server.model.Client;
import se.kth.id1212.server.model.FileProperties;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class FileTransferService {
    private final String DIRECTORY = "ServerFiles/";
    private final FileProperties fileProperties;
    private final Client client;
    
    public FileTransferService(FileProperties file, Client client) {
        this.fileProperties = file;
        this.client = client;
    }
    
    
    public void receiveFile() {
        try {
            FileWriter writer = new FileWriter(makePath());
            ServerFileReceiver receiver = new ServerFileReceiver(CommonConstants.FILE_TRANSFER_PORT, writer, fileProperties.getFileSize(), client);
            receiver.receive();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public String makePath() {
        return DIRECTORY + fileProperties.getFileName();
    }
    
    public void deleteFile() {
        File file = new File(makePath());
        file.delete();
    }
    
    public void sendFile() throws IOException {
        FileReader reader = new FileReader();
        reader.readFile(makePath());
        ServerFileSender sender = new ServerFileSender(CommonConstants.FILE_TRANSFER_PORT, reader, client, fileProperties);
        sender.send();
    }
}
