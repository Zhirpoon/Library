package se.kth.id1212.hw4.integration;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import se.kth.id1212.hw4.model.Currency;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class DBAO {
    @PersistenceContext(unitName = "ConverterPU")
    private EntityManager em;
    
    public Currency findCurrencyByName(String currName) {
        Currency curr = em.find(Currency.class, currName);
        if(curr == null) {
            throw new EntityNotFoundException("No currency with the name :" + currName + " exists.");
        }
        return curr;
    }
     
    public List<Currency> findAllCurrencies() {
        return em.createNamedQuery("Currency.findAll", Currency.class).
                getResultList();      
    }

}