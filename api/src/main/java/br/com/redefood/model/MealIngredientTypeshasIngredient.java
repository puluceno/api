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
@Table(name = "MealIngredientTypes_has_Ingredient", schema = "RedeFood")
public class MealIngredientTypeshasIngredient implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idMealIngredientTypes_has_Ingredients", nullable = false)
	private Integer id;
	@Max(value = 999)
	@Min(value = 0)
	@Column(name = "price", precision = 5, scale = 2, nullable = false)
	private Double price = 0.0;
	@Column(name = "optional")
	private Boolean optional;
	@JoinColumn(name = "idIngredient", referencedColumnName = "idIngredient", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Ingredient ingredient;
	@JoinColumn(name = "idMealIngredientType", referencedColumnName = "idMealIngredientType", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private MealIngredientTypes mealIngredientType;

	public MealIngredientTypeshasIngredient() {
	}

	public MealIngredientTypeshasIngredient(Integer idMealIngredientTypeshasIngredients) {
		id = idMealIngredientTypeshasIngredients;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer idMealIngredientTypeshasIngredients) {
		id = idMealIngredientTypeshasIngredients;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient idIngredient) {
		ingredient = idIngredient;
	}

	@JsonIgnore
	public MealIngredientTypes getMealIngredientType() {
		return mealIngredientType;
	}

	public void setMealIngredientType(MealIngredientTypes idMealIngredientType) {
		mealIngredientType = idMealIngredientType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
		result = prime * result + ((mealIngredientType == null) ? 0 : mealIngredientType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((optional == null) ? 0 : optional.hashCode());
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
		MealIngredientTypeshasIngredient other = (MealIngredientTypeshasIngredient) obj;
		if (ingredient == null) {
			if (other.ingredient != null)
				return false;
		} else if (!ingredient.equals(other.ingredient))
			return false;
		if (mealIngredientType == null) {
			if (other.mealIngredientType != null)
				return false;
		} else if (!mealIngredientType.equals(other.mealIngredientType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (optional == null) {
			if (other.optional != null)
				return false;
		} else if (!optional.equals(other.optional))
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
		return "MealIngredientTypeshasIngredient [idMealIngredientTypeshasIngredients=" + id + ", price=" + price
				+ ", optional=" + optional + ", idIngredient=" + ingredient + ", idMealIngredientType="
				+ mealIngredientType + "]";
	}

}
