package se.kth.id1212.common;

import java.io.Serializable;
import se.kth.id1212.server.model.UserAccount;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public interface FilePropertiesDTO extends Serializable {
    public String getFileName();
    public long getFileSize();
    public UserAccount getOwner();
    public boolean getIsPublicReadable();
    public boolean getIsPublicWriteable();
}
