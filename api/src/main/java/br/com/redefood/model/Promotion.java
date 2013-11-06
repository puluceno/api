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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Promotion", schema = "RedeFood")
public class Promotion implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idPromotion", nullable = false)
	private Integer idPromotion;
	@Column(name = "name", length = 60)
	private String name;
	@Column(name = "description", length = 300)
	private String description;
	@Lob
	@Column(name = "imagePopUp")
	private String imagePopUp;
	@Lob
	@Column(name = "imageBanner")
	private String imageBanner;
	@Lob
	@Column(name = "imageInfo")
	private String imageInfo;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idPromotion", fetch = FetchType.LAZY)
	private List<SubsidiaryhasPromotion> subsidiaryhasPromotionList;

	public Promotion() {
	}

	public Promotion(Integer idPromotion) {
		this.idPromotion = idPromotion;
	}

	public Integer getIdPromotion() {
		return idPromotion;
	}

	public void setIdPromotion(Integer idPromotion) {
		this.idPromotion = idPromotion;
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

	public String getImagePopUp() {
		return imagePopUp;
	}

	public void setImagePopUp(String imagePopUp) {
		this.imagePopUp = imagePopUp;
	}

	public String getImageBanner() {
		return imageBanner;
	}

	public void setImageBanner(String imageBanner) {
		this.imageBanner = imageBanner;
	}

	public String getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(String imageInfo) {
		this.imageInfo = imageInfo;
	}

	@XmlTransient
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((idPromotion == null) ? 0 : idPromotion.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subsidiaryhasPromotionList == null) ? 0 : subsidiaryhasPromotionList.hashCode());
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
		Promotion other = (Promotion) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (idPromotion == null) {
			if (other.idPromotion != null)
				return false;
		} else if (!idPromotion.equals(other.idPromotion))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (subsidiaryhasPromotionList == null) {
			if (other.subsidiaryhasPromotionList != null)
				return false;
		} else if (!subsidiaryhasPromotionList.equals(other.subsidiaryhasPromotionList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Promotion [idPromotion=" + idPromotion + ", name=" + name + ", description=" + description + "]";
	}

}
