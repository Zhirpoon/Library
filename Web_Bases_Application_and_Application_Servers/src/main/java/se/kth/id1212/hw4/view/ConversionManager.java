package se.kth.id1212.hw4.view;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import se.kth.id1212.hw4.controller.ConversionFacade;
import se.kth.id1212.hw4.model.Currency;


/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
@Named("conversionManager")
@RequestScoped
public class ConversionManager implements Serializable {
    @EJB
    private ConversionFacade conversionFacade;
    private Exception transactionFailure;
    @Inject
    private Conversation conversation;
    private String fromCurrName;
    private String toCurrName;
    private double amountToConvert;
    private double convertedAmount;

    public Double getConvertedAmount() {
        return (double) Math.round(this.convertedAmount * 100.0) / 100.0;
    }
    
    public void convert() {
        try {
            this.convertedAmount = conversionFacade.convert(fromCurrName, toCurrName, amountToConvert);
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    public List<Currency> getCurrList() {
        List<Currency> allCurrencies = null;
        try {
            allCurrencies = conversionFacade.getAllCurrencies();
        } catch (Exception e) {
            handleException(e);
        }
        return allCurrencies;
    }

    public void setToCurrName(String toCurrName) {
        this.toCurrName = toCurrName;
    }
    
    public String getToCurrName() {
        return toCurrName;
    }

    public void setFromCurrName(String fromCurrName) {
        this.fromCurrName = fromCurrName;
    }
    
    public String getFromCurrName() {
        return fromCurrName;
    }
    
    public void setAmountToConvert(Double amount) {
        this.amountToConvert = amount;
    }
    
    public Double getAmountToConvert() {
        return amountToConvert;
    }
    
    private void startConversation() {
        if(conversation.isTransient()) {
            conversation.begin();
        }
    }
    
    private void stopConversation() {
        if(!conversation.isTransient()) {
            conversation.end();
        }
    }
    
    private void handleException(Exception e) {
        stopConversation();
        e.printStackTrace(System.err);
        transactionFailure = e;
    }
    
    public boolean getFailure() {
        return transactionFailure != null;
    }
    
    public Exception getException() {
        return transactionFailure;
    }
}
