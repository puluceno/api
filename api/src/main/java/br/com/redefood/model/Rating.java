/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.redefood.provider.CustomJsonDateSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Rating", schema = "RedeFood")
@NamedQueries({
    @NamedQuery(name = Rating.FIND_BY_SUBSIDIARY, query = "SELECT r FROM Rating r WHERE r.subsidiary.idSubsidiary = :idSubsidiary ORDER BY r.ratingDate DESC, r.idRating DESC"),
    @NamedQuery(name = Rating.FIND_BY_USER, query = "SELECT r FROM Rating r WHERE r.user.idUser = :idUser ORDER BY r.ratingDate DESC"),
    @NamedQuery(name = Rating.FIND_BY_ORDER, query = "SELECT r FROM Rating r WHERE r.order.idOrders = :idOrder ORDER BY r.ratingDate DESC"),
    @NamedQuery(name = Rating.COUNT_BY_SUBSIDIARY, query = "SELECT COUNT(r.idRating) FROM Rating r WHERE r.subsidiary.idSubsidiary = :idSubsidiary") })
public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_BY_SUBSIDIARY = "FIND_BY_SUBSIDIARY";
    public static final String FIND_BY_USER = "FIND_BY_USER";
    public static final String FIND_BY_ORDER = "FIND_BY_ORDER";
    public static final String COUNT_BY_SUBSIDIARY = "COUNT_BY_SUBSIDIARY";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idRating", nullable = false)
    private Integer idRating;
    @Column(name = "delivery")
    private Short delivery;
    @Column(name = "costBenefit")
    private Short costBenefit;
    @Column(name = "experience")
    private Short experience;
    @Column(name = "comment", length = 300)
    private String comment;
    @Basic(optional = false)
    @Column(name = "ratingDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ratingDate;
    @Column(name = "reply", length = 300)
    private String reply;
    @Column(name = "replyDate")
    @Temporal(TemporalType.DATE)
    private Date replyDate;
    @Column(name = "rejoinder", length = 300)
    private String rejoinder;
    @Column(name = "rejoinderDate")
    @Temporal(TemporalType.DATE)
    private Date rejoinderDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rating", fetch = FetchType.LAZY)
    private List<MealRating> mealRatings;
    @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Subsidiary subsidiary;
    @JoinColumn(name = "idOrders", referencedColumnName = "idOrders", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Orders order;
    @JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    
    public Rating() {
    }
    
    public Rating(Integer idRating) {
	this.idRating = idRating;
    }
    
    public Rating(Short delivery, Short costBenefit, String comment, Short experience, List<MealRating> mealRatings) {
	this.delivery = delivery;
	this.costBenefit = costBenefit;
	this.comment = comment;
	this.experience = experience;
	this.mealRatings = mealRatings;
    }
    
    @JsonProperty("id")
    public Integer getIdRating() {
	return idRating;
    }
    
    @JsonProperty("id")
    public void setIdRating(Integer idRating) {
	this.idRating = idRating;
    }
    
    public Short getDelivery() {
	return delivery;
    }
    
    public void setDelivery(Short delivery) {
	this.delivery = delivery;
    }
    
    public Short getCostBenefit() {
	return costBenefit;
    }
    
    public void setCostBenefit(Short costBenefit) {
	this.costBenefit = costBenefit;
    }
    
    public Short getExperience() {
	return experience;
    }
    
    public void setExperience(Short experience) {
	this.experience = experience;
    }
    
    public String getComment() {
	return comment;
    }
    
    public void setComment(String comment) {
	this.comment = comment;
    }
    
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getRatingDate() {
	return ratingDate;
    }
    
    public void setRatingDate(Date ratingDate) {
	this.ratingDate = ratingDate;
    }
    
    public String getReply() {
	return reply;
    }
    
    public void setReply(String reply) {
	this.reply = reply;
    }
    
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getReplyDate() {
	return replyDate;
    }
    
    public void setReplyDate(Date replyDate) {
	this.replyDate = replyDate;
    }
    
    public String getRejoinder() {
	return rejoinder;
    }
    
    public void setRejoinder(String rejoinder) {
	this.rejoinder = rejoinder;
    }
    
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getRejoinderDate() {
	return rejoinderDate;
    }
    
    public void setRejoinderDate(Date rejoinderDate) {
	this.rejoinderDate = rejoinderDate;
    }
    
    public List<MealRating> getMealRatings() {
	return mealRatings;
    }
    
    public void setMealRatings(List<MealRating> mealRatings) {
	this.mealRatings = mealRatings;
    }
    
    public Subsidiary getSubsidiary() {
	return subsidiary;
    }
    
    public void setSubsidiary(Subsidiary subsidiary) {
	this.subsidiary = subsidiary;
    }
    
    public Orders getOrder() {
	return order;
    }
    
    public void setOrder(Orders order) {
	this.order = order;
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
	result = prime * result + (comment == null ? 0 : comment.hashCode());
	result = prime * result + (costBenefit == null ? 0 : costBenefit.hashCode());
	result = prime * result + (delivery == null ? 0 : delivery.hashCode());
	result = prime * result + (mealRatings == null ? 0 : mealRatings.hashCode());
	result = prime * result + (order == null ? 0 : order.hashCode());
	result = prime * result + (ratingDate == null ? 0 : ratingDate.hashCode());
	result = prime * result + (subsidiary == null ? 0 : subsidiary.hashCode());
	result = prime * result + (user == null ? 0 : user.hashCode());
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
	Rating other = (Rating) obj;
	if (comment == null) {
	    if (other.comment != null)
		return false;
	} else if (!comment.equals(other.comment))
	    return false;
	if (costBenefit == null) {
	    if (other.costBenefit != null)
		return false;
	} else if (!costBenefit.equals(other.costBenefit))
	    return false;
	if (delivery == null) {
	    if (other.delivery != null)
		return false;
	} else if (!delivery.equals(other.delivery))
	    return false;
	if (mealRatings == null) {
	    if (other.mealRatings != null)
		return false;
	} else if (!mealRatings.equals(other.mealRatings))
	    return false;
	if (order == null) {
	    if (other.order != null)
		return false;
	} else if (!order.equals(other.order))
	    return false;
	if (ratingDate == null) {
	    if (other.ratingDate != null)
		return false;
	} else if (!ratingDate.equals(other.ratingDate))
	    return false;
	if (subsidiary == null) {
	    if (other.subsidiary != null)
		return false;
	} else if (!subsidiary.equals(other.subsidiary))
	    return false;
	if (user == null) {
	    if (other.user != null)
		return false;
	} else if (!user.equals(other.user))
	    return false;
	return true;
    }
    
}
