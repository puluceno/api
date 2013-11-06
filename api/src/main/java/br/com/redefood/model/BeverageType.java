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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "BeverageType", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = "BeverageType.findAll", query = "SELECT b FROM BeverageType b"),
		@NamedQuery(name = "BeverageType.findAllByRestaurant", query = "SELECT DISTINCT bt FROM BeverageType bt LEFT JOIN FETCH bt.beverages b WHERE bt.subsidiary.idSubsidiary = :idSubsidiary AND (b.subsidiary.idSubsidiary = :idSubsidiary OR b.subsidiary IS NULL) AND b.active = true AND bt.active = true ORDER BY bt.exhibitionOrder ASC, b.exhibitionOrder ASC"),
		@NamedQuery(name = "BeverageType.findLocalOrDeliveryByRestaurant", query = "SELECT DISTINCT bt FROM BeverageType bt LEFT JOIN FETCH bt.beverages b WHERE bt.subsidiary.idSubsidiary = :idSubsidiary AND (b.subsidiary.idSubsidiary = :idSubsidiary OR b.subsidiary IS NULL) AND b.active = true AND b.localOnly = :localOnly AND bt.active = true ORDER BY bt.exhibitionOrder ASC, b.exhibitionOrder ASC"),
		@NamedQuery(name = "BeverageType.findJustBeverageTypeBySubsidiary", query = "SELECT DISTINCT bt FROM BeverageType bt  WHERE bt.subsidiary.idSubsidiary = :idSubsidiary AND bt.active = true ORDER BY bt.exhibitionOrder ASC"),
		@NamedQuery(name = "BeverageType.findMaxExhibitionOrder", query = "SELECT MAX(bt.exhibitionOrder) FROM BeverageType bt WHERE bt.subsidiary.idSubsidiary = :idSubsidiary AND bt.active = true") })
public class BeverageType implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "BeverageType.findAll";
	public static final String FIND_ALL_BY_RESTAURANT = "BeverageType.findAllByRestaurant";
	public static final String FIND_LOCAL_OR_DELIVERY__BY_RESTAURANT = "BeverageType.findLocalOrDeliveryByRestaurant";
	public static final String FIND_JUST_BEVERAGETYPE_BY_SUBSIDIARY = "BeverageType.findJustBeverageTypeBySubsidiary";
	public static final String FIND_MAX_EXHIBITIONORDER = "BeverageType.findMaxExhibitionOrder";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idBeverageType", nullable = false)
	private Short id;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	@Column(name = "description", length = 300)
	private String description;
	@Column(name = "exhibitionOrder")
	private Integer exhibitionOrder;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "beverageType", fetch = FetchType.LAZY)
	private List<Beverage> beverages;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@Column(name = "active")
	private boolean active = true;

	public BeverageType() {
	}

	public BeverageType(Short idBeverageType) {
		id = idBeverageType;
	}

	public BeverageType(Short idBeverageType, String name) {
		id = idBeverageType;
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

	public List<Beverage> getBeverages() {
		return beverages;
	}

	public void setBeverages(List<Beverage> beverages) {
		this.beverages = beverages;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

	public Integer getExhibitionOrder() {
		return exhibitionOrder;
	}

	public void setExhibitionOrder(Integer exhibitionOrder) {
		this.exhibitionOrder = exhibitionOrder;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beverages == null) ? 0 : beverages.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		BeverageType other = (BeverageType) obj;
		if (beverages == null) {
			if (other.beverages != null)
				return false;
		} else if (!beverages.equals(other.beverages))
			return false;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BeverageType [id=" + id + ", name=" + name + ", description=" + description + ", beverageList="
				+ beverages + "]";
	}

}
