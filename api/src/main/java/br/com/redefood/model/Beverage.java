/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Beverage", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = "Beverage.findBySubsidiaryAndBeverage", query = "SELECT b FROM Beverage b WHERE b.subsidiary.idSubsidiary = :idSubsidiary AND b.id = :idBeverage AND b.active = true"),
		@NamedQuery(name = "Beverage.findMaxExhibitionOrder", query = "SELECT MAX(b.exhibitionOrder) FROM Beverage b WHERE b.subsidiary.idSubsidiary = :idSubsidiary AND b.beverageType.id = :idBeverageType AND b.active = true") })
public class Beverage implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_SUBSIDIARY_AND_BEVERAGE = "Beverage.findBySubsidiaryAndBeverage";
	public static final String FIND_MAX_EXHIBITIONORDER = "Beverage.findMaxExhibitionOrder";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idBeverage", nullable = false)
	private Short id;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 50)
	private String name;
	@Column(name = "description", length = 200)
	private String description;
	@Column(name = "idNutritionalFacts")
	private Integer nutritionalFacts;
	@Column(name = "image")
	private String image = "default/drink256.png";
	@JoinColumn(name = "idBeverageType", referencedColumnName = "idBeverageType", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private BeverageType beverageType;
	@Max(value = 9999, message = "max price")
	@Min(value = 0, message = "min price")
	@Column(name = "price", precision = 6, scale = 2)
	private Double price;
	@Column(name = "exhibitionOrder")
	private Integer exhibitionOrder;
	@Column(name = "sku", length = 30)
	private String sku;
	@Column(name = "note", length = 300)
	private String note;
	@ManyToMany(mappedBy = "beverages", fetch = FetchType.LAZY)
	private List<Orders> orders;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@Column(name = "active")
	private boolean active = true;
	@Column(name = "localOnly")
	private Boolean localOnly = false;
	@Column(name = "outOfStock")
	private Boolean outOfStock = false;

	public Beverage() {
	}

	public Beverage(Short idBeverage) {
		id = idBeverage;
	}

	public Beverage(Short idBeverage, String name) {
		id = idBeverage;
		this.name = name;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
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

	public Integer getNutritionalFacts() {
		return nutritionalFacts;
	}

	public void setNutritionalFacts(Integer nutritionalFacts) {
		this.nutritionalFacts = nutritionalFacts;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@JsonIgnoreProperties({ "beverages" })
	public BeverageType getBeverageType() {
		return beverageType;
	}

	public void setBeverageType(BeverageType beverageType) {
		this.beverageType = beverageType;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getExhibitionOrder() {
		return exhibitionOrder;
	}

	public void setExhibitionOrder(Integer exhibitionOrder) {
		this.exhibitionOrder = exhibitionOrder;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@JsonIgnore
	public List<Orders> getOrders() {
		return orders;
	}

	public void setOrders(List<Orders> orders) {
		this.orders = orders;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Boolean getLocalOnly() {
		return localOnly;
	}

	public void setLocalOnly(Boolean localOnly) {
		this.localOnly = localOnly;
	}

	public Boolean getOutOfStock() {
		return outOfStock;
	}

	public void setOutOfStock(Boolean outOfStock) {
		this.outOfStock = outOfStock;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((beverageType == null) ? 0 : beverageType.hashCode());
		result = prime * result + ((nutritionalFacts == null) ? 0 : nutritionalFacts.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Beverage other = (Beverage) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (beverageType == null) {
			if (other.beverageType != null)
				return false;
		} else if (!beverageType.equals(other.beverageType))
			return false;
		if (nutritionalFacts == null) {
			if (other.nutritionalFacts != null)
				return false;
		} else if (!nutritionalFacts.equals(other.nutritionalFacts))
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
		return "Beverage [idBeverage=" + id + ", name=" + name + ", description=" + description
				+ ", idNutritionalFacts=" + nutritionalFacts + ", idBeverageType=" + beverageType + "]";
	}

}
