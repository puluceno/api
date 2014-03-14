/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.redefood.model.enumtype.TypeOrder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "OrderType", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = OrderType.FIND_ALL_ORDER_TYPE, query = "SELECT o FROM OrderType o"),
		@NamedQuery(name = OrderType.COUNT_BY_ORDERTYPE_SUBSIDIARY_AND_PERIOD, query = "SELECT COUNT(*) as qty, o.orderType.name as name FROM Orders o "
				+ "WHERE o.subsidiary.idSubsidiary = :idSubsidiary "
				+ "AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED' "
				+ "GROUP BY o.orderType.idOrderType"),
		@NamedQuery(name = OrderType.FIND_AVAILABLE_ORDERTYPE_BY_SUBSIDIARY, query = "SELECT ot FROM OrderType ot WHERE ot NOT IN (SELECT sot FROM Subsidiary s JOIN s.orderTypes sot where s.idSubsidiary = :idSubsidiary)") })
public class OrderType implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_AVAILABLE_ORDERTYPE_BY_SUBSIDIARY = "FIND_AVAILABLE_ORDERTYPE_BY_SUBSIDIARY";
	public static final String FIND_ALL_ORDER_TYPE = "FIND_ALL_ORDER_TYPE";
	public static final String COUNT_BY_ORDERTYPE_SUBSIDIARY_AND_PERIOD = "COUNT_BY_ORDERTYPE_SUBSIDIARY_AND_PERIOD";

	/**
	 * Delivery Online = 1
	 */
	public static final Short DELIVERY_ONLINE = 1;
	/**
	 * Schedule Online = 2
	 */
	public static final Short SCHEDULE_ONLINE = 2;
	/**
	 * Delivery Phone = 3
	 */
	public static final Short DELIVERY_PHONE = 3;
	/**
	 * Local = 4
	 */
	public static final Short LOCAL = 4;
	/**
	 * Local to go = 5
	 */
	public static final Short LOCAL_TO_GO = 5;
	/**
	 * Pickup Online = 6
	 */
	public static final Short PICKUP_ONLINE = 6;
	/**
	 * Pickup Phone = 7
	 */
	public static final Short PICKUP_PHONE = 7;
	/**
	 * Employee Meal = 8
	 */
	public static final Short EMPLOYEE_MEAL = 8;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idOrderType", nullable = false)
	private Short idOrderType;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 45)
	private String name;
	@Basic(optional = false)
	@Column(name = "description", nullable = false, length = 300)
	private String description;
	@Basic(optional = false)
	@Column(name = "clientName", nullable = false, length = 45)
	private String clientName;
	@Basic(optional = false)
	@Column(name = "clientDescription", nullable = false, length = 300)
	private String clientDescription;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "orderType", fetch = FetchType.LAZY)
	private List<Orders> ordersList;
	@ManyToMany(mappedBy = "orderTypes", fetch = FetchType.LAZY)
	private List<Subsidiary> subsidiaryList;
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private TypeOrder type;

	public OrderType() {
	}

	public OrderType(Short idOrderType) {
		this.idOrderType = idOrderType;
	}

	public OrderType(Short idOrderType, String name, String description) {
		this.idOrderType = idOrderType;
		this.name = name;
		this.description = description;
	}

	@JsonProperty("id")
	public Short getId() {
		return idOrderType;
	}

	@JsonProperty("id")
	public void setId(Short idOrderType) {
		this.idOrderType = idOrderType;
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

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientDescription() {
		return clientDescription;
	}

	public void setClientDescription(String clientDescription) {
		this.clientDescription = clientDescription;
	}

	@JsonIgnore
	public List<Orders> getOrdersList() {
		return ordersList;
	}

	@JsonIgnore
	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
	}

	@JsonIgnore
	public List<Subsidiary> getSubsidiaryList() {
		return subsidiaryList;
	}

	@JsonIgnore
	public void setSubsidiaryList(List<Subsidiary> subsidiaryList) {
		this.subsidiaryList = subsidiaryList;
	}

	public TypeOrder getType() {
		return type;
	}

	public void setType(TypeOrder type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idOrderType == null ? 0 : idOrderType.hashCode());
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
		OrderType other = (OrderType) obj;
		if (idOrderType == null) {
			if (other.idOrderType != null)
				return false;
		} else if (!idOrderType.equals(other.idOrderType))
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
		return "OrderType [name=" + name + ", description=" + description + "]";
	}

}
