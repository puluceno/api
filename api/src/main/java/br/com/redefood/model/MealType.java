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
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "MealType", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = "MealType.findAll", query = "SELECT m FROM MealType m"),
		@NamedQuery(name = "MealType.findMealTypesBySubsidiary", query = "SELECT DISTINCT mt FROM MealType mt INNER JOIN FETCH mt.meals m WHERE mt.subsidiary.idSubsidiary = :idSubsidiary AND m.subsidiary.idSubsidiary = :idSubsidiary AND mt.active = 1  AND m.active = 1 ORDER BY mt.exhibitionOrder ASC, m.exhibitionOrder ASC"),
		@NamedQuery(name = "MealType.findLocalOrDeliveryMealTypesBySubsidiary", query = "SELECT DISTINCT mt FROM MealType mt INNER JOIN FETCH mt.meals m WHERE mt.subsidiary.idSubsidiary = :idSubsidiary AND m.subsidiary.idSubsidiary = :idSubsidiary AND mt.active = 1  AND m.active = 1 AND m.localOnly = :localOnly ORDER BY mt.exhibitionOrder ASC, m.exhibitionOrder ASC"),
		@NamedQuery(name = "MealType.findByRestaurantId", query = "SELECT DISTINCT mt from MealType mt LEFT JOIN FETCH mt.meals m WHERE mt.subsidiary.idSubsidiary=:idSubsidiary AND (m.subsidiary.idSubsidiary = :idSubsidiary OR m IS NULL) AND mt.active = true AND m.active = 1 ORDER BY mt.exhibitionOrder ASC, m.exhibitionOrder ASC"),
		@NamedQuery(name = "MealType.findMaxExhibitionOrder", query = "SELECT MAX(mt.exhibitionOrder) FROM MealType mt WHERE mt.subsidiary.idSubsidiary = :idSubsidiary AND mt.active = true"),
		@NamedQuery(name = "MealType.findAllBySubsidiary", query = "SELECT DISTINCT mt from MealType mt WHERE mt.subsidiary.idSubsidiary=:idSubsidiary AND mt.active = true ORDER BY mt.exhibitionOrder ASC") })
public class MealType implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_RESTAURANT_ID = "MealType.findByRestaurantId";
	public static final String FIND_MEALS_BY_MEALTYPE_AND_SUBSIDIARY = "MealType.findMealTypesBySubsidiary";
	public static final String FIND_MEALS_LOCAL_OR_DELIVERY_BY_MEALTYPE_AND_SUBSIDIARY = "MealType.findLocalOrDeliveryMealTypesBySubsidiary";
	public static final String FIND_MAX_EXHIBITIONORDER = "MealType.findMaxExhibitionOrder";
	public static final String FIND_ALL_BY_SUBSIDIARY = "MealType.findAllBySubsidiary";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idMealType", nullable = false)
	private Integer idMealType;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 60)
	private String name;
	@Column(name = "exhibitionOrder")
	private Integer exhibitionOrder;
	@Column(name = "description", length = 100)
	private String description;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "mealType", fetch = FetchType.LAZY)
	private List<Meal> meals;
	@Column(name = "active")
	private boolean active = true;

	public MealType() {
	}

	public MealType(Integer idMealType) {
		this.idMealType = idMealType;
	}

	public MealType(Integer idMealType, String name) {
		this.idMealType = idMealType;
		this.name = name;
	}

	@JsonProperty("id")
	public Integer getId() {
		return idMealType;
	}

	@JsonProperty("id")
	public void setId(Integer idMealType) {
		this.idMealType = idMealType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getExhibitionOrder() {
		return exhibitionOrder;
	}

	public void setExhibitionOrder(Integer exhibitionOrder) {
		this.exhibitionOrder = exhibitionOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary idSubsidiary) {
		subsidiary = idSubsidiary;
	}

	public List<Meal> getMeals() {
		return meals;
	}

	public void setMeals(List<Meal> meals) {
		this.meals = meals;
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((exhibitionOrder == null) ? 0 : exhibitionOrder.hashCode());
		result = prime * result + ((idMealType == null) ? 0 : idMealType.hashCode());
		result = prime * result + ((meals == null) ? 0 : meals.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
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
		MealType other = (MealType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (exhibitionOrder == null) {
			if (other.exhibitionOrder != null)
				return false;
		} else if (!exhibitionOrder.equals(other.exhibitionOrder))
			return false;
		if (idMealType == null) {
			if (other.idMealType != null)
				return false;
		} else if (!idMealType.equals(other.idMealType))
			return false;
		if (meals == null) {
			if (other.meals != null)
				return false;
		} else if (!meals.equals(other.meals))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MealType [idMealType=" + idMealType + ", name=" + name + ", exhibitionOrder=" + exhibitionOrder
				+ ", description=" + description + ", subsidiary=" + subsidiary + ", meals=" + meals + "]";
	}

}
