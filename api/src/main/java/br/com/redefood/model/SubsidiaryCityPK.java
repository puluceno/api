package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SubsidiaryCityPK implements Serializable {
	private static final long serialVersionUID = 544690773785165917L;

	@Column(name = "idSubsidiary", nullable = false)
	private Short idSubsidiary;
	@Basic(optional = false)
	@Column(name = "idCity", nullable = false)
	private Short idCity;

	public SubsidiaryCityPK() {
	}

	public SubsidiaryCityPK(Short idSubsidiary, Short idCity) {
		this.idSubsidiary = idSubsidiary;
		this.idCity = idCity;
	}

	public Short getIdSubsidiary() {
		return idSubsidiary;
	}

	public void setIdSubsidiary(Short idSubsidiary) {
		this.idSubsidiary = idSubsidiary;
	}

	public Short getIdCity() {
		return idCity;
	}

	public void setIdCity(Short idCity) {
		this.idCity = idCity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idCity == null ? 0 : idCity.hashCode());
		result = prime * result + (idSubsidiary == null ? 0 : idSubsidiary.hashCode());
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
		SubsidiaryCityPK other = (SubsidiaryCityPK) obj;
		if (idCity == null) {
			if (other.idCity != null)
				return false;
		} else if (!idCity.equals(other.idCity))
			return false;
		if (idSubsidiary == null) {
			if (other.idSubsidiary != null)
				return false;
		} else if (!idSubsidiary.equals(other.idSubsidiary))
			return false;
		return true;
	}



}
