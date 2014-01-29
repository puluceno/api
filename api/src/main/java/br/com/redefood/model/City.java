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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "City", schema = "RedeFood")
@NamedQueries({
	@NamedQuery(name = City.FIND_ALL_CITY, query = "SELECT c FROM City c"),
	@NamedQuery(name = City.FIND_CITY_BY_ID, query = "SELECT c FROM City c WHERE c.idCity = :idCity"),
	@NamedQuery(name = City.FIND_CITY_BY_NAME, query = "SELECT c FROM City c WHERE c.name LIKE :name"),
	@NamedQuery(name = City.FIND_ATTENDED_CITY, query = "SELECT DISTINCT n.idCity FROM DeliveryArea d INNER JOIN d.neighborhood n"),
	//@NamedQuery(name = City.FIND_AVAILABLE_CITY_BY_SUBSIDIARY_AND_STATE, query = "")
})
public class City implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_CITY = "FIND_ALL_CITY";
	public static final String FIND_CITY_BY_ID = "FIND_CITY_BY_ID";
	public static final String FIND_CITY_BY_NAME = "FIND_CITY_BY_NAME";
	public static final String FIND_ATTENDED_CITY = "FIND_ATTENDED_CITY";
	public static final String FIND_AVAILABLE_CITY_BY_SUBSIDIARY_AND_STATE = "FIND_AVAILABLE_CITY_BY_SUBSIDIARY_AND_STATE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idCity", nullable = false)
	@JsonProperty("id")
	private Short idCity;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "name", nullable = false, length = 40)
	@JsonProperty("name")
	private String name;
	@Lob
	@Column(name = "image")
	private String image;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "idCity", fetch = FetchType.LAZY)
	@JsonProperty("neighborhoods")
	private List<Neighborhood> neighborhoodList;
	@JoinColumn(name = "state", referencedColumnName = "idState", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private State state;

	public City() {
	}

	public City(Short idCity) {
		this.idCity = idCity;
	}

	public City(String id) {
		idCity = new Short(id);
	}

	public City(Short idCity, String name) {
		this.idCity = idCity;
		this.name = name;
	}

	public City(String name, State state) {
		this.name = name;
		this.state = state;
	}

	@JsonProperty("id")
	public Short getId() {
		return idCity;
	}

	@JsonProperty("id")
	public void setId(Short idCity) {
		this.idCity = idCity;
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

	@JsonProperty("neighborhoods")
	public List<Neighborhood> getNeighborhoods() {
		return neighborhoodList;
	}

	@JsonProperty("neighborhoods")
	public void setNeighborhoods(List<Neighborhood> neighborhoodList) {
		this.neighborhoodList = neighborhoodList;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idCity == null ? 0 : idCity.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (neighborhoodList == null ? 0 : neighborhoodList.hashCode());
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
		City other = (City) obj;
		if (idCity == null) {
			if (other.idCity != null)
				return false;
		} else if (!idCity.equals(other.idCity))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (neighborhoodList == null) {
			if (other.neighborhoodList != null)
				return false;
		} else if (!neighborhoodList.equals(other.neighborhoodList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "City [idCity=" + idCity + ", name=" + name + "]";
	}

}
