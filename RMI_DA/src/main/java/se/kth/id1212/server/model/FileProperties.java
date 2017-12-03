package se.kth.id1212.server.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import se.kth.id1212.common.FileClient;
import se.kth.id1212.common.FilePropertiesDTO;
import se.kth.id1212.server.constants.Constants;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */

@NamedQueries({
    @NamedQuery(
            name = Constants.NQ_DELETE_FILE_PROPERTIES_BY_FILE_NAME,
            query = "DELETE FROM FileProperties files WHERE files.fileName LIKE :" + Constants.P_FILE_NAME
    )
    ,
    
    @NamedQuery(
            name = Constants.NQ_FIND_ALL_FILE_PROPERTIES,
            query = "SELECT files FROM FileProperties files",
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
    
    @NamedQuery(
            name = Constants.NQ_FIND_FILE_PROPERTIES_BY_FILE_NAME,
            query = "SELECT file FROM FileProperties file WHERE file.fileName LIKE :" + Constants.P_FILE_NAME,
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
    
    @NamedQuery(
            name = Constants.NQ_DELETE_FILE_PROPERTIES_BY_OWNER,
            query = "DELETE FROM FileProperties file WHERE file.owner.username LIKE :" + Constants.P_USERNAME
    )
    
})
@Entity(name= "FileProperties")
public class FileProperties implements FilePropertiesDTO {
    // Good practice to include, but not necessary in this homework
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name= "fileName", nullable= false)
    private String fileName;
    
    @Column(name= "fileSize", nullable= false)
    private long fileSize;
    
    @ManyToOne
    @JoinColumn(name= "owner", nullable= false)
    private UserAccount owner;
    
    @Column(name= "publicReadable", nullable= false)
    private boolean publicReadable;
    
    @Column(name= "publicWriteable", nullable= false)
    private boolean publicWriteable;
    
    @Column(name = "notifyOwner", nullable= false)
    private boolean notifyOwner;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;
    
    public FileProperties() {
        this(null, 0, null);
    }
    
    public FileProperties(String fileName, long fileSize, UserAccount owner) {
        this(fileName, fileSize, owner, false, false);
    }
    
    public FileProperties(String fileName, long fileSize, UserAccount owner, boolean publicReadable, boolean publicWriteable) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.owner = owner;
        this.publicReadable = publicReadable;
        this.publicWriteable = publicWriteable;
        this.notifyOwner = false;
    }
    
    public boolean getNotifyOwner() {
        return notifyOwner;
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public UserAccount getOwner() {
        return owner;
    }

    @Override
    public boolean getIsPublicReadable() {
        return publicReadable;
    }

    @Override
    public boolean getIsPublicWriteable() {
        return publicWriteable;
    }
    
    public void setNotifyOwner(boolean notifyOwner) {
        this.notifyOwner = notifyOwner;
    }
    
    public void setPublicWriteable(boolean isWriteable) {
        publicWriteable = isWriteable;
    }
    
    public void setPublicReadable(boolean isReadable) {
        publicReadable = isReadable;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder fileString = new StringBuilder();
        fileString.append("Filename:");
        fileString.append(fileName);
        fileString.append("\nFile size:");
        fileString.append(fileSize);
        fileString.append("\nOwner:");
        fileString.append(owner.getUsername());
        if(publicReadable == false && publicWriteable == false) {
            fileString.append("\nFile is private.");
        } else {
            fileString.append("\nFile is public.");
            fileString.append("\nFile is readable:");
            fileString.append(publicReadable);
            fileString.append("\nFile is writeable:");
            fileString.append(publicWriteable);
        }
        return fileString.toString();
    }
    
}
