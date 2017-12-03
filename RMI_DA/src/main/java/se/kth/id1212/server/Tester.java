package se.kth.id1212.server;

import se.kth.id1212.server.controller.Controller;
import se.kth.id1212.server.model.FileProperties;
import se.kth.id1212.server.model.UserAccount;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class Tester {
    private static Controller contr;
    public static void main(String args[]) {
        //contr = new Controller();
        registerUser();
        //testFileAndOwner();
    }
    
    public static void testFileAndOwner() {
        UserAccount owner = new UserAccount("Zhirpoon", "123batman");
        FileProperties file = new FileProperties("PrivateFile", 25, owner);
        FileProperties publicFile = new FileProperties("PublicFile", 25, owner, false, false);
        System.out.println(file);
        System.out.println(owner);
        System.out.println(publicFile);
    }
    
    public static void registerUser() {
        UserAccount owner = new UserAccount("Johan", "123batman");
        FileProperties fileProperties = new FileProperties("PrivateFile", 25, owner);
        
        //contr.createUserAccount(owner.getUsername(), owner.getPassword());
        contr.createFileProperties(fileProperties);
        //contr.updateFileSize("PrivateFile", 234567);
    }
    
    
}
