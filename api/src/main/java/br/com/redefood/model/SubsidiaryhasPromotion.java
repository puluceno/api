/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Subsidiary_has_Promotion", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = SubsidiaryhasPromotion.PROMOTION_BY_SUBSIDIARY, query = "SELECT s FROM SubsidiaryhasPromotion s WHERE s.subsidiary.idSubsidiary = :idSubsidiary") })
public class SubsidiaryhasPromotion implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String PROMOTION_BY_SUBSIDIARY = "PROMOTION_BY_SUBSIDIARY";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idSubsidiaryHasPromotion", nullable = false)
	private Integer idSubsidiaryHasPromotion;
	@Max(value = 999)
	@Min(value = 0)
	@Column(name = "mealPrice", precision = 7, scale = 2)
	private BigDecimal mealPrice;
	@Column(name = "beveragePrice", precision = 6, scale = 2)
	private BigDecimal beveragePrice;
	@JoinColumn(name = "idOpenTime", referencedColumnName = "idOpenTime", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private OpenTime idOpenTime;
	@JoinColumn(name = "idMeal", referencedColumnName = "idMeal")
	@ManyToOne(fetch = FetchType.LAZY)
	private Meal idMeal;
	@JoinColumn(name = "idPromotion", referencedColumnName = "idPromotion", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Promotion idPromotion;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;

	public SubsidiaryhasPromotion() {
	}

	public SubsidiaryhasPromotion(Integer idSubsidiaryHasPromotion) {
		this.idSubsidiaryHasPromotion = idSubsidiaryHasPromotion;
	}

	public Integer getIdSubsidiaryHasPromotion() {
		return idSubsidiaryHasPromotion;
	}

	public void setIdSubsidiaryHasPromotion(Integer idSubsidiaryHasPromotion) {
		this.idSubsidiaryHasPromotion = idSubsidiaryHasPromotion;
	}

	public BigDecimal getMealPrice() {
		return mealPrice;
	}

	public void setMealPrice(BigDecimal mealPrice) {
		this.mealPrice = mealPrice;
	}

	public BigDecimal getBeveragePrice() {
		return beveragePrice;
	}

	public void setBeveragePrice(BigDecimal beveragePrice) {
		this.beveragePrice = beveragePrice;
	}

	public OpenTime getIdOpenTime() {
		return idOpenTime;
	}

	public void setIdOpenTime(OpenTime idOpenTime) {
		this.idOpenTime = idOpenTime;
	}

	public Meal getIdMeal() {
		return idMeal;
	}

	public void setIdMeal(Meal idMeal) {
		this.idMeal = idMeal;
	}

	public Promotion getIdPromotion() {
		return idPromotion;
	}

	public void setIdPromotion(Promotion idPromotion) {
		this.idPromotion = idPromotion;
	}

	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary idSubsidiary) {
		subsidiary = idSubsidiary;
	}

}
