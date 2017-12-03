package se.kth.id1212.server.net;


import se.kth.id1212.common.integration.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.kth.id1212.server.model.Client;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
final class ServerFileReceiver {
    private final int port;
    private final FileWriter fileWriter;
    private final long size;
    private final Client client;

    ServerFileReceiver(final int port, final FileWriter fileWriter, final long size, Client client) {
        this.port = port;
        this.fileWriter = fileWriter;
        this.size = size;
        this.client = client;
    }

    void receive() throws IOException {
        SocketChannel channel = null;

        try (final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            init(serverSocketChannel);
            CompletableFuture.runAsync(() -> {
                try {
                    client.startSend();
                } catch (RemoteException ex) {
                    System.err.println("Something went wrong on the server-side sending the file");
                }
            });
            channel = serverSocketChannel.accept();
            doTransfer(channel);
        } finally {    
            channel.close();
            this.fileWriter.close();
        }
    }

    private void doTransfer(final SocketChannel channel) throws IOException {
        assert !Objects.isNull(channel);

        this.fileWriter.transfer(channel, this.size);
    }

    private void init(final ServerSocketChannel serverSocketChannel) throws IOException {
        assert !Objects.isNull(serverSocketChannel);

        serverSocketChannel.bind(new InetSocketAddress(this.port));
    }
}

