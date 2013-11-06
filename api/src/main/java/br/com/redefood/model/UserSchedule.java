/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "UserSchedule", schema = "RedeFood")
public class UserSchedule implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected UserSchedulePK userSchedulePK;
	@Basic(optional = false)
	@Column(name = "deliveryHour", nullable = false)
	@Temporal(TemporalType.TIME)
	private Date deliveryHour;
	@JoinColumn(name = "idDaysOfWeek", referencedColumnName = "idOrders", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Orders idDaysOfWeek;
	@JoinColumn(name = "idOpenTime", referencedColumnName = "idOpenTime", nullable = false, insertable = false, updatable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private OpenTime openTime;
	@JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = false, insertable = false, updatable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private User user;

	public UserSchedule() {
	}

	public UserSchedule(UserSchedulePK userSchedulePK) {
		this.userSchedulePK = userSchedulePK;
	}

	public UserSchedule(UserSchedulePK userSchedulePK, Date deliveryHour) {
		this.userSchedulePK = userSchedulePK;
		this.deliveryHour = deliveryHour;
	}

	public UserSchedule(int idUser, int idOpenTime) {
		userSchedulePK = new UserSchedulePK(idUser, idOpenTime);
	}

	public UserSchedulePK getUserSchedulePK() {
		return userSchedulePK;
	}

	public void setUserSchedulePK(UserSchedulePK userSchedulePK) {
		this.userSchedulePK = userSchedulePK;
	}

	public Date getDeliveryHour() {
		return deliveryHour;
	}

	public void setDeliveryHour(Date deliveryHour) {
		this.deliveryHour = deliveryHour;
	}

	public Orders getIdDaysOfWeek() {
		return idDaysOfWeek;
	}

	public void setIdDaysOfWeek(Orders idDaysOfWeek) {
		this.idDaysOfWeek = idDaysOfWeek;
	}

	public OpenTime getOpenTime() {
		return openTime;
	}

	public void setOpenTime(OpenTime openTime) {
		this.openTime = openTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDaysOfWeek == null) ? 0 : idDaysOfWeek.hashCode());
		result = prime * result + ((openTime == null) ? 0 : openTime.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((userSchedulePK == null) ? 0 : userSchedulePK.hashCode());
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
		UserSchedule other = (UserSchedule) obj;
		if (idDaysOfWeek == null) {
			if (other.idDaysOfWeek != null)
				return false;
		} else if (!idDaysOfWeek.equals(other.idDaysOfWeek))
			return false;
		if (openTime == null) {
			if (other.openTime != null)
				return false;
		} else if (!openTime.equals(other.openTime))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (userSchedulePK == null) {
			if (other.userSchedulePK != null)
				return false;
		} else if (!userSchedulePK.equals(other.userSchedulePK))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserSchedule [userSchedulePK=" + userSchedulePK + ", idDaysOfWeek=" + idDaysOfWeek + ", openTime="
				+ openTime + ", user=" + user + "]";
	}

}
