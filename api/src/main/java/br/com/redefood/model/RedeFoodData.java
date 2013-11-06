package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "RedeFoodData", schema = "RedeFood", uniqueConstraints = { @UniqueConstraint(columnNames = { "cnpj" }) })
public class RedeFoodData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "cnpj", nullable = false, length = 19)
    private String cnpj;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Column(name = "street", length = 100)
    private String street;
    @Column(name = "number", length = 45)
    private String number;
    @Column(name = "zipcode", length = 9)
    private String zipcode;
    @Column(name = "neighborhood", length = 45)
    private String neighborhood;
    @Column(name = "city", length = 45)
    private String city;
    @Column(name = "state", length = 2)
    private String state;
    @Column(name = "bankName", length = 60)
    private String bankName;
    @Column(name = "accountNumber", length = 12)
    private String accountNumber;
    @Column(name = "documentNumber", length = 45)
    private String documentNumber;
    @Column(name = "carteira", length = 12)
    private String carteira;
    @Column(name = "convenioNumber", length = 45)
    private String convenioNumber;
    @Column(name = "ourNumber", length = 45)
    private String ourNumber;
    @Column(name = "bankNumber", length = 45)
    private String bankNumber;
    @Column(name = "bankAgency", length = 45)
    private String bankAgency;
    @Column(name = "bankAgencyDigit", length = 45)
    private String bankAgencyDigit;
    @Column(name = "slogan", length = 100)
    private String slogan;
    
    public RedeFoodData() {
    }
    
    public RedeFoodData(String cnpj) {
	this.cnpj = cnpj;
    }
    
    public RedeFoodData(String cnpj, String name) {
	this.cnpj = cnpj;
	this.name = name;
    }
    
    public String getCnpj() {
	return cnpj;
    }
    
    public void setCnpj(String cnpj) {
	this.cnpj = cnpj;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public String getStreet() {
	return street;
    }
    
    public void setStreet(String street) {
	this.street = street;
    }
    
    public String getNumber() {
	return number;
    }
    
    public void setNumber(String number) {
	this.number = number;
    }
    
    public String getZipcode() {
	return zipcode;
    }
    
    public void setZipcode(String zipcode) {
	this.zipcode = zipcode;
    }
    
    public String getNeighborhood() {
	return neighborhood;
    }
    
    public void setNeighborhood(String neighborhood) {
	this.neighborhood = neighborhood;
    }
    
    public String getCity() {
	return city;
    }
    
    public void setCity(String city) {
	this.city = city;
    }
    
    public String getState() {
	return state;
    }
    
    public void setState(String state) {
	this.state = state;
    }
    
    public String getBankName() {
	return bankName;
    }
    
    public void setBankName(String bankName) {
	this.bankName = bankName;
    }
    
    public String getAccountNumber() {
	return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
	this.accountNumber = accountNumber;
    }
    
    public String getDocumentNumber() {
	return documentNumber;
    }
    
    public void setDocumentNumber(String documentNumber) {
	this.documentNumber = documentNumber;
    }
    
    public String getCarteira() {
	return carteira;
    }
    
    public void setCarteira(String carteira) {
	this.carteira = carteira;
    }
    
    public String getConvenioNumber() {
	return convenioNumber;
    }
    
    public void setConvenioNumber(String convenioNumber) {
	this.convenioNumber = convenioNumber;
    }
    
    public String getOurNumber() {
	return ourNumber;
    }
    
    public void setOurNumber(String ourNumber) {
	this.ourNumber = ourNumber;
    }
    
    public String getBankNumber() {
	return bankNumber;
    }
    
    public void setBankNumber(String bankNumber) {
	this.bankNumber = bankNumber;
    }
    
    public String getBankAgency() {
	return bankAgency;
    }
    
    public void setBankAgency(String bankAgency) {
	this.bankAgency = bankAgency;
    }
    
    public String getBankAgencyDigit() {
	return bankAgencyDigit;
    }
    
    public void setBankAgencyDigit(String bankAgencyDigit) {
	this.bankAgencyDigit = bankAgencyDigit;
    }
    
    public String getSlogan() {
	return slogan;
    }

    public void setSlogan(String slogan) {
	this.slogan = slogan;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (accountNumber == null ? 0 : accountNumber.hashCode());
	result = prime * result + (bankAgency == null ? 0 : bankAgency.hashCode());
	result = prime * result + (bankAgencyDigit == null ? 0 : bankAgencyDigit.hashCode());
	result = prime * result + (bankName == null ? 0 : bankName.hashCode());
	result = prime * result + (bankNumber == null ? 0 : bankNumber.hashCode());
	result = prime * result + (carteira == null ? 0 : carteira.hashCode());
	result = prime * result + (city == null ? 0 : city.hashCode());
	result = prime * result + (cnpj == null ? 0 : cnpj.hashCode());
	result = prime * result + (convenioNumber == null ? 0 : convenioNumber.hashCode());
	result = prime * result + (documentNumber == null ? 0 : documentNumber.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (neighborhood == null ? 0 : neighborhood.hashCode());
	result = prime * result + (number == null ? 0 : number.hashCode());
	result = prime * result + (ourNumber == null ? 0 : ourNumber.hashCode());
	result = prime * result + (state == null ? 0 : state.hashCode());
	result = prime * result + (street == null ? 0 : street.hashCode());
	result = prime * result + (zipcode == null ? 0 : zipcode.hashCode());
	return result;
    }
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	RedeFoodData other = (RedeFoodData) obj;
	if (accountNumber == null) {
	    if (other.accountNumber != null)
		return false;
	} else if (!accountNumber.equals(other.accountNumber))
	    return false;
	if (bankAgency == null) {
	    if (other.bankAgency != null)
		return false;
	} else if (!bankAgency.equals(other.bankAgency))
	    return false;
	if (bankAgencyDigit == null) {
	    if (other.bankAgencyDigit != null)
		return false;
	} else if (!bankAgencyDigit.equals(other.bankAgencyDigit))
	    return false;
	if (bankName == null) {
	    if (other.bankName != null)
		return false;
	} else if (!bankName.equals(other.bankName))
	    return false;
	if (bankNumber == null) {
	    if (other.bankNumber != null)
		return false;
	} else if (!bankNumber.equals(other.bankNumber))
	    return false;
	if (carteira == null) {
	    if (other.carteira != null)
		return false;
	} else if (!carteira.equals(other.carteira))
	    return false;
	if (city == null) {
	    if (other.city != null)
		return false;
	} else if (!city.equals(other.city))
	    return false;
	if (cnpj == null) {
	    if (other.cnpj != null)
		return false;
	} else if (!cnpj.equals(other.cnpj))
	    return false;
	if (convenioNumber == null) {
	    if (other.convenioNumber != null)
		return false;
	} else if (!convenioNumber.equals(other.convenioNumber))
	    return false;
	if (documentNumber == null) {
	    if (other.documentNumber != null)
		return false;
	} else if (!documentNumber.equals(other.documentNumber))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (neighborhood == null) {
	    if (other.neighborhood != null)
		return false;
	} else if (!neighborhood.equals(other.neighborhood))
	    return false;
	if (number == null) {
	    if (other.number != null)
		return false;
	} else if (!number.equals(other.number))
	    return false;
	if (ourNumber == null) {
	    if (other.ourNumber != null)
		return false;
	} else if (!ourNumber.equals(other.ourNumber))
	    return false;
	if (state == null) {
	    if (other.state != null)
		return false;
	} else if (!state.equals(other.state))
	    return false;
	if (street == null) {
	    if (other.street != null)
		return false;
	} else if (!street.equals(other.street))
	    return false;
	if (zipcode == null) {
	    if (other.zipcode != null)
		return false;
	} else if (!zipcode.equals(other.zipcode))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "RedeFoodData [cnpj=" + cnpj + ", name=" + name + ", street=" + street + ", number=" + number
		+ ", zipcode=" + zipcode + ", neighborhood=" + neighborhood + ", city=" + city + ", state=" + state
		+ ", bankName=" + bankName + ", accountNumber=" + accountNumber + ", documentNumber=" + documentNumber
		+ ", carteira=" + carteira + ", convenioNumber=" + convenioNumber + ", ourNumber=" + ourNumber
		+ ", bankNumber=" + bankNumber + ", bankAgency=" + bankAgency + ", bankAgencyDigit=" + bankAgencyDigit
		+ "]";
    }
    
}
