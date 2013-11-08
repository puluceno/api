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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "MealOrder", schema = "RedeFood")
public class MealOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMealOrder")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "idMeal", nullable = false)
    private Integer idMeal;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 45)
    private String name;
    @Column(name = "idMealType")
    private Integer idMealType;
    @Column(name = "mealTypeName", length = 60)
    private String mealTypeName;
    @Max(value = 9999)
    @Min(value = 0)
    @Basic(optional = false)
    @Column(name = "price", nullable = false, precision = 6, scale = 2)
    private BigDecimal price;
    @JoinColumn(name = "idOrders", referencedColumnName = "idOrders", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Orders order;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mealOrder", fetch = FetchType.LAZY)
    private List<MealOrderIngredient> mealOrderIngredients;
    @Column(name = "note")
    private String note;
    @Column(name = "payed")
    private Boolean payed = false;
    
    public MealOrder() {
    }
    
    public MealOrder(Integer idMealOrder) {
	id = idMealOrder;
    }
    
    public MealOrder(Integer idMeal, String name, BigDecimal price, Orders order,
	    List<MealOrderIngredient> mealOrderIngredients, String note) {
	this.idMeal = idMeal;
	this.name = name;
	this.price = price;
	this.order = order;
	this.mealOrderIngredients = mealOrderIngredients;
	this.note = note;
    }
    
    public MealOrder(Integer idMeal, String name, Integer idMealType, String mealTypeName, BigDecimal price,
	    Orders order, List<MealOrderIngredient> mealOrderIngredients, String note) {
	this.idMeal = idMeal;
	this.name = name;
	this.idMealType = idMealType;
	this.mealTypeName = mealTypeName;
	this.price = price;
	this.order = order;
	this.mealOrderIngredients = mealOrderIngredients;
	this.note = note;
    }
    
    public MealOrder(Integer idMeal, String name, BigDecimal price) {
	this.idMeal = idMeal;
	this.name = name;
	this.price = price;
    }
    
    public MealOrder(Integer idMeal, List<MealOrderIngredient> mealOrderIngredients) {
	this.idMeal = idMeal;
	this.mealOrderIngredients = mealOrderIngredients;
    }
    
    public Integer getId() {
	return id;
    }
    
    public void setId(Integer idMealOrder) {
	id = idMealOrder;
    }
    
    public Integer getIdMeal() {
	return idMeal;
    }
    
    public void setIdMeal(Integer idMeal) {
	this.idMeal = idMeal;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public Integer getIdMealType() {
	return idMealType;
    }
    
    public void setIdMealType(Integer idMealType) {
	this.idMealType = idMealType;
    }
    
    public String getMealTypeName() {
	return mealTypeName;
    }
    
    public void setMealTypeName(String mealTypeName) {
	this.mealTypeName = mealTypeName;
    }
    
    public BigDecimal getPrice() {
	return price;
    }
    
    public void setPrice(BigDecimal price) {
	this.price = price;
    }
    
    @JsonIgnore
    public Orders getOrders() {
	return order;
    }
    
    @JsonIgnore
    public void setOrders(Orders idOrders) {
	order = idOrders;
    }
    
    public List<MealOrderIngredient> getMealOrderIngredients() {
	return mealOrderIngredients;
    }
    
    public void setMealOrderIngredients(List<MealOrderIngredient> mealOrderIngredientList) {
	mealOrderIngredients = mealOrderIngredientList;
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
	result = prime * result + (idMeal == null ? 0 : idMeal.hashCode());
	result = prime * result + (idMealType == null ? 0 : idMealType.hashCode());
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
	MealOrder other = (MealOrder) obj;
	if (idMeal == null) {
	    if (other.idMeal != null)
		return false;
	} else if (!idMeal.equals(other.idMeal))
	    return false;
	if (idMealType == null) {
	    if (other.idMealType != null)
		return false;
	} else if (!idMealType.equals(other.idMealType))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "MealOrder [id=" + id + ", idMeal=" + idMeal + ", name=" + name + ", idMealType=" + idMealType
		+ ", mealTypeName=" + mealTypeName + ", price=" + price + ", order=" + order
		+ ", mealOrderIngredients=" + mealOrderIngredients + ", note=" + note + ", payed=" + payed + "]";
    }
    
}
