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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Restaurant", schema = "RedeFood", uniqueConstraints = { @UniqueConstraint(columnNames = { "subdomain" }) })
@NamedQueries({
		@NamedQuery(name = Restaurant.FIND_ALL, query = "SELECT r FROM Restaurant r"),
		@NamedQuery(name = Restaurant.FIND_ALL_PRODUCTS, query = "SELECT DISTINCT r FROM Restaurant r INNER JOIN r.subsidiaries sub INNER JOIN sub.meals m INNER JOIN sub.beverages b WHERE r.idRestaurant = :idRestaurant"),
		@NamedQuery(name = Restaurant.FIND_BY_NAME, query = "SELECT r FROM Restaurant r WHERE r.name = :name"),
		@NamedQuery(name = Restaurant.FIND_BY_SUBDOMAIN, query = "SELECT r FROM Restaurant r WHERE r.subdomain = :subdomain"),
		@NamedQuery(name = Restaurant.FIND_PENDING_RESTAURANTS, query = "SELECT r FROM Restaurant r INNER JOIN FETCH r.subsidiaries s WHERE s.active = false"),
		@NamedQuery(name = Restaurant.FIND_BY_EMPLOYEE, query = "SELECT DISTINCT r FROM Restaurant r INNER JOIN FETCH r.subsidiaries s INNER JOIN s.employeeList e WHERE e.idEmployee = :idEmployee"),
		@NamedQuery(name = Restaurant.FIND_ATTEND_NEIGHBORHOOD, query = "SELECT DISTINCT r FROM Restaurant r INNER JOIN FETCH r.subsidiaries s INNER JOIN s.deliveryAreas d WHERE d.neighborhood.id = :idNeighborhood"),
		@NamedQuery(name = Restaurant.FIND_ALL_RESTAURANT_SUBDOMAIN, query = "SELECT r.subdomain FROM Restaurant r") })
public class Restaurant implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "FIND_ALL";
	public static final String FIND_ALL_PRODUCTS = "FIND_ALL_PRODUCTS";
	public static final String FIND_BY_NAME = "FIND_BY_NAME";
	public static final String FIND_BY_SUBDOMAIN = "FIND_BY_SUBDOMAIN";
	public static final String FIND_PENDING_RESTAURANTS = "FIND_PENDING_RESTAURANTS";
	public static final String FIND_BY_EMPLOYEE = "FIND_BY_EMPLOYEE";
	public static final String FIND_ATTEND_NEIGHBORHOOD = "FIND_ATTEND_NEIGHBORHOOD";
	public static final String FIND_ALL_RESTAURANT_SUBDOMAIN = "FIND_ALL_RESTAURANT_SUBDOMAIN";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idRestaurant", nullable = false)
	private Short idRestaurant;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 60)
	private String name;
	@Column(name = "logo")
	private String logo = "default/store256.png";
	@Column(name = "subdomain", length = 30)
	private String subdomain;
	@Column(name = "template", length = 300)
	private String template;
	@Column(name = "theme", length = 300)
	private String theme;
	@Column(name = "description", length = 3000)
	private String description;
	@Column(name = "insertDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date insertDate;
	@Column(name = "answerDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date answerDate;
	@JoinTable(name = "Restaurant_has_RestaurantType", joinColumns = { @JoinColumn(name = "idRestaurant", referencedColumnName = "idRestaurant", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idRestaurantType", referencedColumnName = "idRestaurantType", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<RestaurantType> restaurantTypes;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idRestaurant", fetch = FetchType.LAZY)
	private List<Subsidiary> subsidiaries;
	@Column(name = "slogan", length = 100)
	private String slogan;

	public Restaurant() {
	}

	public Restaurant(Short idRestaurant) {
		this.idRestaurant = idRestaurant;
	}

	public Restaurant(Short idRestaurant, String name) {
		this.idRestaurant = idRestaurant;
		this.name = name;
	}

	public Restaurant(String name) {
		this.name = name;
	}

	@JsonProperty("id")
	public Short getIdRestaurant() {
		return idRestaurant;
	}

	@JsonProperty("id")
	public void setIdRestaurant(Short idRestaurant) {
		this.idRestaurant = idRestaurant;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String domain) {
		subdomain = domain;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public Date getAnswerDate() {
		return answerDate;
	}

	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}

	@JsonProperty("restaurantTypes")
	public List<RestaurantType> getRestaurantTypes() {
		return restaurantTypes;
	}

	@JsonProperty("restaurantTypes")
	public void setRestaurantTypes(List<RestaurantType> restaurantTypeList) {
		restaurantTypes = restaurantTypeList;
	}

	@JsonProperty("subsidiaries")
	public List<Subsidiary> getSubsidiaries() {
		return subsidiaries;
	}

	@JsonProperty("subsidiaries")
	public void setSubsidiaries(List<Subsidiary> subsidiaries) {
		this.subsidiaries = subsidiaries;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idRestaurant == null ? 0 : idRestaurant.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (restaurantTypes == null ? 0 : restaurantTypes.hashCode());
		result = prime * result + (subsidiaries == null ? 0 : subsidiaries.hashCode());
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
		Restaurant other = (Restaurant) obj;
		if (idRestaurant == null) {
			if (other.idRestaurant != null)
				return false;
		} else if (!idRestaurant.equals(other.idRestaurant))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (restaurantTypes == null) {
			if (other.restaurantTypes != null)
				return false;
		} else if (!restaurantTypes.equals(other.restaurantTypes))
			return false;
		if (subsidiaries == null) {
			if (other.subsidiaries != null)
				return false;
		} else if (!subsidiaries.equals(other.subsidiaries))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Restaurant [idRestaurant=" + idRestaurant + ", name=" + name + "]";
	}

}
