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
@Table(name = "IngredientType", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = IngredientType.FIND_ALL_INGREDIENT_TYPE, query = "SELECT DISTINCT i FROM IngredientType i INNER JOIN FETCH i.ingredients ORDER BY i.name ASC"),
		@NamedQuery(name = IngredientType.FIND_INGREDIENT_TYPE_BY_SUBSIDIARY, query = "SELECT DISTINCT it FROM IngredientType it WHERE it.subsidiary.idSubsidiary = :idSubsidiary AND it.active = true ORDER BY it.name ASC"),
		@NamedQuery(name = IngredientType.FIND_INGREDIENT_TYPES_BY_RESTAURANT, query = "SELECT DISTINCT it FROM IngredientType it LEFT JOIN FETCH it.ingredients i WHERE it.subsidiary.idSubsidiary = :idSubsidiary AND (i.subsidiary.idSubsidiary = :idSubsidiary OR i.subsidiary IS NULL) AND it.active = true AND i.active = true ORDER BY it.name, i.name ASC") })
public class IngredientType implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_INGREDIENT_TYPE = "FIND_ALL_INGREDIENT_TYPE";
	public static final String FIND_INGREDIENT_TYPE_BY_SUBSIDIARY = "FIND_INGREDIENT_TYPE_BY_SUBSIDIARY";
	public static final String FIND_INGREDIENT_TYPES_BY_RESTAURANT = "FIND_INGREDIENT_TYPES_BY_RESTAURANT";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idIngredientType", nullable = false)
	private Short id;
	@Column(name = "name", length = 60)
	private String name;
	@Column(name = "description", length = 300)
	private String description;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ingredientType", fetch = FetchType.LAZY)
	private List<MealIngredientTypes> mealIngredientTypes;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ingredientType", fetch = FetchType.LAZY)
	private List<Ingredient> ingredients;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@Column(name = "active")
	private boolean active = true;

	public IngredientType() {
	}

	public IngredientType(Short idIngredientType) {
		id = idIngredientType;
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

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredientList) {
		ingredients = ingredientList;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ingredients == null) ? 0 : ingredients.hashCode());
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
		IngredientType other = (IngredientType) obj;
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
		if (ingredients == null) {
			if (other.ingredients != null)
				return false;
		} else if (!ingredients.equals(other.ingredients))
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
		return "IngredientType [id=" + id + ", name=" + name + ", description=" + description
				+ ", mealIngredientTypes=" + mealIngredientTypes + ", ingredients=" + ingredients + "]";
	}

}
