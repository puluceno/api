/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import br.com.redefood.model.enumtype.DiscountType;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "DiscountCoupon", schema = "RedeFood", uniqueConstraints = { @UniqueConstraint(columnNames = { "couponCode" }) })
public class DiscountCoupon implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false)
	private Integer idDiscountCoupon;
	@Basic(optional = false)
	@Column(nullable = false, length = 60)
	private String name;
	@Basic(optional = false)
	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal value;
	@Basic(optional = false)
	@Column(nullable = false, length = 300)
	private String description;
	@Enumerated(EnumType.STRING)
	@NotNull
	private DiscountType discountType;
	@Basic(optional = false)
	@Column(nullable = false, length = 30)
	private String couponCode;
	/**
	 * Date when coupon starts to be valid
	 */
	@Basic(optional = false)
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fromDate;
	/**
	 * Final Date time when coupon expires
	 */
	@Basic(optional = false)
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date toDate;
	/**
	 * Zero means unlimited uses
	 */
	private Short maximumUses;
	/**
	 * Coupon valid only to user's first order at given subsidiary
	 */
	@Basic(optional = false)
	@Column(nullable = false)
	private Boolean onlyFirstOrder = Boolean.FALSE;
	private Boolean used = Boolean.FALSE;
	private Boolean expired = Boolean.FALSE;
	@JoinTable(name = "User_has_DiscountCoupon", joinColumns = { @JoinColumn(name = "idDiscountCoupon", referencedColumnName = "idDiscountCoupon", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<User> userList;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;

	public DiscountCoupon() {
	}

	public DiscountCoupon(Integer idDiscountCoupon) {
		this.idDiscountCoupon = idDiscountCoupon;
	}

	public Integer getIdDiscountCoupon() {
		return idDiscountCoupon;
	}

	public void setIdDiscountCoupon(Integer idDiscountCoupon) {
		this.idDiscountCoupon = idDiscountCoupon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DiscountType getDiscountType() {
		return discountType;
	}

	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setEndDate(Date toDate) {
		this.toDate = toDate;
	}

	public Short getMaximumUses() {
		return maximumUses;
	}

	public void setMaximumUses(Short maximumUses) {
		this.maximumUses = maximumUses;
	}

	public boolean isOnlyFirstOrder() {
		return onlyFirstOrder;
	}

	public void setOnlyFirstOrder(boolean onlyFirstOrder) {
		this.onlyFirstOrder = onlyFirstOrder;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

}
