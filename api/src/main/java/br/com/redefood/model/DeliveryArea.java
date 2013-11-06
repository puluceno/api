package br.com.redefood.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "DeliveryArea", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = DeliveryArea.FIND_DELIVER_EXISTS, query = "SELECT d FROM DeliveryArea d WHERE d.idSubsidiary.idSubsidiary = :idSubsidiary AND d.neighborhood.id = :idNeighborhood"),
		@NamedQuery(name = DeliveryArea.FIND_DELIVERY_AREA_BY_SUBSIDIARY, query = "SELECT d FROM DeliveryArea d WHERE idSubsidiary.idSubsidiary = :idSubsidiary ORDER BY d.neighborhood.name"), })
public class DeliveryArea implements Serializable {
	public static final String FIND_DELIVER_EXISTS = "FIND_DELIVER_EXISTS";
	public static final String FIND_DELIVERY_AREA_BY_SUBSIDIARY = "FIND_DELIVERY_AREA_BY_SUBSIDIARY";

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idDeliveryArea", nullable = false)
	@JsonProperty("id")
	private Integer idDeliveryArea;
	@Max(value = 99)
	@Min(value = 0)
	@Column(name = "tax", precision = 4, scale = 2)
	private Double tax;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private Subsidiary idSubsidiary;
	@JoinColumn(name = "idNeighborhood", referencedColumnName = "idNeighborhood", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JsonProperty("neighborhood")
	private Neighborhood neighborhood;

	public DeliveryArea() {
	}

	public DeliveryArea(Integer idDeliveryArea) {
		this.idDeliveryArea = idDeliveryArea;
	}

	public DeliveryArea(Double tax, Neighborhood neighborhood) {
		this.tax = tax;
		this.neighborhood = neighborhood;
	}

	public DeliveryArea(Double tax) {
		this.tax = tax;
	}

	public DeliveryArea(Double tax, Subsidiary idSubsidiary, Neighborhood neighborhood) {
		this.tax = tax;
		this.idSubsidiary = idSubsidiary;
		this.neighborhood = neighborhood;
	}

	@JsonProperty("id")
	public Integer getIdDeliveryArea() {
		return idDeliveryArea;
	}

	@JsonProperty("id")
	public void setIdDeliveryArea(Integer idDeliveryArea) {
		this.idDeliveryArea = idDeliveryArea;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@JsonProperty("subsidiary")
	@JsonIgnore
	public Subsidiary getIdSubsidiary() {
		return idSubsidiary;
	}

	@JsonProperty("subsidiary")
	@JsonIgnore
	public void setIdSubsidiary(Subsidiary idSubsidiary) {
		this.idSubsidiary = idSubsidiary;
	}

	@JsonProperty("neighborhood")
	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	@JsonProperty("neighborhood")
	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDeliveryArea == null) ? 0 : idDeliveryArea.hashCode());
		result = prime * result + ((neighborhood == null) ? 0 : neighborhood.hashCode());
		result = prime * result + ((idSubsidiary == null) ? 0 : idSubsidiary.hashCode());
		result = prime * result + ((tax == null) ? 0 : tax.hashCode());
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
		DeliveryArea other = (DeliveryArea) obj;
		if (idDeliveryArea == null) {
			if (other.idDeliveryArea != null)
				return false;
		} else if (!idDeliveryArea.equals(other.idDeliveryArea))
			return false;
		if (neighborhood == null) {
			if (other.neighborhood != null)
				return false;
		} else if (!neighborhood.equals(other.neighborhood))
			return false;
		if (idSubsidiary == null) {
			if (other.idSubsidiary != null)
				return false;
		} else if (!idSubsidiary.equals(other.idSubsidiary))
			return false;
		if (tax == null) {
			if (other.tax != null)
				return false;
		} else if (!tax.equals(other.tax))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeliveryArea [idDeliveryArea=" + idDeliveryArea + ", tax=" + tax + ", idSubsidiary=" + idSubsidiary
				+ ", neighborhood=" + neighborhood + "]";
	}

}
