package se.kth.id1212.hw4.model;

import javax.ejb.Stateless;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
@Stateless
public class Calculator {
    
    public double calculate(Currency from, Currency to, double amount) {
        return (from.getConversionrate()/to.getConversionrate())*amount;
    }
}
