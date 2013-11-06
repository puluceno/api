/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Ingredient_has_NutritionalFacts", schema = "RedeFood")
public class IngredienthasNutritionalFacts implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idIngredientHasNutritionalFacts", nullable = false)
	private Integer idIngredientHasNutritionalFacts;
	@Basic(optional = false)
	@Column(name = "servingSize", nullable = false)
	private short servingSize;
	@Basic(optional = false)
	@Column(name = "servingSizeUnity", nullable = false, length = 5)
	private String servingSizeUnity;
	@Basic(optional = false)
	@Column(name = "amount", nullable = false)
	private short amount;
	@JoinColumn(name = "idNutritionalFacts", referencedColumnName = "idNutritionalFacts", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private NutritionalFacts idNutritionalFacts;
	@JoinColumn(name = "idIngredient", referencedColumnName = "idIngredient", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Ingredient idIngredient;

	public IngredienthasNutritionalFacts() {
	}

	public IngredienthasNutritionalFacts(Integer idIngredientHasNutritionalFacts) {
		this.idIngredientHasNutritionalFacts = idIngredientHasNutritionalFacts;
	}

	public IngredienthasNutritionalFacts(Integer idIngredientHasNutritionalFacts, short servingSize,
			String servingSizeUnity, short amount) {
		this.idIngredientHasNutritionalFacts = idIngredientHasNutritionalFacts;
		this.servingSize = servingSize;
		this.servingSizeUnity = servingSizeUnity;
		this.amount = amount;
	}

	public Integer getIdIngredientHasNutritionalFacts() {
		return idIngredientHasNutritionalFacts;
	}

	public void setIdIngredientHasNutritionalFacts(Integer idIngredientHasNutritionalFacts) {
		this.idIngredientHasNutritionalFacts = idIngredientHasNutritionalFacts;
	}

	public short getServingSize() {
		return servingSize;
	}

	public void setServingSize(short servingSize) {
		this.servingSize = servingSize;
	}

	public String getServingSizeUnity() {
		return servingSizeUnity;
	}

	public void setServingSizeUnity(String servingSizeUnity) {
		this.servingSizeUnity = servingSizeUnity;
	}

	public short getAmount() {
		return amount;
	}

	public void setAmount(short amount) {
		this.amount = amount;
	}

	public NutritionalFacts getIdNutritionalFacts() {
		return idNutritionalFacts;
	}

	public void setIdNutritionalFacts(NutritionalFacts idNutritionalFacts) {
		this.idNutritionalFacts = idNutritionalFacts;
	}

	public Ingredient getIdIngredient() {
		return idIngredient;
	}

	public void setIdIngredient(Ingredient idIngredient) {
		this.idIngredient = idIngredient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + ((idIngredient == null) ? 0 : idIngredient.hashCode());
		result = prime * result
				+ ((idIngredientHasNutritionalFacts == null) ? 0 : idIngredientHasNutritionalFacts.hashCode());
		result = prime * result + ((idNutritionalFacts == null) ? 0 : idNutritionalFacts.hashCode());
		result = prime * result + servingSize;
		result = prime * result + ((servingSizeUnity == null) ? 0 : servingSizeUnity.hashCode());
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
		IngredienthasNutritionalFacts other = (IngredienthasNutritionalFacts) obj;
		if (amount != other.amount)
			return false;
		if (idIngredient == null) {
			if (other.idIngredient != null)
				return false;
		} else if (!idIngredient.equals(other.idIngredient))
			return false;
		if (idIngredientHasNutritionalFacts == null) {
			if (other.idIngredientHasNutritionalFacts != null)
				return false;
		} else if (!idIngredientHasNutritionalFacts.equals(other.idIngredientHasNutritionalFacts))
			return false;
		if (idNutritionalFacts == null) {
			if (other.idNutritionalFacts != null)
				return false;
		} else if (!idNutritionalFacts.equals(other.idNutritionalFacts))
			return false;
		if (servingSize != other.servingSize)
			return false;
		if (servingSizeUnity == null) {
			if (other.servingSizeUnity != null)
				return false;
		} else if (!servingSizeUnity.equals(other.servingSizeUnity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IngredienthasNutritionalFacts [idIngredientHasNutritionalFacts=" + idIngredientHasNutritionalFacts
				+ ", servingSize=" + servingSize + ", servingSizeUnity=" + servingSizeUnity + ", amount=" + amount
				+ "]";
	}
}