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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Ingredient", schema = "RedeFood")
public class Ingredient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idIngredient", nullable = false)
	private Integer id;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	@Lob
	@Column(name = "image")
	private String image;
	@Column(name = "description", length = 200)
	private String description;
	@JoinColumn(name = "idIngredientType", referencedColumnName = "idIngredientType", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private IngredientType ingredientType;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idIngredient", fetch = FetchType.LAZY)
	private List<IngredienthasNutritionalFacts> ingredienthasNutritionalFacts;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ingredient", fetch = FetchType.LAZY)
	private List<MealIngredientTypeshasIngredient> mealIngredientTypeshasIngredient;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@Column(name = "active")
	private boolean active = true;
	@Column(name = "outOfStock")
	private Boolean outOfStock;

	public Ingredient() {
	}

	public Ingredient(Integer idIngredient) {
		id = idIngredient;
	}

	public Ingredient(Integer idIngredient, String name) {
		id = idIngredient;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public IngredientType getIngredientType() {
		return ingredientType;
	}

	public void setIngredientType(IngredientType ingredientType) {
		this.ingredientType = ingredientType;
	}

	public List<IngredienthasNutritionalFacts> getIngredienthasNutritionalFacts() {
		return ingredienthasNutritionalFacts;
	}

	public void setIngredienthasNutritionalFacts(List<IngredienthasNutritionalFacts> ingredienthasNutritionalFacts) {
		this.ingredienthasNutritionalFacts = ingredienthasNutritionalFacts;
	}

	@JsonIgnore
	public List<MealIngredientTypeshasIngredient> getMealIngredientTypeshasIngredient() {
		return mealIngredientTypeshasIngredient;
	}

	public void setMealIngredientTypeshasIngredient(
			List<MealIngredientTypeshasIngredient> mealIngredientTypeshasIngredient) {
		this.mealIngredientTypeshasIngredient = mealIngredientTypeshasIngredient;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	@JsonIgnore
	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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
		result = prime * result + ((ingredientType == null) ? 0 : ingredientType.hashCode());
		result = prime * result
				+ ((ingredienthasNutritionalFacts == null) ? 0 : ingredienthasNutritionalFacts.hashCode());
		result = prime * result
				+ ((mealIngredientTypeshasIngredient == null) ? 0 : mealIngredientTypeshasIngredient.hashCode());
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
		Ingredient other = (Ingredient) obj;
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
		if (ingredientType == null) {
			if (other.ingredientType != null)
				return false;
		} else if (!ingredientType.equals(other.ingredientType))
			return false;
		if (ingredienthasNutritionalFacts == null) {
			if (other.ingredienthasNutritionalFacts != null)
				return false;
		} else if (!ingredienthasNutritionalFacts.equals(other.ingredienthasNutritionalFacts))
			return false;
		if (mealIngredientTypeshasIngredient == null) {
			if (other.mealIngredientTypeshasIngredient != null)
				return false;
		} else if (!mealIngredientTypeshasIngredient.equals(other.mealIngredientTypeshasIngredient))
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
		return "Ingredient [id=" + id + ", name=" + name + ", description=" + description + ", ingredientType="
				+ ingredientType + ", ingredienthasNutritionalFacts=" + ingredienthasNutritionalFacts
				+ ", mealIngredientTypeshasIngredient=" + mealIngredientTypeshasIngredient + "]";
	}

}
