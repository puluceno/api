/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "DiscountCoupon", schema = "RedeFood")
public class DiscountCoupon implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idDiscountCoupon", nullable = false)
    private Integer idDiscountCoupon;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "name", nullable = false, length = 60)
    private String name;
    @Max(value = 999)
    @Min(value = 0)
    @Basic(optional = false)
    @NotNull
    @Column(name = "value", nullable = false, precision = 5, scale = 2)
    private BigDecimal value;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "description", nullable = false, length = 300)
    private String description;
    @ManyToMany(mappedBy = "discountCouponList", fetch = FetchType.LAZY)
    private List<User> userList;
    @OneToMany(mappedBy = "discountCoupon", fetch = FetchType.LAZY)
    private List<Orders> ordersList;
    
    public DiscountCoupon() {
    }
    
    public DiscountCoupon(Integer idDiscountCoupon) {
	this.idDiscountCoupon = idDiscountCoupon;
    }
    
    public DiscountCoupon(Integer idDiscountCoupon, String name, BigDecimal value, String description) {
	this.idDiscountCoupon = idDiscountCoupon;
	this.name = name;
	this.value = value;
	this.description = description;
    }
    
    public Integer getIdDiscountCoupon() {
	return idDiscountCoupon;
    }
    
    public void setIdDiscountCoupon(Integer idDiscountCoupon) {
	this.idDiscountCoupon = idDiscountCoupon;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public BigDecimal getValue() {
	return value;
    }
    
    public void setValue(BigDecimal value) {
	this.value = value;
    }
    
    public String getDescription() {
	return description;
    }
    
    public void setDescription(String description) {
	this.description = description;
    }
    
    @XmlTransient
    public List<User> getUserList() {
	return userList;
    }
    
    public void setUserList(List<User> userList) {
	this.userList = userList;
    }
    
    @XmlTransient
    public List<Orders> getOrdersList() {
	return ordersList;
    }
    
    public void setOrdersList(List<Orders> ordersList) {
	this.ordersList = ordersList;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (idDiscountCoupon == null ? 0 : idDiscountCoupon.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (ordersList == null ? 0 : ordersList.hashCode());
	result = prime * result + (userList == null ? 0 : userList.hashCode());
	result = prime * result + (value == null ? 0 : value.hashCode());
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
	DiscountCoupon other = (DiscountCoupon) obj;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (idDiscountCoupon == null) {
	    if (other.idDiscountCoupon != null)
		return false;
	} else if (!idDiscountCoupon.equals(other.idDiscountCoupon))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (ordersList == null) {
	    if (other.ordersList != null)
		return false;
	} else if (!ordersList.equals(other.ordersList))
	    return false;
	if (userList == null) {
	    if (other.userList != null)
		return false;
	} else if (!userList.equals(other.userList))
	    return false;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "DiscountCoupon [idDiscountCoupon=" + idDiscountCoupon + ", name=" + name + ", value=" + value
		+ ", description=" + description + ", userList=" + userList + ", ordersList=" + ordersList + "]";
    }
    
}
