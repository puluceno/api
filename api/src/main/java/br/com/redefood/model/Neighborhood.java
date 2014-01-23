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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Neighborhood", schema = "RedeFood")
@NamedQueries({
	@NamedQuery(name = Neighborhood.FIND_ALL_NEIGHBORHOOD, query = "SELECT n FROM Neighborhood n"),
	@NamedQuery(name = Neighborhood.FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY, query = "SELECT DISTINCT c FROM City c INNER JOIN FETCH c.neighborhoodList n WHERE n.id NOT IN (SELECT d.neighborhood FROM DeliveryArea d WHERE d.idSubsidiary.idSubsidiary = :idSubsidiary)"),
	@NamedQuery(name = Neighborhood.FIND_NEIGHBORHOOD_BY_NAME, query = "SELECT n FROM Neighborhood n WHERE n.name LIKE :name"),
	@NamedQuery(name = Neighborhood.FIND_NEIGHBORHOOD_BY_CITY, query = "SELECT n FROM Neighborhood n WHERE n.idCity.idCity = :idCity"),
	@NamedQuery(name = Neighborhood.FIND_NEIGHBORHOOD_BY_CITY_NAME, query = "SELECT n FROM Neighborhood n WHERE n.idCity.name LIKE :city"),
	@NamedQuery(name = Neighborhood.FIND_NEIGHBORHOOD_BY_NAME_AND_CITY, query = "SELECT n FROM Neighborhood n INNER JOIN n.idCity c WHERE c.name LIKE :city AND n.name LIKE :name"),
	@NamedQuery(name = Neighborhood.FIND_ATTENDED_NEIGHBORHOOD_BY_CITY, query = "SELECT DISTINCT n FROM DeliveryArea d INNER JOIN d.neighborhood n WHERE n.idCity.idCity = :idCity"),
	@NamedQuery(name = Neighborhood.FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY_AND_CITY, query = "SELECT DISTINCT c FROM City c JOIN FETCH c.neighborhoodList n WHERE c.idCity IN (SELECT sc.id.idCity FROM SubsidiaryCity sc WHERE sc.id.idSubsidiary = :idSubsidiary) AND n.id NOT IN (SELECT d.neighborhood.id FROM DeliveryArea d WHERE d.idSubsidiary = :idSubsidiary)") })
public class Neighborhood implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_NEIGHBORHOOD = "FIND_ALL_NEIGHBORHOOD";
	public static final String FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY = "FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY";
	public static final String FIND_NEIGHBORHOOD_BY_NAME = "FIND_NEIGHBORHOOD_BY_NAME";
	public static final String FIND_NEIGHBORHOOD_BY_CITY = "FIND_NEIGHBORHOOD_BY_CITY";
	public static final String FIND_NEIGHBORHOOD_BY_CITY_NAME = "FIND_NEIGHBORHOOD_BY_CITY_NAME";
	public static final String FIND_NEIGHBORHOOD_BY_NAME_AND_CITY = "FIND_NEIGHBORHOOD_BY_NAME_AND_CITY";
	public static final String FIND_ATTENDED_NEIGHBORHOOD_BY_CITY = "FIND_ATTENDED_NEIGHBORHOOD_BY_CITY";
	public static final String FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY_AND_CITY = "FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY_AND_CITY";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idNeighborhood", nullable = false)
	@JsonProperty("id")
	private Short id;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 60)
	@Column(name = "name", nullable = false, length = 60)
	private String name;
	@Size(max = 30)
	@Column(name = "lat", length = 30)
	private String lat;
	@Size(max = 30)
	@Column(name = "lng", length = 30)
	private String lng;
	@JoinColumn(name = "idCity", referencedColumnName = "idCity", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private City idCity;

	public Neighborhood() {
	}

	public Neighborhood(Short idNeighborhood) {
		id = idNeighborhood;
	}

	public Neighborhood(String id) {
		this.id = new Short(id);
	}

	public Neighborhood(Short idNeighborhood, String name) {
		id = idNeighborhood;
		this.name = name;
	}

	public Neighborhood(String name, City city) {
		this.name = name;
		idCity = city;
	}

	@JsonProperty("id")
	public Short getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Short idNeighborhood) {
		id = idNeighborhood;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	@JsonProperty("city")
	@JsonIgnore
	public City getIdCity() {
		return idCity;
	}

	public void setIdCity(City idCity) {
		this.idCity = idCity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idCity == null ? 0 : idCity.hashCode());
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
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
		Neighborhood other = (Neighborhood) obj;
		if (idCity == null) {
			if (other.idCity != null)
				return false;
		} else if (!idCity.equals(other.idCity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		return "Neighborhood [name=" + name + ", City=" + idCity.getName() + "]";
	}

}
