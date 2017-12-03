package se.kth.id1212.server.constants;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Constants {
    public static final String P_FILE_NAME = "fileName";
    public static final String P_FILE_SIZE = "fileSize";
    public static final String P_FILE_READABLE = "publicReadable";
    public static final String P_FILE_WRITEABLE = "publicWriteable";
    public static final String NQ_FIND_ALL_FILE_PROPERTIES = "findAllFileProperties";
    public static final String NQ_FIND_FILE_PROPERTIES_BY_FILE_NAME = "findFilePropertiesByFileName";
    public static final String NQ_DELETE_FILE_PROPERTIES_BY_FILE_NAME = "deleteFilePropertiesByFileName";
    public static final String NQ_DELETE_FILE_PROPERTIES_BY_OWNER = "deleteFilePropertiesByOwner";
    
    public static final String P_USERNAME = "username";
    public static final String NQ_FIND_USER_ACCOUNT_BY_NAME = "findUserAccountByName";
    public static final String NQ_DELETE_USER_ACCOUNT_BY_NAME = "deleteUserAccountByName";
    
    public static final char READ_FILE = 'R';
    public static final char WRITTEN_FILE = 'W';
    public static final char DELETED_FILE = 'D';

    
}
