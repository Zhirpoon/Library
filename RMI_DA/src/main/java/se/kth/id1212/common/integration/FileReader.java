package se.kth.id1212.common.integration;


import com.mysql.cj.core.util.StringUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public final class FileReader {

    private FileChannel channel;
    private long fileSize;

    synchronized public void readFile(final String path) throws IOException {
        if(StringUtils.isEmptyOrWhitespaceOnly(path)) {
            throw new IllegalArgumentException("Path required!");
        }
        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
        this.fileSize = this.channel.size();
    }

    public FileChannel getChannel() {
        return channel;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void close() throws IOException {
        this.channel.close();
    }
}

