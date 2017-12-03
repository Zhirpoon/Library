package se.kth.id1212.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import se.kth.id1212.common.UserAccountDTO;
import se.kth.id1212.server.constants.Constants;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */

@NamedQueries({    
    @NamedQuery(
            name = Constants.NQ_FIND_USER_ACCOUNT_BY_NAME,
            query = "SELECT account FROM UserAccount account WHERE account.username LIKE :" + Constants.P_USERNAME,
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
    
    @NamedQuery(
            name = Constants.NQ_DELETE_USER_ACCOUNT_BY_NAME,
            query = "DELETE FROM UserAccount account WHERE account.username LIKE :" + Constants.P_USERNAME
    )
    
})
@Entity(name= "UserAccount")
public class UserAccount implements UserAccountDTO {
     
    // Good practice to include, but not necessary in this homework
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name= "username", nullable= false)
    private String username;
    
    @Column(name= "password", nullable= false)
    private String password; 
    
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public UserAccount() {
        this(null, null);
    }

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    @Override
    public String toString() {
        return "User:username = " + getUsername();
    }
}
