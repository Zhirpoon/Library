package se.kth.id1212.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import se.kth.id1212.common.integration.FileWriter;
/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public final class ClientFileReceiver {
    private final InetSocketAddress hostAddress;
    private SocketChannel client;
    private final FileWriter fileWriter;
    private final long filesize;

    public ClientFileReceiver(final int port, final FileWriter fileWriter, final long filesize ) throws IOException {
        this.fileWriter = fileWriter;
        this.hostAddress = new InetSocketAddress(port);
        this.client = SocketChannel.open(this.hostAddress);
        this.filesize = filesize;
    }

    public void transfer() throws IOException {
        assert !Objects.isNull(client);
        fileWriter.transfer(client, this.filesize);
        fileWriter.close();
        client.close();
    }
}