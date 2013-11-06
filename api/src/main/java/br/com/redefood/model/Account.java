/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Account", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = Account.FIND_ACCOUNT_BY_GENERATED_DATE, query = "SELECT a FROM Account a WHERE a.subsidiary.idSubsidiary = :idSubsidiary AND a.generatedDate LIKE :generatedDate") })
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ACCOUNT_BY_GENERATED_DATE = "FIND_ACCOUNT_BY_GENERATED_DATE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idAccount", nullable = false)
	private Integer idAccount;
	@Basic(optional = false)
	@NotNull
	@Column(name = "referenceMonth", nullable = false)
	private short referenceMonth;
	@Basic(optional = false)
	@NotNull
	@Column(name = "referenceYear", nullable = false)
	private Integer referenceYear;
	@Column(name = "fromDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	@Column(name = "toDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;
	@Column(name = "ordersQuantity")
	private Short ordersQuantity;
	@Max(value = 99999)
	@Min(value = 0)
	@Column(name = "totalPrice", precision = 7, scale = 2)
	private Double totalPrice;
	@Column(name = "payed")
	private Boolean payed;
	@Column(name = "description", length = 300)
	private String description;
	@Column(name = "generated_date")
	@Temporal(TemporalType.DATE)
	private Date generatedDate;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;

	public Account() {
	}

	public Account(Integer idAccount) {
		this.idAccount = idAccount;
	}

	public Account(Integer idAccount, short month, Integer year) {
		this.idAccount = idAccount;
		referenceMonth = month;
		referenceYear = year;
	}

	public Account(short month, Integer year, Date from, Date to, Short ordersQuantity, Double totalPrice,
			Boolean payed, Subsidiary subsidiary, String description, Date generatedDate) {
		referenceMonth = month;
		referenceYear = year;
		fromDate = from;
		toDate = to;
		this.ordersQuantity = ordersQuantity;
		this.totalPrice = totalPrice;
		this.payed = payed;
		this.subsidiary = subsidiary;
		this.description = description;
		this.generatedDate = generatedDate;
	}

	public Integer getIdAccount() {
		return idAccount;
	}

	public void setIdAccount(Integer idAccount) {
		this.idAccount = idAccount;
	}

	public short getReferenceMonth() {
		return referenceMonth;
	}

	public void setReferenceMonth(short referenceMonth) {
		this.referenceMonth = referenceMonth;
	}

	public Integer getReferenceYear() {
		return referenceYear;
	}

	public void setReferenceYear(Integer referenceYear) {
		this.referenceYear = referenceYear;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Short getOrdersQuantity() {
		return ordersQuantity;
	}

	public void setOrdersQuantity(Short ordersQuantity) {
		this.ordersQuantity = ordersQuantity;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Boolean getPayed() {
		return payed;
	}

	public void setPayed(Boolean payed) {
		this.payed = payed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary idSubsidiary) {
		subsidiary = idSubsidiary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((generatedDate == null) ? 0 : generatedDate.hashCode());
		result = prime * result + ((idAccount == null) ? 0 : idAccount.hashCode());
		result = prime * result + referenceMonth;
		result = prime * result + ((ordersQuantity == null) ? 0 : ordersQuantity.hashCode());
		result = prime * result + ((payed == null) ? 0 : payed.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
		result = prime * result + ((totalPrice == null) ? 0 : totalPrice.hashCode());
		result = prime * result + ((referenceYear == null) ? 0 : referenceYear.hashCode());
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
		Account other = (Account) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (generatedDate == null) {
			if (other.generatedDate != null)
				return false;
		} else if (!generatedDate.equals(other.generatedDate))
			return false;
		if (idAccount == null) {
			if (other.idAccount != null)
				return false;
		} else if (!idAccount.equals(other.idAccount))
			return false;
		if (referenceMonth != other.referenceMonth)
			return false;
		if (ordersQuantity == null) {
			if (other.ordersQuantity != null)
				return false;
		} else if (!ordersQuantity.equals(other.ordersQuantity))
			return false;
		if (payed == null) {
			if (other.payed != null)
				return false;
		} else if (!payed.equals(other.payed))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		if (totalPrice == null) {
			if (other.totalPrice != null)
				return false;
		} else if (!totalPrice.equals(other.totalPrice))
			return false;
		if (referenceYear == null) {
			if (other.referenceYear != null)
				return false;
		} else if (!referenceYear.equals(other.referenceYear))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [idAccount=" + idAccount + ", month=" + referenceMonth + ", year=" + referenceYear
				+ ", ordersQuantity=" + ordersQuantity + ", totalPrice=" + totalPrice + ", idSubsidiary=" + subsidiary
				+ "]";
	}

}
