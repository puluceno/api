/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Subsidiary_Module", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = SubsidiaryModule.FIND_ACTIVE_BY_SUBSIDIARY, query = "SELECT s FROM SubsidiaryModule s WHERE s.subsidiary.idSubsidiary =:idSubsidiary AND s.active = true ORDER BY s.module.paymentType ASC"),
		@NamedQuery(name = SubsidiaryModule.FIND_ACTIVE_BY_SUBSIDIARY_AND_MODULE_TYPE, query = "SELECT s FROM SubsidiaryModule s WHERE s.subsidiary.idSubsidiary =:idSubsidiary AND s.active = true AND s.module.moduleType = :moduleType ORDER BY s.module.paymentType ASC") })
public class SubsidiaryModule implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ACTIVE_BY_SUBSIDIARY = "FIND_ACTIVE_BY_SUBSIDIARY";
	public static final String FIND_ACTIVE_BY_SUBSIDIARY_AND_MODULE_TYPE = "FIND_ACTIVE_BY_SUBSIDIARY_AND_MODULE_TYPE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idSubsidiary_Module", nullable = false)
	private Short idSubsidiaryModule;
	@Basic(optional = false)
	@Column(name = "start_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Max(value = 9999)
	@Min(value = 0)
	@Column(name = "price", precision = 6, scale = 2)
	private Double price;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;
	@JoinColumn(name = "idModule", referencedColumnName = "idModule", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Module module;
	@Column(name = "active")
	private Boolean active = true;
	@Column(name = "charged")
	private Boolean charged = false;
	@Column(name = "deactivate")
	private Boolean deactivate = false;
	@Column(name = "requestDeactivate_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date requestDeactivateDate;
	@Column(name = "dateDeactivated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDeactivated;

	public SubsidiaryModule() {
	}

	public SubsidiaryModule(Short idSubsidiaryModule) {
		this.idSubsidiaryModule = idSubsidiaryModule;
	}

	public SubsidiaryModule(Short idSubsidiaryModule, Date startDate) {
		this.idSubsidiaryModule = idSubsidiaryModule;
		this.startDate = startDate;
	}

	public SubsidiaryModule(Date startDate, Double price, Subsidiary subsidiary, Module module, Boolean active) {
		this.startDate = startDate;
		this.price = price;
		this.subsidiary = subsidiary;
		this.module = module;
		this.active = active;
	}

	public Short getId() {
		return idSubsidiaryModule;
	}

	public void setId(Short idSubsidiaryModule) {
		this.idSubsidiaryModule = idSubsidiaryModule;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary idSubsidiary) {
		subsidiary = idSubsidiary;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module idModule) {
		module = idModule;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getCharged() {
		return charged;
	}

	public void setCharged(Boolean charged) {
		this.charged = charged;
	}

	public Boolean getDeactivate() {
		return deactivate;
	}

	public void setDeactivate(Boolean deactivate) {
		this.deactivate = deactivate;
	}

	public Date getResquestDeactivateDate() {
		return requestDeactivateDate;
	}

	public void setRequestDeactivateDate(Date deactivateDate) {
		requestDeactivateDate = deactivateDate;
	}

	public Date getDateDeactivated() {
		return dateDeactivated;
	}

	public void setDateDeactivated(Date dateDeactivated) {
		this.dateDeactivated = dateDeactivated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
		result = prime * result + ((idSubsidiaryModule == null) ? 0 : idSubsidiaryModule.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
		SubsidiaryModule other = (SubsidiaryModule) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		if (idSubsidiaryModule == null) {
			if (other.idSubsidiaryModule != null)
				return false;
		} else if (!idSubsidiaryModule.equals(other.idSubsidiaryModule))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubsidiaryModule [idSubsidiaryModule=" + idSubsidiaryModule + ", startDate=" + startDate + ", price="
				+ price + ", idSubsidiary=" + subsidiary + ", idModule=" + module + "]";
	}

}
