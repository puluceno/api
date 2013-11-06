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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "MealRating", schema = "RedeFood")
public class MealRating implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idMealRating", nullable = false)
	private Integer idMealRating;
	@Column(name = "mealRating")
	private Short mealRating;
	@Column(name = "comment", length = 300)
	private String comment;
	@JoinColumn(name = "idMeal", referencedColumnName = "idMeal", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Meal meal;
	@JoinColumn(name = "idRating", referencedColumnName = "idRating", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Rating rating;

	public MealRating() {
	}

	public MealRating(Integer idMealRating) {
		this.idMealRating = idMealRating;
	}

	public MealRating(Short mealRating, String comment) {
		this.mealRating = mealRating;
		this.comment = comment;
	}

	@JsonIgnore
	public Integer getIdMealRating() {
		return idMealRating;
	}

	@JsonIgnore
	public void setIdMealRating(Integer idMealRating) {
		this.idMealRating = idMealRating;
	}

	public Short getMealRating() {
		return mealRating;
	}

	public void setMealRating(Short mealRating) {
		this.mealRating = mealRating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@JsonIgnore
	public Meal getMeal() {
		return meal;
	}

	@JsonIgnore
	public void setMeal(Meal meal) {
		this.meal = meal;
	}

	@JsonIgnore
	public Rating getRating() {
		return rating;
	}

	@JsonIgnore
	public void setRating(Rating rating) {
		this.rating = rating;
	}

}
