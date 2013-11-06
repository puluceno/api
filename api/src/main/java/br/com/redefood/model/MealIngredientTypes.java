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
@Table(name = "MealIngredientTypes", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = MealIngredientTypes.FIND_MEAL_INGREDIENT_TYPE_BY_MEAL_AND_RESTAURANT, query = "SELECT DISTINCT mit FROM MealIngredientTypes mit LEFT JOIN FETCH mit.mealIngredientTypeshasIngredient miti LEFT JOIN FETCH miti.ingredient i WHERE mit.meal.idMeal = :idMeal AND mit.meal.subsidiary.idSubsidiary = :idSubsidiary") })
public class MealIngredientTypes implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_MEAL_INGREDIENT_TYPE_BY_MEAL_AND_RESTAURANT = "FIND_MEAL_INGREDIENT_TYPE_BY_MEAL_AND_RESTAURANT";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idMealIngredientType", nullable = false)
	private Integer id;
	@Column(name = "multiple")
	private Boolean multiple;
	@Column(name = "exhibitionOrder")
	private Integer exhibitionOrder;
	@JoinColumn(name = "idIngredientType", referencedColumnName = "idIngredientType", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private IngredientType ingredientType;
	@JoinColumn(name = "idMeal", referencedColumnName = "idMeal", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Meal meal;
	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH }, mappedBy = "mealIngredientType", fetch = FetchType.LAZY)
	private List<MealIngredientTypeshasIngredient> mealIngredientTypeshasIngredient;

	public MealIngredientTypes() {
	}

	public MealIngredientTypes(Integer idMealIngredientType) {
		id = idMealIngredientType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer idMealIngredientType) {
		id = idMealIngredientType;
	}

	public Boolean getMultiple() {
		return multiple;
	}

	public Integer getOrder() {
		return exhibitionOrder;
	}

	public void setOrder(Integer order) {
		exhibitionOrder = order;
	}

	public void setMultiple(Boolean multiple) {
		this.multiple = multiple;
	}

	public IngredientType getIngredientType() {
		return ingredientType;
	}

	public void setIngredientType(IngredientType ingredientType) {
		this.ingredientType = ingredientType;
	}

	@JsonIgnore
	public Meal getMeal() {
		return meal;
	}

	public void setMeal(Meal meal) {
		this.meal = meal;
	}

	public List<MealIngredientTypeshasIngredient> getMealIngredientTypeshasIngredient() {
		return mealIngredientTypeshasIngredient;
	}

	public void setMealIngredientTypeshasIngredient(
			List<MealIngredientTypeshasIngredient> mealIngredientTypeshasIngredient) {
		this.mealIngredientTypeshasIngredient = mealIngredientTypeshasIngredient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ingredientType == null) ? 0 : ingredientType.hashCode());
		result = prime * result + ((meal == null) ? 0 : meal.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((multiple == null) ? 0 : multiple.hashCode());
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
		MealIngredientTypes other = (MealIngredientTypes) obj;
		if (ingredientType == null) {
			if (other.ingredientType != null)
				return false;
		} else if (!ingredientType.equals(other.ingredientType))
			return false;
		if (meal == null) {
			if (other.meal != null)
				return false;
		} else if (!meal.equals(other.meal))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (multiple == null) {
			if (other.multiple != null)
				return false;
		} else if (!multiple.equals(other.multiple))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MealIngredientTypes [idMealIngredientType=" + id + ", multiple=" + multiple + ", ingredientType="
				+ ingredientType + ", meal=" + meal + ", mealIngredientTypeshasIngredient="
				+ mealIngredientTypeshasIngredient + "]";
	}

}
