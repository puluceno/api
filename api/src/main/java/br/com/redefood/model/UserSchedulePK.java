package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author pulu
 */
@Embeddable
public class UserSchedulePK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "idUser", nullable = false)
	private int idUser;
	@Basic(optional = false)
	@Column(name = "idOpenTime", nullable = false)
	private int idOpenTime;

	public UserSchedulePK() {
	}

	public UserSchedulePK(int idUser, int idOpenTime) {
		this.idUser = idUser;
		this.idOpenTime = idOpenTime;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getIdOpenTime() {
		return idOpenTime;
	}

	public void setIdOpenTime(int idOpenTime) {
		this.idOpenTime = idOpenTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idOpenTime;
		result = prime * result + idUser;
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
		UserSchedulePK other = (UserSchedulePK) obj;
		if (idOpenTime != other.idOpenTime)
			return false;
		if (idUser != other.idUser)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserSchedulePK [idUser=" + idUser + ", idOpenTime=" + idOpenTime + "]";
	}

}
