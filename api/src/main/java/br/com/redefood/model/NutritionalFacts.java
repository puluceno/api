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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "NutritionalFacts", schema = "RedeFood")
public class NutritionalFacts implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idNutritionalFacts", nullable = false)
	private Short idNutritionalFacts;
	@Basic(optional = false)
	@Column(name = "description", nullable = false, length = 45)
	private String description;
	@Basic(optional = false)
	@Column(name = "unity", nullable = false, length = 5)
	private String unity;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idNutritionalFacts", fetch = FetchType.LAZY)
	private List<IngredienthasNutritionalFacts> ingredienthasNutritionalFactsList;

	public NutritionalFacts() {
	}

	public NutritionalFacts(Short idNutritionalFacts) {
		this.idNutritionalFacts = idNutritionalFacts;
	}

	public NutritionalFacts(Short idNutritionalFacts, String description, String unity) {
		this.idNutritionalFacts = idNutritionalFacts;
		this.description = description;
		this.unity = unity;
	}

	public Short getIdNutritionalFacts() {
		return idNutritionalFacts;
	}

	public void setIdNutritionalFacts(Short idNutritionalFacts) {
		this.idNutritionalFacts = idNutritionalFacts;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnity() {
		return unity;
	}

	public void setUnity(String unity) {
		this.unity = unity;
	}

	@XmlTransient
	public List<IngredienthasNutritionalFacts> getIngredienthasNutritionalFactsList() {
		return ingredienthasNutritionalFactsList;
	}

	public void setIngredienthasNutritionalFactsList(
			List<IngredienthasNutritionalFacts> ingredienthasNutritionalFactsList) {
		this.ingredienthasNutritionalFactsList = ingredienthasNutritionalFactsList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((idNutritionalFacts == null) ? 0 : idNutritionalFacts.hashCode());
		result = prime * result
				+ ((ingredienthasNutritionalFactsList == null) ? 0 : ingredienthasNutritionalFactsList.hashCode());
		result = prime * result + ((unity == null) ? 0 : unity.hashCode());
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
		NutritionalFacts other = (NutritionalFacts) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (idNutritionalFacts == null) {
			if (other.idNutritionalFacts != null)
				return false;
		} else if (!idNutritionalFacts.equals(other.idNutritionalFacts))
			return false;
		if (ingredienthasNutritionalFactsList == null) {
			if (other.ingredienthasNutritionalFactsList != null)
				return false;
		} else if (!ingredienthasNutritionalFactsList.equals(other.ingredienthasNutritionalFactsList))
			return false;
		if (unity == null) {
			if (other.unity != null)
				return false;
		} else if (!unity.equals(other.unity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NutritionalFacts [idNutritionalFacts=" + idNutritionalFacts + ", description=" + description
				+ ", unity=" + unity + "]";
	}

}
