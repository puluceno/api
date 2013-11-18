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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "OpenTime", schema = "RedeFood")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = OpenTime.FIND_OPEN_TIME_BY_SUBSIDIARY, query = "SELECT o FROM OpenTime o WHERE subsidiary.idSubsidiary = :idSubsidiary ORDER BY o.daysOfWeek.id ASC, o.localOpenTime ASC"),
	@NamedQuery(name = OpenTime.FIND_OPEN_TIME_BY_SUBSIDIARY_AND_DAY, query = "SELECT ot FROM OpenTime ot WHERE ot.subsidiary.idSubsidiary = :idSubsidiary AND ot.daysOfWeek.id = :dayOfWeek AND ot.localOpenTime = :localOpenTime") })
public class OpenTime implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_OPEN_TIME_BY_SUBSIDIARY = "FIND_OPEN_TIME_BY_SUBSIDIARY";
	public static final String FIND_OPEN_TIME_BY_SUBSIDIARY_AND_DAY = "FIND_OPEN_TIME_BY_SUBSIDIARY_AND_DAY";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idOpenTime", nullable = false)
	private Integer idOpenTime;
	@Basic(optional = false)
	@Column(name = "open", nullable = false)
	private String open;
	@Basic(optional = false)
	@Column(name = "close", nullable = false)
	private String close;
	@Basic(optional = false)
	@Column(name = "localOpenTime")
	private Boolean localOpenTime;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "openTime", fetch = FetchType.LAZY)
	private List<UserSchedule> userScheduleList;
	@JoinColumn(name = "idDaysOfWeek", referencedColumnName = "idDaysOfWeek", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@org.hibernate.annotations.OrderBy(clause = "idDaysOfWeek asc")
	private DaysOfWeek daysOfWeek;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idOpenTime", fetch = FetchType.LAZY)
	private List<SubsidiaryhasPromotion> subsidiaryhasPromotionList;

	public OpenTime() {
	}

	public OpenTime(Integer idOpenTime) {
		this.idOpenTime = idOpenTime;
	}

	public OpenTime(Integer idOpenTime, String open, String close) {
		this.idOpenTime = idOpenTime;
		this.open = open;
		this.close = close;
	}

	public OpenTime(String open, String close, DaysOfWeek idDaysOfWeek) {
		this.open = open;
		this.close = close;
		daysOfWeek = idDaysOfWeek;
	}

	public Integer getId() {
		return idOpenTime;
	}

	public void setId(Integer idOpenTime) {
		this.idOpenTime = idOpenTime;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}

	public Boolean getLocalOpenTime() {
		return localOpenTime;
	}

	public void setLocalTime(Boolean localOpenTime) {
		this.localOpenTime = localOpenTime;
	}

	@XmlTransient
	@JsonIgnore
	public List<UserSchedule> getUserScheduleList() {
		return userScheduleList;
	}

	public void setUserScheduleList(List<UserSchedule> userScheduleList) {
		this.userScheduleList = userScheduleList;
	}

	public DaysOfWeek getDayOfWeek() {
		return daysOfWeek;
	}

	public void setDayOfWeek(DaysOfWeek idDaysOfWeek) {
		daysOfWeek = idDaysOfWeek;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary idSubsidiary) {
		subsidiary = idSubsidiary;
	}

	@XmlTransient
	@JsonIgnore
	public List<SubsidiaryhasPromotion> getSubsidiaryhasPromotionList() {
		return subsidiaryhasPromotionList;
	}

	public void setSubsidiaryhasPromotionList(List<SubsidiaryhasPromotion> subsidiaryhasPromotionList) {
		this.subsidiaryhasPromotionList = subsidiaryhasPromotionList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (close == null ? 0 : close.hashCode());
		result = prime * result + (daysOfWeek == null ? 0 : daysOfWeek.hashCode());
		result = prime * result + (idOpenTime == null ? 0 : idOpenTime.hashCode());
		result = prime * result + (subsidiary == null ? 0 : subsidiary.hashCode());
		result = prime * result + (open == null ? 0 : open.hashCode());
		result = prime * result + (subsidiaryhasPromotionList == null ? 0 : subsidiaryhasPromotionList.hashCode());
		result = prime * result + (userScheduleList == null ? 0 : userScheduleList.hashCode());
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
		OpenTime other = (OpenTime) obj;
		if (close == null) {
			if (other.close != null)
				return false;
		} else if (!close.equals(other.close))
			return false;
		if (daysOfWeek == null) {
			if (other.daysOfWeek != null)
				return false;
		} else if (!daysOfWeek.equals(other.daysOfWeek))
			return false;
		if (idOpenTime == null) {
			if (other.idOpenTime != null)
				return false;
		} else if (!idOpenTime.equals(other.idOpenTime))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		if (open == null) {
			if (other.open != null)
				return false;
		} else if (!open.equals(other.open))
			return false;
		if (subsidiaryhasPromotionList == null) {
			if (other.subsidiaryhasPromotionList != null)
				return false;
		} else if (!subsidiaryhasPromotionList.equals(other.subsidiaryhasPromotionList))
			return false;
		if (userScheduleList == null) {
			if (other.userScheduleList != null)
				return false;
		} else if (!userScheduleList.equals(other.userScheduleList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return (daysOfWeek.getName() == null ? daysOfWeek.getId() : daysOfWeek.getName()) + " [open=" + open
				+ ", close=" + close + "]";
	}
}