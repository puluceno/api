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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Address", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = Address.FIND_ADDRESS_BY_USER_ID, query = "SELECT a FROM Address a WHERE a.user.idUser = :idUser") })
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ADDRESS_BY_USER_ID = "FIND_ADDRESS_BY_USER_ID";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idAddress", nullable = false)
	@JsonProperty("id")
	private Integer idAddress;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 9)
	@Column(name = "zipcode", nullable = false, length = 9)
	private String zipcode;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "street", nullable = false, length = 80)
	private String street;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 5)
	@Column(name = "number", nullable = false, length = 5)
	private String number;
	@Size(max = 80)
	@Column(name = "complement", length = 80)
	private String complement;
	@Column(name = "name", length = 60)
	@Basic(optional = false)
	private String name;
	@Size(max = 30)
	@Column(name = "lat", length = 30)
	private String lat;
	@Size(max = 30)
	@Column(name = "lng", length = 30)
	private String lng;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "idAddress", fetch = FetchType.LAZY)
	private List<Subsidiary> subsidiaryList;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "idAddress", fetch = FetchType.LAZY)
	private List<Employee> employeeList;
	@JoinColumn(name = "idNeighborhood", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Neighborhood neighborhood;
	@JoinColumn(name = "idUser", referencedColumnName = "idUser")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	@JoinColumn(name = "idCity", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private City idCity;

	public Address() {
	}

	public Address(Integer idAddress) {
		this.idAddress = idAddress;
	}

	public Address(Integer idAddress, String zipcode, String street, String number) {
		this.idAddress = idAddress;
		this.zipcode = zipcode;
		this.street = street;
		this.number = number;
	}

	public Address(String zipcode, String street, String number, String complement, String name,
			Neighborhood neighborhood, City idCity) {
		this.zipcode = zipcode;
		this.street = street;
		this.number = number;
		this.complement = complement;
		this.name = name;
		this.neighborhood = neighborhood;
		this.idCity = idCity;
	}

	@JsonProperty("id")
	public Integer getId() {
		return idAddress;
	}

	@JsonProperty("id")
	public void setId(Integer idAddress) {
		this.idAddress = idAddress;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
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

	@XmlTransient
	@JsonIgnore
	public List<Subsidiary> getSubsidiaryList() {
		return subsidiaryList;
	}

	@XmlTransient
	@JsonIgnore
	public void setSubsidiaryList(List<Subsidiary> subsidiaryList) {
		this.subsidiaryList = subsidiaryList;
	}

	@XmlTransient
	@JsonIgnore
	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	@XmlTransient
	@JsonIgnore
	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	@JsonProperty("neighborhood")
	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	@JsonProperty("neighborhood")
	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User idUser) {
		user = idUser;
	}

	@JsonProperty("city")
	public City getCity() {
		return idCity;
	}

	@JsonProperty("city")
	public void setCity(City idCity) {
		this.idCity = idCity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((complement == null) ? 0 : complement.hashCode());
		result = prime * result + ((employeeList == null) ? 0 : employeeList.hashCode());
		result = prime * result + ((idCity == null) ? 0 : idCity.hashCode());
		result = prime * result + ((neighborhood == null) ? 0 : neighborhood.hashCode());
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lng == null) ? 0 : lng.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		result = prime * result + ((subsidiaryList == null) ? 0 : subsidiaryList.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((zipcode == null) ? 0 : zipcode.hashCode());
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
		Address other = (Address) obj;
		if (complement == null) {
			if (other.complement != null)
				return false;
		} else if (!complement.equals(other.complement))
			return false;
		if (employeeList == null) {
			if (other.employeeList != null)
				return false;
		} else if (!employeeList.equals(other.employeeList))
			return false;
		if (idCity == null) {
			if (other.idCity != null)
				return false;
		} else if (!idCity.equals(other.idCity))
			return false;
		if (neighborhood == null) {
			if (other.neighborhood != null)
				return false;
		} else if (!neighborhood.equals(other.neighborhood))
			return false;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lng == null) {
			if (other.lng != null)
				return false;
		} else if (!lng.equals(other.lng))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (subsidiaryList == null) {
			if (other.subsidiaryList != null)
				return false;
		} else if (!subsidiaryList.equals(other.subsidiaryList))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (zipcode == null) {
			if (other.zipcode != null)
				return false;
		} else if (!zipcode.equals(other.zipcode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Address [idAddress=" + idAddress + ", zipcode=" + zipcode + ", street=" + street + ", number=" + number
				+ ", complement=" + complement + ", idNeighborhood=" + neighborhood + ", idCity=" + idCity + "]";
	}

}
