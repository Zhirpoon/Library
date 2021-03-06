package se.kth.id1212.common.integration;

import com.mysql.cj.core.util.StringUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public final class FileWriter {
    private final FileChannel channel;

    public FileWriter(final String path) throws IOException {
        if (StringUtils.isEmptyOrWhitespaceOnly(path)) {
            throw new IllegalArgumentException("path required");
        }

        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    public void transfer(final SocketChannel channel, final long bytes) throws IOException {
        assert !Objects.isNull(channel);

        long position = 0l;
        while (position < bytes) {
            position += this.channel.transferFrom(channel, position, 8192);
        }
    }
    
    public int write(final ByteBuffer buffer, long position) throws IOException {
        assert !Objects.isNull(buffer);

        int bytesWritten = 0;        
        while(buffer.hasRemaining()) {
            bytesWritten += this.channel.write(buffer, position + bytesWritten);            
        }
        
        return bytesWritten;
    }

    public void close() throws IOException {
        this.channel.close();
    }
}

