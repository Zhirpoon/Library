package se.kth.id1212.hw4.controller;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import se.kth.id1212.hw4.integration.DBAO;
import se.kth.id1212.hw4.model.Calculator;
import se.kth.id1212.hw4.model.Currency;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class ConversionFacade {
    //BY CDI INJECTION 
    @EJB DBAO dbao;
    @EJB Calculator calc;
    
    public List<Currency> getAllCurrencies() {
        return dbao.findAllCurrencies();
    }
    
    public Currency getCurrency(String currName) {
        return dbao.findCurrencyByName(currName);
    }
    
    public double convert(String from, String to, double amount) {
        Currency currFrom = getCurrency(from);
        Currency currTo = getCurrency(to);
        return calc.calculate(currFrom, currTo, amount);
    }
    
}
