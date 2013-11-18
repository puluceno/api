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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "DaysOfWeek", schema = "RedeFood")
public class DaysOfWeek implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Short SUNDAY = 1;
	public static final Short MONDAY = 2;
	public static final Short TUESDAY = 3;
	public static final Short WEDNESDAY = 4;
	public static final Short THURSDAY = 5;
	public static final Short FRIDAY = 6;
	public static final Short SATURDAY = 7;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idDaysOfWeek", nullable = false)
	private Short id;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 20)
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "daysOfWeek", fetch = FetchType.LAZY)
	private List<OpenTime> openTimeList;

	public DaysOfWeek() {
	}

	public DaysOfWeek(Short idDaysOfWeek) {
		id = idDaysOfWeek;
	}

	public DaysOfWeek(Short idDaysOfWeek, String name) {
		id = idDaysOfWeek;
		this.name = name;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short idDaysOfWeek) {
		id = idDaysOfWeek;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	@JsonIgnore
	public List<OpenTime> getOpenTimeList() {
		return openTimeList;
	}

	public void setOpenTimeList(List<OpenTime> openTimeList) {
		this.openTimeList = openTimeList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
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
		DaysOfWeek other = (DaysOfWeek) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DaysOfWeek [idDaysOfWeek=" + id + ", name=" + name + "]";
	}

}
