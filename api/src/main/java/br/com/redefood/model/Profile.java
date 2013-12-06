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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Profile", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = Profile.FIND_ALL_PROFILES, query = "SELECT p FROM Profile p") })
public class Profile implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_PROFILES = "FIND_ALL_PROFILES";

	/**
	 * Employee = 1
	 */
	public static final Short EMPLOYEE = 1;
	/**
	 * Manager = 2
	 */
	public static final Short MANAGER = 2;
	/**
	 * Owner = 3
	 */
	public static final Short OWNER = 3;
	/**
	 * Motoboy = 4
	 */
	public static final Short MOTOBOY = 4;
	/**
	 * Demonstration = 99
	 */
	public static final Short DEMO = 99;
	/**
	 * RedeFood Admin = 100
	 */
	public static final Short ADMIN_REDEFOOD = 100;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idProfile", nullable = false)
	private Short idProfile;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 45)
	private String name;
	@Basic(optional = false)
	@Column(name = "description", nullable = false, length = 200)
	private String description;
	@Lob
	@Column(name = "image")
	private String image;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "profile", fetch = FetchType.LAZY)
	private List<Employee> employeeList;

	public Profile() {
	}

	public Profile(Short idProfile) {
		this.idProfile = idProfile;
	}

	public Profile(Short idProfile, String name, String description) {
		this.idProfile = idProfile;
		this.name = name;
		this.description = description;
	}

	@JsonProperty("id")
	public Short getId() {
		return idProfile;
	}

	@JsonProperty("id")
	public void setId(Short idProfile) {
		this.idProfile = idProfile;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@XmlTransient
	@JsonIgnore
	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (description == null ? 0 : description.hashCode());
		result = prime * result + (employeeList == null ? 0 : employeeList.hashCode());
		result = prime * result + (idProfile == null ? 0 : idProfile.hashCode());
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
		Profile other = (Profile) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (employeeList == null) {
			if (other.employeeList != null)
				return false;
		} else if (!employeeList.equals(other.employeeList))
			return false;
		if (idProfile == null) {
			if (other.idProfile != null)
				return false;
		} else if (!idProfile.equals(other.idProfile))
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
		return "Profile [idProfile=" + idProfile + ", name=" + name + ", description=" + description + "]";
	}

}
