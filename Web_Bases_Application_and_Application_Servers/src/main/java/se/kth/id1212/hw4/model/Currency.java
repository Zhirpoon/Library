package se.kth.id1212.hw4.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Johan Rosengren <jrosengr@kth.se>
 */
@Entity
@Table(name = "CURRENCY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Currency.findAll", query = "SELECT c FROM Currency c")
    , @NamedQuery(name = "Currency.findByCurrencyname", query = "SELECT c FROM Currency c WHERE c.currencyname = :currencyname")
    , @NamedQuery(name = "Currency.findByConversionrate", query = "SELECT c FROM Currency c WHERE c.conversionrate = :conversionrate")})
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "CURRENCYNAME")
    private String currencyname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CONVERSIONRATE")
    private double conversionrate;

    public Currency() {
    }

    public Currency(String currencyname, double conversionrate) {
        this.currencyname = currencyname;
        this.conversionrate = conversionrate;
    }

    public String getCurrencyname() {
        return currencyname;
    }

    public double getConversionrate() {
        return conversionrate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (currencyname != null ? currencyname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency) object;
        if ((this.currencyname == null && other.currencyname != null) || (this.currencyname != null && !this.currencyname.equals(other.currencyname))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Currency[ currencyname=" + currencyname + " ]";
    }
    
}
