/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "RestaurantType", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = RestaurantType.FIND_ALL_RESTAURANT_TYPE, query = "SELECT r FROM RestaurantType r"),
		@NamedQuery(name = RestaurantType.FIND_BY_RESTAURANT, query = "SELECT r FROM RestaurantType r INNER JOIN r.restaurants rest WHERE rest.idRestaurant = :idRestaurant") })
public class RestaurantType implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_RESTAURANT_TYPE = "FIND_ALL_RESTAURANT_TYPE";
	public static final String FIND_BY_RESTAURANT = "FIND_BY_RESTAURANT";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idRestaurantType", nullable = false)
	private Short idRestaurantType;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 60)
	private String name;
	@Basic(optional = false)
	@Column(name = "description", nullable = false, length = 300)
	private String description;
	@ManyToMany(mappedBy = "restaurantTypes", fetch = FetchType.LAZY)
	private List<Restaurant> restaurants;

	public RestaurantType() {
	}

	public RestaurantType(Short idRestaurantType) {
		this.idRestaurantType = idRestaurantType;
	}

	public RestaurantType(Short idRestaurantType, String name, String description) {
		this.idRestaurantType = idRestaurantType;
		this.name = name;
		this.description = description;
	}

	@JsonProperty("id")
	public Short getIdRestaurantType() {
		return idRestaurantType;
	}

	@JsonProperty("id")
	public void setIdRestaurantType(Short idRestaurantType) {
		this.idRestaurantType = idRestaurantType;
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

	@XmlTransient
	public List<Restaurant> getRestaurants() {
		return restaurants;
	}

	public void setRestaurants(List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((idRestaurantType == null) ? 0 : idRestaurantType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((restaurants == null) ? 0 : restaurants.hashCode());
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
		RestaurantType other = (RestaurantType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (idRestaurantType == null) {
			if (other.idRestaurantType != null)
				return false;
		} else if (!idRestaurantType.equals(other.idRestaurantType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (restaurants == null) {
			if (other.restaurants != null)
				return false;
		} else if (!restaurants.equals(other.restaurants))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RestaurantType [idRestaurantType=" + idRestaurantType + ", name=" + name + ", description="
				+ description + "]";
	}

}
