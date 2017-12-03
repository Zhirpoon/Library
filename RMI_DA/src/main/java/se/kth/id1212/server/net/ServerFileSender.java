package se.kth.id1212.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.common.FilePropertiesDTO;
import se.kth.id1212.common.integration.FileReader;
import se.kth.id1212.server.model.Client;
import se.kth.id1212.server.model.FileProperties;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class ServerFileSender {
    private final FileReader reader;
    private final int port;
    private final Client client;
    private final FilePropertiesDTO fileP;

    public ServerFileSender(int port, FileReader reader, Client client, FileProperties fileP) throws IOException {
        this.reader = reader;
        this.port = port;
        this.client = client;
        this.fileP = (FilePropertiesDTO) fileP;
    }

    public void send() throws IOException {
        SocketChannel channel = null;

        try (final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            init(serverSocketChannel);
            CompletableFuture.runAsync(() -> {
                try {
                    client.startReceive(fileP);
                } catch (RemoteException ex) {
                    System.err.println(ex.getMessage());
                }
            });
            channel = serverSocketChannel.accept();
            doTransfer(channel);
        } finally {
            channel.close();
            this.reader.close();
        }
    }

    private void doTransfer(SocketChannel channel) throws IOException {
        assert !Objects.isNull(reader.getChannel());
        long position = 0l;
        while (position < reader.getFileSize()) {
            position += reader.getChannel().transferTo(position, 8192, channel);
        }
    }


    private void init(final ServerSocketChannel serverSocketChannel) throws IOException {
        assert !Objects.isNull(serverSocketChannel);

        serverSocketChannel.bind(new InetSocketAddress(this.port));
    }
}
