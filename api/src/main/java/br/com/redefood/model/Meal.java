/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Meal", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = Meal.FIND_MAX_ORDER, query = "SELECT MAX(m.exhibitionOrder) FROM Meal m WHERE m.subsidiary.idSubsidiary = :idSubsidiary AND m.mealType.id = :idMealType AND m.active=1"),
		@NamedQuery(name = Meal.FIND_BY_ID_AND_SUBSIDIARY, query = "SELECT m FROM Meal m WHERE m.idMeal = :idMeal AND m.subsidiary.idSubsidiary = :idSubsidiary"),
		@NamedQuery(name = Meal.FIND_MEAL_BY_SUBSIDIARY, query = "SELECT m FROM Meal m WHERE m.subsidiary.idSubsidiary = :idSubsidiary AND m.active = 1 ORDER BY m.exhibitionOrder ASC"),
		@NamedQuery(name = Meal.FIND_BY_SUBSIDIARY_GROUP_BY_MEALTYPE, query = "SELECT DISTINCT mt FROM MealType mt INNER JOIN FETCH mt.meals m WHERE m.subsidiary.idSubsidiary = :idSubsidiary AND m.active = 1 ORDER BY mt.exhibitionOrder ASC, m.exhibitionOrder ASC"),
		@NamedQuery(name = Meal.FIND_MIN_PRICE_BY_SUBSIDIARY, query = "SELECT MIN(m.price) FROM Meal m WHERE m.subsidiary.idSubsidiary = :idSubsidiary"),
		@NamedQuery(name = Meal.FIND_MAX_PRICE_BY_SUBSIDIARY, query = "SELECT MAX(m.price) FROM Meal m WHERE m.subsidiary.idSubsidiary = :idSubsidiary") })
public class Meal implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_MEAL_BY_SUBSIDIARY = "FIND_MEAL_BY_SUBSIDIARY";
	public static final String FIND_BY_SUBSIDIARY_GROUP_BY_MEALTYPE = "FIND_BY_SUBSIDIARY_GROUP_BY_MEALTYPE";
	public static final String FIND_BY_ID_AND_SUBSIDIARY = "FIND_BY_ID_AND_SUBSIDIARY";
	public static final String FIND_MAX_ORDER = "FIND_MAX_ORDER";
	public static final String FIND_MAX_PRICE_BY_SUBSIDIARY = "FIND_MAX_PRICE_BY_SUBSIDIARY";
	public static final String FIND_MIN_PRICE_BY_SUBSIDIARY = "FIND_MIN_PRICE_BY_SUBSIDIARY";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idMeal", nullable = false)
	private Integer idMeal;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	@Max(value = 9999)
	@Min(value = 0)
	@Basic(optional = false)
	@Column(name = "price", nullable = false, precision = 6, scale = 2)
	private BigDecimal price;
	@Column(name = "description", length = 300)
	private String description;
	@Column(name = "image")
	private String image = "default/food512.png";
	@Column(name = "exhibitionOrder")
	private Integer exhibitionOrder;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "meal", fetch = FetchType.LAZY)
	private List<MealRating> mealRatings;
	@ManyToMany(mappedBy = "meals", fetch = FetchType.LAZY)
	private List<Orders> ordersList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "meal", fetch = FetchType.LAZY)
	private List<MealIngredientTypes> mealIngredientTypes;
	@OneToMany(mappedBy = "idMeal", fetch = FetchType.LAZY)
	private List<SubsidiaryhasPromotion> subsidiaryhasPromotionList;
	@JoinColumn(name = "idMealType", referencedColumnName = "idMealType", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private MealType mealType;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@Column(name = "note")
	private String note;
	@Column(name = "active", nullable = false)
	private Boolean active = true;
	@Column(name = "sku")
	private String sku;
	@Column(name = "localOnly")
	private Boolean localOnly = false;
	@Column(name = "outOfStock")
	private Boolean outOfStock = false;

	public Meal() {
	}

	public Meal(Integer idMeal) {
		this.idMeal = idMeal;
	}

	public Meal(Integer idMeal, String name, BigDecimal price) {
		this.idMeal = idMeal;
		this.name = name;
		this.price = price;
	}

	public Meal(String name, BigDecimal price, String description) {
		this.name = name;
		this.price = price;
		this.description = description;
	}

	@JsonProperty("id")
	public Integer getId() {
		return idMeal;
	}

	@JsonProperty("id")
	public void setId(Integer idMeal) {
		this.idMeal = idMeal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price.setScale(2);
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getExhibitionOrder() {
		return exhibitionOrder;
	}

	public void setExhibitionOrder(Integer order) {
		exhibitionOrder = order;
	}

	public List<MealRating> getMealRatings() {
		return mealRatings;
	}

	public void setMealRatings(List<MealRating> mealRatings) {
		this.mealRatings = mealRatings;
	}

	@JsonIgnore
	public List<Orders> getOrdersList() {
		return ordersList;
	}

	@JsonIgnore
	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
	}

	public List<MealIngredientTypes> getMealIngredientTypes() {
		return mealIngredientTypes;
	}

	public void setMealIngredientTypes(List<MealIngredientTypes> mealIngredientTypes) {
		this.mealIngredientTypes = mealIngredientTypes;
	}

	@XmlTransient
	@JsonIgnore
	public List<SubsidiaryhasPromotion> getSubsidiaryhasPromotionList() {
		return subsidiaryhasPromotionList;
	}

	public void setSubsidiaryhasPromotionList(List<SubsidiaryhasPromotion> subsidiaryhasPromotionList) {
		this.subsidiaryhasPromotionList = subsidiaryhasPromotionList;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	@JsonIgnore
	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

	@JsonIgnoreProperties({ "meals" })
	public MealType getMealType() {
		return mealType;
	}

	public void setMealType(MealType mealType) {
		this.mealType = mealType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meal other = (Meal) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
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
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (mealIngredientTypes == null) {
			if (other.mealIngredientTypes != null)
				return false;
		} else if (!mealIngredientTypes.equals(other.mealIngredientTypes))
			return false;
		if (mealRatings == null) {
			if (other.mealRatings != null)
				return false;
		} else if (!mealRatings.equals(other.mealRatings))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (ordersList == null) {
			if (other.ordersList != null)
				return false;
		} else if (!ordersList.equals(other.ordersList))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		if (subsidiaryhasPromotionList == null) {
			if (other.subsidiaryhasPromotionList != null)
				return false;
		} else if (!subsidiaryhasPromotionList.equals(other.subsidiaryhasPromotionList))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((exhibitionOrder == null) ? 0 : exhibitionOrder.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((mealIngredientTypes == null) ? 0 : mealIngredientTypes.hashCode());
		result = prime * result + ((mealRatings == null) ? 0 : mealRatings.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((ordersList == null) ? 0 : ordersList.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
		result = prime * result + ((subsidiaryhasPromotionList == null) ? 0 : subsidiaryhasPromotionList.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Meal [idMeal=" + idMeal + ", name=" + name + ", description=" + description + "]";
	}

}
