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
@Table(name = "MealOrder_Ingredient", schema = "RedeFood")
public class MealOrderIngredient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMealOrderIngredient", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "idIngredient", nullable = false)
    private Integer idIngredient;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "idIngredientType")
    private Short idIngredientType;
    @Column(name = "ingredientTypeName", length = 60)
    private String ingredientTypeName;
    @Max(value = 999)
    @Min(value = 0)
    @Basic(optional = false)
    @Column(name = "price", nullable = false, precision = 5, scale = 2)
    private Double price;
    @JoinColumn(name = "idMealOrder", referencedColumnName = "idMealOrder", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private MealOrder mealOrder;
    
    public MealOrderIngredient() {
    }
    
    public MealOrderIngredient(Integer idIngredient) {
	this.idIngredient = idIngredient;
    }
    
    public MealOrderIngredient(Integer idIngredient, String name, Double price) {
	this.idIngredient = idIngredient;
	this.name = name;
	this.price = price;
    }
    
    public MealOrderIngredient(Integer idIngredient, String name, Short idIngredientType, String ingredientTypeName,
	    Double price) {
	this.idIngredient = idIngredient;
	this.name = name;
	this.idIngredientType = idIngredientType;
	this.ingredientTypeName = ingredientTypeName;
	this.price = price;
    }
    
    public MealOrderIngredient(Ingredient ingredient) {
	idIngredient = ingredient.getId();
	name = ingredient.getName();
	idIngredientType = ingredient.getIngredientType().getId();
	ingredientTypeName = ingredient.getIngredientType().getName();
	price = ingredient.getMealIngredientTypeshasIngredient().get(0).getPrice();
    }
    
    public Integer getIdMealOrderIngredient() {
	return id;
    }
    
    public void setIdMealOrderIngredient(Integer idMealOrderIngredient) {
	id = idMealOrderIngredient;
    }
    
    public Integer getIdIngredient() {
	return idIngredient;
    }
    
    public void setIdIngredient(Integer idIngredient) {
	this.idIngredient = idIngredient;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public Short getIdIngredientType() {
	return idIngredientType;
    }
    
    public void setIdIngredientType(Short idIngredientType) {
	this.idIngredientType = idIngredientType;
    }
    
    public String getIngredientTypeName() {
	return ingredientTypeName;
    }
    
    public void setIngredientTypeName(String ingredientTypeName) {
	this.ingredientTypeName = ingredientTypeName;
    }
    
    public Double getPrice() {
	return price;
    }
    
    public void setPrice(Double price) {
	this.price = price;
    }
    
    @JsonIgnore
    public MealOrder getMealOrder() {
	return mealOrder;
    }
    
    @JsonIgnore
    public void setMealOrder(MealOrder idMealOrder) {
	mealOrder = idMealOrder;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (idIngredient == null ? 0 : idIngredient.hashCode());
	result = prime * result + (idIngredientType == null ? 0 : idIngredientType.hashCode());
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
	MealOrderIngredient other = (MealOrderIngredient) obj;
	if (idIngredient == null) {
	    if (other.idIngredient != null)
		return false;
	} else if (!idIngredient.equals(other.idIngredient))
	    return false;
	if (idIngredientType == null) {
	    if (other.idIngredientType != null)
		return false;
	} else if (!idIngredientType.equals(other.idIngredientType))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "MealOrderIngredient [idIngredient=" + idIngredient + ", name=" + name + ", ingredientTypeName="
		+ ingredientTypeName + ", price=" + price + "]";
    }
    
}
