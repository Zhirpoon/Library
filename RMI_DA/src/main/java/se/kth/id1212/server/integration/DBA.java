package se.kth.id1212.server.integration;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import se.kth.id1212.server.constants.Constants;
import se.kth.id1212.server.model.FileProperties;
import se.kth.id1212.server.model.UserAccount;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
public class DBA {
    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
    
    public DBA() {
        emFactory = Persistence.createEntityManagerFactory("fileServerPersistenceUnit");
    }
    
    public void createUserAccount(UserAccount user) throws Exception {
        try {
            EntityManager em = beginTransaction();
            em.persist(user);
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteFilePropertiesByOwner(String owner) {
        EntityManager em = beginTransaction();
        try {
            em.createNamedQuery(Constants.NQ_DELETE_USER_ACCOUNT_BY_NAME, UserAccount.class).
                    setParameter(Constants.P_USERNAME, owner).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteUserAccount(String username) {
        EntityManager em = beginTransaction();
        try {
            em.createNamedQuery(Constants.NQ_DELETE_USER_ACCOUNT_BY_NAME, UserAccount.class).
                    setParameter(Constants.P_USERNAME, username).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    private EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return em;
    }
    
    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }
    
    public UserAccount findUserAccountByName(String username) {
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery(Constants.NQ_FIND_USER_ACCOUNT_BY_NAME, UserAccount.class).
                        setParameter(Constants.P_USERNAME, username).getSingleResult();
            } catch (NoResultException NoSuchUserAccount) {
                return null;
            }
        } finally {
            commitTransaction();
        }
    }
    
    public void createFileProperties(FileProperties file) {
        try {
            EntityManager em = beginTransaction();
            em.persist(file);
        } finally {
            commitTransaction();
        }
    }
    
    public void updateFileProperties() {
        commitTransaction();
    }
    
    public FileProperties findFilePropertyByFileName(String fileName, boolean endAfterSearch) {
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery(Constants.NQ_FIND_FILE_PROPERTIES_BY_FILE_NAME, FileProperties.class).
                        setParameter(Constants.P_FILE_NAME, fileName).getSingleResult();
            } catch (NoResultException NoSuchFileProperties) {
                return null;
            }
        } finally {
            if(endAfterSearch) {
                commitTransaction();
            }
        }
    }
    
    public List<FileProperties> findAllFileProperties() {
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery(Constants.NQ_FIND_ALL_FILE_PROPERTIES, FileProperties.class).
                        getResultList();
            } catch (NoResultException NoSuchFileProperties) {
                return new ArrayList<>();
            }
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteFilePropertiesByFileName(String fileName) {
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery(Constants.NQ_DELETE_FILE_PROPERTIES_BY_FILE_NAME, FileProperties.class).
                    setParameter(Constants.P_FILE_NAME, fileName).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
}
