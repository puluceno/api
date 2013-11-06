package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "BeverageOrder", schema = "RedeFood")
public class BeverageOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idBeverageOrder", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "idBeverage", nullable = false)
	private short idBeverage;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	@Max(value = 9999)
	@Min(value = 0)
	@Basic(optional = false)
	@Column(name = "price", nullable = false, precision = 6, scale = 2)
	private Double price;
	@JoinColumn(name = "idOrders", referencedColumnName = "idOrders", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Orders order;
	@Column(name = "note")
	private String note;
	@Column(name = "payed")
	private Boolean payed = false;

	public BeverageOrder() {
	}

	public BeverageOrder(Integer idBeverageOrder) {
		id = idBeverageOrder;
	}

	public BeverageOrder(short idBeverage, String name, Double price, Orders order, String note) {
		this.idBeverage = idBeverage;
		this.name = name;
		this.price = price;
		this.order = order;
		this.note = note;
	}

	public BeverageOrder(short idBeverage, String name, Double price) {
		this.idBeverage = idBeverage;
		this.name = name;
		this.price = price;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer idBeverageOrder) {
		id = idBeverageOrder;
	}

	public short getIdBeverage() {
		return idBeverage;
	}

	public void setIdBeverage(short idBeverage) {
		this.idBeverage = idBeverage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@JsonIgnore
	public Orders getOrder() {
		return order;
	}

	public void setOrder(Orders idOrders) {
		order = idOrders;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getPayed() {
		return payed;
	}

	public void setPayed(Boolean payed) {
		this.payed = payed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + idBeverage;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
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
		BeverageOrder other = (BeverageOrder) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idBeverage != other.idBeverage)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BeverageOrder [id=" + id + ", idBeverage=" + idBeverage + ", name=" + name + ", price=" + price
				+ ", order=" + order + "]";
	}

}
