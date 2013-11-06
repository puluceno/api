package br.com.redefood.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "PaymentMethod", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = "PaymentMethod.findAll", query = "SELECT p FROM PaymentMethod p ORDER BY p.name ASC") })
public class PaymentMethod implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL = "PaymentMethod.findAll";
    
    public static final String MONEY = "Dinheiro";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPaymentMethod", nullable = false)
    private Short idPaymentMethod;
    @Column(name = "name", length = 45)
    private String name;
    @Column(name = "description", length = 45)
    private String description;
    @ManyToMany(mappedBy = "paymentMethodList", fetch = FetchType.LAZY)
    private List<Subsidiary> subsidiaryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paymentMethod", fetch = FetchType.LAZY)
    private List<OrderPaymentMethod> orderPaymentMethod;
    
    public PaymentMethod() {
    }
    
    public PaymentMethod(Short idPaymentMethod) {
	this.idPaymentMethod = idPaymentMethod;
    }
    
    public PaymentMethod(Integer idPaymentMethod) {
	this.idPaymentMethod = idPaymentMethod.shortValue();
    }
    
    public Short getId() {
	return idPaymentMethod;
    }
    
    public void setId(Short idPaymentMethod) {
	this.idPaymentMethod = idPaymentMethod;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public String getDescription() {
	return description;
    }
    
    public void setDescription(String description) {
	this.description = description;
    }
    
    @XmlTransient
    @JsonIgnore
    public List<Subsidiary> getSubsidiaryList() {
	return subsidiaryList;
    }
    
    @JsonIgnore
    public void setSubsidiaryList(List<Subsidiary> subsidiaryList) {
	this.subsidiaryList = subsidiaryList;
    }
    
    public List<OrderPaymentMethod> getOrderPaymentMethod() {
	return orderPaymentMethod;
    }
    
    public void setOrderPaymentMethod(List<OrderPaymentMethod> orderPaymentMethod) {
	this.orderPaymentMethod = orderPaymentMethod;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (idPaymentMethod == null ? 0 : idPaymentMethod.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
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
	PaymentMethod other = (PaymentMethod) obj;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (idPaymentMethod == null) {
	    if (other.idPaymentMethod != null)
		return false;
	} else if (!idPaymentMethod.equals(other.idPaymentMethod))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "PaymentMethod [idPaymentMethod=" + idPaymentMethod + ", name=" + name + ", description=" + description
		+ "]";
    }
    
}
