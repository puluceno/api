package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Subsidiary_has_City", schema = "RedeFood")
public class SubsidiaryCity implements Serializable {
	private static final long serialVersionUID = -5530071612301139133L;

	@EmbeddedId
	protected SubsidiaryCityPK id;

	public SubsidiaryCity(SubsidiaryCityPK id) {
		this.id = id;
	}

	public SubsidiaryCity() {
	}

	public SubsidiaryCityPK getId() {
		return id;
	}

	public void setId(SubsidiaryCityPK id) {
		this.id = id;
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
		SubsidiaryCity other = (SubsidiaryCity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
