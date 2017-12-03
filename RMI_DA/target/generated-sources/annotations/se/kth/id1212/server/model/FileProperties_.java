package se.kth.id1212.server.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import se.kth.id1212.server.model.UserAccount;

@Generated(value="EclipseLink-2.7.0.v20170811-rNA", date="2017-12-02T18:23:55")
@StaticMetamodel(FileProperties.class)
public class FileProperties_ { 

    public static volatile SingularAttribute<FileProperties, UserAccount> owner;
    public static volatile SingularAttribute<FileProperties, String> fileName;
    public static volatile SingularAttribute<FileProperties, Long> fileSize;
    public static volatile SingularAttribute<FileProperties, Integer> versionNum;
    public static volatile SingularAttribute<FileProperties, Boolean> notifyOwner;
    public static volatile SingularAttribute<FileProperties, Boolean> publicReadable;
    public static volatile SingularAttribute<FileProperties, Boolean> publicWriteable;

}