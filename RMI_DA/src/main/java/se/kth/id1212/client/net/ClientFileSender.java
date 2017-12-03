package se.kth.id1212.client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import se.kth.id1212.common.CommonConstants;
import se.kth.id1212.common.integration.FileReader;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public final class ClientFileSender {

    private final InetSocketAddress hostAddress;
    private final SocketChannel clientSocket;
    private final FileReader reader;

    public ClientFileSender(final int port, FileReader reader) throws IOException {
        this.hostAddress = new InetSocketAddress(port);
        this.clientSocket = SocketChannel.open(this.hostAddress);
        this.reader = reader;
    }

    public void transfer() throws IOException {
        assert !Objects.isNull(reader.getChannel());
        long position = 00; // For me it misses the first letter on 01
        while (position < reader.getFileSize()) {
            position += reader.getChannel().transferTo(position, CommonConstants.MAXIMUM_PART_SIZE, this.clientSocket);
        }
    }

    public void close() throws IOException {
        reader.close();
        this.clientSocket.close();
    }
}

