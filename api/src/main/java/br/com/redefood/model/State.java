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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "State", schema = "RedeFood")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = State.FIND_BY_SHORTNAME, query = "SELECT s FROM State s WHERE s.shortName = :shortName") })
public class State implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_SHORTNAME = "FIND_BY_SHORTNAME";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idState", nullable = false)
	private Short idState;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 50)
	private String name;
	@Column(name = "shortName", length = 3)
	private String shortName;
	@Column(name = "region", length = 45)
	private String region;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "state", fetch = FetchType.LAZY)
	private List<City> cityList;

	public State() {
	}

	public State(Short idState) {
		this.idState = idState;
	}

	public State(Short idState, String name) {
		this.idState = idState;
		this.name = name;
	}

	@JsonProperty("id")
	public Short getId() {
		return idState;
	}

	@JsonProperty("id")
	public void setId(Short idState) {
		this.idState = idState;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@XmlTransient
	@JsonIgnore
	public List<City> getCityList() {
		return cityList;
	}

	public void setCityList(List<City> cityList) {
		this.cityList = cityList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (cityList == null ? 0 : cityList.hashCode());
		result = prime * result + (idState == null ? 0 : idState.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (region == null ? 0 : region.hashCode());
		result = prime * result + (shortName == null ? 0 : shortName.hashCode());
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
		State other = (State) obj;
		if (cityList == null) {
			if (other.cityList != null)
				return false;
		} else if (!cityList.equals(other.cityList))
			return false;
		if (idState == null) {
			if (other.idState != null)
				return false;
		} else if (!idState.equals(other.idState))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "State [idState=" + idState + ", name=" + name + ", shortName=" + shortName + ", region=" + region + "]";
	}

}
