package br.com.redefood.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Subsidiary", schema = "RedeFood", uniqueConstraints = { @UniqueConstraint(columnNames = { "cnpj" }) })
@NamedQueries({
	@NamedQuery(name = Subsidiary.FIND_ALL_SUBSIDIARIES, query = "SELECT DISTINCT s FROM Subsidiary s INNER JOIN FETCH s.idRestaurant r LEFT JOIN FETCH r.restaurantTypes rt INNER JOIN FETCH s.idAddress a INNER JOIN FETCH a.idCity c INNER JOIN FETCH a.neighborhood n "),
	@NamedQuery(name = Subsidiary.FIND_ACTIVE_SUBSIDIARY, query = "SELECT DISTINCT s FROM Subsidiary s WHERE s.active = true"),
	@NamedQuery(name = Subsidiary.FIND_SUBSIDIARY_BY_ID, query = "SELECT DISTINCT s FROM Subsidiary s INNER JOIN FETCH s.idRestaurant r LEFT JOIN FETCH r.restaurantTypes rt INNER JOIN FETCH s.idAddress a INNER JOIN FETCH a.idCity c INNER JOIN FETCH a.neighborhood n WHERE s.idRestaurant.idRestaurant = :idRestaurant"),
	@NamedQuery(name = Subsidiary.FIND_SUBSIDIARY_BY_EMPLOYEE_ID, query = "SELECT s FROM Subsidiary s INNER JOIN FETCH s.employeeList e WHERE e.idEmployee = :idEmployee"),
	@NamedQuery(name = Subsidiary.FIND_SUBSIDIARY_BY_ID_AND_RESTAURANT_ID, query = "SELECT s FROM Subsidiary s WHERE s.idRestaurant.idRestaurant = :idRestaurant AND s.idSubsidiary=:idSubsidiary"),
	@NamedQuery(name = Subsidiary.FIND_PENDING_BY_RESTAURANT, query = "SELECT s FROM Subsidiary s WHERE s.active = false AND s.idRestaurant.idRestaurant = :idRestaurant"),
	@NamedQuery(name = Subsidiary.FIND_TOTAL_MONEY_BY_DATE, query = "SELECT sum(o.totalPrice) as totalOrderValue, sum(o.deliveryPrice) as totalDeliveryValue, (sum(o.totalPrice)-sum(o.deliveryPrice)) as totalRenevue FROM Orders o WHERE o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' AND o.orderType <> 8"),
	@NamedQuery(name = Subsidiary.FIND_TOTAL_MONEY_BY_ORDERTYPE, query = "SELECT sum(o.totalPrice) as totalOrderValue, o.orderType.name FROM Orders o WHERE o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' GROUP BY o.orderType"),
	@NamedQuery(name = Subsidiary.FIND_MAX_ORDER_PRICE, query = "SELECT MAX(o.totalPrice) FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"),
	@NamedQuery(name = Subsidiary.FIND_MIN_ORDER_PRICE, query = "SELECT MIN(o.totalPrice) FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"),
	@NamedQuery(name = Subsidiary.FIND_AVG_PREPARETIME, query = "SELECT AVG(TIME_TO_SEC(TIMEDIFF(o.orderSent, o.orderMade))) FROM Orders o WHERE o.orderSent IS NOT NULL AND o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED'"),
	@NamedQuery(name = Subsidiary.FIND_MAX_PREPARETIME, query = "SELECT MAX(TIME_TO_SEC(TIMEDIFF(o.orderSent, o.orderMade))) FROM Orders o WHERE o.orderSent IS NOT NULL AND o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED'"),
	@NamedQuery(name = Subsidiary.FIND_MIN_PREPARETIME, query = "SELECT MIN(TIME_TO_SEC(TIMEDIFF(o.orderSent, o.orderMade))) FROM Orders o WHERE o.orderSent IS NOT NULL AND o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED'"),
	@NamedQuery(name = Subsidiary.FIND_SUBSIDIARY_EMAIL_BY_EMPLOYEE_ID, query = "SELECT s.email FROM Subsidiary s JOIN s.employeeList e WHERE e.idEmployee = :idEmployee"),
	@NamedQuery(name = Subsidiary.FIND_SUBSIDIARY_SUBDOMAIN, query = "SELECT s.idRestaurant.subdomain FROM Subsidiary s WHERE s.idSubsidiary = :idSubsidiary"),
	@NamedQuery(name = Subsidiary.FIND_OWNER_LOGIN_BY_ID_SUBSIDIARY, query = "SELECT new br.com.redefood.model.complex.UserLogin(e.cpf, e.password, 'localhost') FROM Subsidiary s JOIN s.employeeList e WHERE s.idSubsidiary = :idSubsidiary AND e.profile.idProfile = :idProfile"),
	@NamedQuery(name = Subsidiary.FIND_AVG_RATINGS_BY_SUBSIDIARY, query = "SELECT ROUND(AVG(r.delivery), 1), ROUND(AVG(r.costBenefit), 1), ROUND(AVG(r.experience), 1),ROUND(AVG(mr.mealRating), 1), ROUND((AVG(r.delivery) + AVG(r.costBenefit) + AVG(r.experience) + AVG(mr.mealRating)) / 4, 1) FROM Rating r JOIN r.mealRatings mr WHERE r.subsidiary.idSubsidiary = :idSubsidiary") })
public class Subsidiary implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_SUBSIDIARIES = "FIND_ALL_SUBSIDIARIES";
	public static final String FIND_ACTIVE_SUBSIDIARY = "FIND_ACTIVE_SUBSIDIARY";
	public static final String FIND_SUBSIDIARY_BY_ID = "FIND_SUBSIDIARY_BY_ID";
	public static final String FIND_SUBSIDIARY_BY_EMPLOYEE_ID = "FIND_SUBSIDIARY_BY_EMPLOYEE_ID";
	public static final String FIND_SUBSIDIARY_BY_ID_AND_RESTAURANT_ID = "FIND_SUBSIDIARY_BY_ID_AND_RESTAURANT_ID";
	public static final String FIND_PENDING_BY_RESTAURANT = "FIND_PENDING_BY_RESTAURANT";
	public static final String FIND_TOTAL_MONEY_BY_DATE = "FIND_TOTAL_MONEY_BY_DATE";
	public static final String FIND_TOTAL_MONEY_BY_ORDERTYPE = "FIND_TOTAL_MONEY_BY_ORDERTYPE";
	public static final String FIND_MAX_ORDER_PRICE = "FIND_MAX_ORDER_PRICE";
	public static final String FIND_MIN_ORDER_PRICE = "FIND_MIN_ORDER_PRICE";
	public static final String FIND_AVG_PREPARETIME = "FIND_AVG_PREPARETIME";
	public static final String FIND_MAX_PREPARETIME = "FIND_MAX_PREPARETIME";
	public static final String FIND_MIN_PREPARETIME = "FIND_MIN_PREPARETIME";
	public static final String FIND_SUBSIDIARY_EMAIL_BY_EMPLOYEE_ID = "FIND_SUBSIDIARY_EMAIL_BY_EMPLOYEE_ID";
	public static final String FIND_SUBSIDIARY_SUBDOMAIN = "FIND_SUBSIDIARY_SUBDOMAIN";
	public static final String FIND_OWNER_LOGIN_BY_ID_SUBSIDIARY = "FIND_OWNER_LOGIN_BY_ID_SUBSIDIARY";
	public static final String FIND_AVG_RATINGS_BY_SUBSIDIARY = "FIND_AVG_RATINGS_BY_SUBSIDIARY";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idSubsidiary", nullable = false)
	private Short idSubsidiary;
	@Column(name = "name", length = 60)
	private String name;
	@Basic(optional = false)
	@Column(name = "cnpj", nullable = false, length = 19)
	private String cnpj;
	@Basic(optional = false)
	@Column(name = "headOffice", nullable = false)
	private boolean headOffice;
	@Column(name = "description", length = 3000)
	private String description;
	@Lob
	@Column(name = "image")
	private String image;
	@Max(value = 9999)
	@Min(value = 0)
	@Column(name = "minOrder", precision = 6, scale = 2)
	private Double minOrder = 0.0;
	@Lob
	@Column(name = "imageBanner")
	private String imageBanner;
	@Lob
	@Column(name = "imageLogo")
	private String imageLogo;
	@Column(name = "phone1", length = 20)
	private String phone1;
	@Column(name = "phone2", length = 20)
	private String phone2;
	@Column(name = "site", length = 100)
	private String site;
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid e-mail")
	@Column(name = "email", length = 100)
	private String email;
	@Column(name = "foursquare", length = 100)
	private String foursquare;
	@Column(name = "facebook", length = 100)
	private String facebook;
	@Column(name = "twitter", length = 100)
	private String twitter;
	@Column(name = "active")
	private Boolean active;
	@Column(name = "denied")
	private Boolean denied;
	@Column(name = "squareActive")
	private Boolean squareActive;
	@Column(name = "insertDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date insertDate;
	@Column(name = "trackOrder")
	private boolean trackOrder;
	@Column(name = "avgDeliveryTime")
	private Short avgDeliveryTime;
	@Column(name = "filledWizard")
	private boolean filledWizard = false;
	@Column(name = "paused")
	private Boolean paused = false;
	@Column(name = "ga", length = 30)
	private String ga;
	@Column(name = "metaDescription", length = 1024)
	private String metaDescription;
	@Column(name = "metaKeyWords", length = 1024)
	private String metaKeyWords;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Rating> ratingList;
	@JoinColumn(name = "idRestaurant", referencedColumnName = "idRestaurant", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Restaurant idRestaurant;
	@JoinColumn(name = "idAddress", referencedColumnName = "idAddress", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Address idAddress;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Account> accountList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Orders> ordersList;
	@JoinTable(name = "Subsidiary_has_Employee", joinColumns = { @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idEmployee", referencedColumnName = "idEmployee", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<Employee> employeeList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<SubsidiaryhasPromotion> subsidiaryhasPromotionList;
	@JoinTable(name = "Subsidiary_has_PaymentMethod", joinColumns = { @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idPaymentMethod", referencedColumnName = "idPaymentMethod", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<PaymentMethod> paymentMethodList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<OpenTime> openTime;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idSubsidiary", fetch = FetchType.LAZY)
	private List<DeliveryArea> deliveryAreas;
	@JoinTable(name = "Subsidiary_has_OrderType", joinColumns = { @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idOrderType", referencedColumnName = "idOrderType", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<OrderType> orderTypes;
	@JoinColumn(name = "idConfiguration", referencedColumnName = "idConfiguration")
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Configuration configuration;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<SubsidiaryModule> subsidiaryModules;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Meal> meals;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<SubsidiaryMessages> messages;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Ingredient> ingredients;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<MealType> mealTypes;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<IngredientType> ingredientTypes;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<BeverageType> beverageTypes;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Beverage> beverages;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<Board> boards;
	@JoinTable(name = "Subsidiary_has_City", joinColumns = { @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idCity", referencedColumnName = "idCity", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<City> citiesAttended = new ArrayList<City>();
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "subsidiary", fetch = FetchType.LAZY)
	private List<DiscountCoupon> discountCoupons;

	@Transient
	private Boolean subsidiaryOpen;
	@Transient
	private HashMap<String, Object> avgRating;

	public Subsidiary() {
	}

	public Subsidiary(Short idSubsidiary) {
		this.idSubsidiary = idSubsidiary;
	}

	public Subsidiary(Short idSubsidiary, String name, String cnpj, boolean headOffice) {
		this.idSubsidiary = idSubsidiary;
		this.name = name;
		this.cnpj = cnpj;
		this.headOffice = headOffice;
	}

	public Subsidiary(String name, String cnpj, boolean headOffice, String description, Restaurant idRestaurant,
			Address idAddress) {
		this.name = name;
		this.cnpj = cnpj;
		this.headOffice = headOffice;
		this.description = description;
		this.idRestaurant = idRestaurant;
		this.idAddress = idAddress;
	}

	@JsonProperty("id")
	public Short getId() {
		return idSubsidiary;
	}

	@JsonProperty("id")
	public void setId(Short idSubsidiary) {
		this.idSubsidiary = idSubsidiary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public boolean getHeadOffice() {
		return headOffice;
	}

	public void setHeadOffice(boolean headOffice) {
		this.headOffice = headOffice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getMinOrder() {
		return minOrder;
	}

	public void setMinOrder(Double minOrder) {
		this.minOrder = minOrder;
	}

	public boolean isTrackOrder() {
		return trackOrder;
	}

	public void setTrackOrder(boolean trackOrder) {
		this.trackOrder = trackOrder;
	}

	public String getImageBanner() {
		return imageBanner;
	}

	public void setImageBanner(String imageBanner) {
		this.imageBanner = imageBanner;
	}

	public String getImageLogo() {
		return imageLogo;
	}

	public void setImageLogo(String imageLogo) {
		this.imageLogo = imageLogo;
	}

	@JsonIgnore
	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	@JsonIgnore
	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	@JsonIgnore
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	@JsonIgnore
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	public String getFoursquare() {
		return foursquare;
	}

	public void setFoursquare(String foursquare) {
		this.foursquare = foursquare;
	}

	@JsonIgnore
	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	@JsonIgnore
	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getDenied() {
		return denied;
	}

	public void setDenied(Boolean denied) {
		this.denied = denied;
	}

	public Boolean getSquareActive() {
		return squareActive;
	}

	public void setSquareActive(Boolean squareActive) {
		this.squareActive = squareActive;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public Short getAvgDeliveryTime() {
		return avgDeliveryTime;
	}

	public void setAvgDeliveryTime(Short avgDeliveryTime) {
		this.avgDeliveryTime = avgDeliveryTime;
	}

	public boolean isFilledWizard() {
		return filledWizard;
	}

	public void setFilledWizard(boolean filledWizard) {
		this.filledWizard = filledWizard;
	}

	public Boolean getPaused() {
		return paused;
	}

	public void setPaused(Boolean paused) {
		this.paused = paused;
	}

	public String getGa() {
		return ga;
	}

	public void setGa(String ga) {
		this.ga = ga;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaKeyWords() {
		return metaKeyWords;
	}

	public void setMetaKeyWords(String metaKeyWords) {
		this.metaKeyWords = metaKeyWords;
	}

	@JsonIgnore
	public List<Rating> getRatingList() {
		return ratingList;
	}

	public void setRatingList(List<Rating> ratingList) {
		this.ratingList = ratingList;
	}

	@JsonProperty("restaurant")
	@JsonIgnore
	public Restaurant getRestaurant() {
		return idRestaurant;
	}

	@JsonProperty("restaurant")
	@JsonIgnore
	public void setRestaurant(Restaurant idRestaurant) {
		this.idRestaurant = idRestaurant;
	}

	@JsonProperty("address")
	public Address getAddress() {
		return idAddress;
	}

	@JsonProperty("address")
	public void setAddress(Address idAddress) {
		this.idAddress = idAddress;
	}

	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	@JsonIgnore
	public List<Orders> getOrdersList() {
		return ordersList;
	}

	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
	}

	public List<Employee> getEmployees() {
		return employeeList;
	}

	public void setEmployees(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public List<SubsidiaryhasPromotion> getSubsidiaryhasPromotionList() {
		return subsidiaryhasPromotionList;
	}

	public void setSubsidiaryhasPromotionList(List<SubsidiaryhasPromotion> subsidiaryhasPromotionList) {
		this.subsidiaryhasPromotionList = subsidiaryhasPromotionList;
	}

	@JsonProperty("paymentMethod")
	public List<PaymentMethod> getPaymentMethod() {
		return paymentMethodList;
	}

	@JsonProperty("paymentMethod")
	public void setPaymentMethod(List<PaymentMethod> paymentMethodList) {
		this.paymentMethodList = paymentMethodList;
	}

	@JsonProperty("openTime")
	public List<OpenTime> getOpenTime() {
		return openTime;
	}

	@JsonProperty("openTime")
	public void setOpenTime(List<OpenTime> openTimeList) {
		openTime = openTimeList;
	}

	@JsonProperty("contact")
	public Map<String, String> getContact() {
		Map<String, String> contact = new HashMap<String, String>();
		contact.put("email", getEmail());
		contact.put("facebook", getFacebook());
		contact.put("foursquare", getFoursquare());
		contact.put("phone1", getPhone1());
		contact.put("phone2", getPhone2());
		contact.put("site", getSite());
		contact.put("twitter", getTwitter());
		return contact;
	}

	@JsonProperty("contact")
	public void setContact(Map<String, String> contact) {
		setEmail(contact.get("email"));
		setFacebook(contact.get("facebook"));
		setFoursquare(contact.get("foursquare"));
		setPhone1(contact.get("phone1"));
		setPhone2(contact.get("phone2"));
		setSite(contact.get("site"));
		setTwitter(contact.get("twitter"));
	}

	public List<DeliveryArea> getDeliveryAreas() {
		return deliveryAreas;
	}

	public void setDeliveryAreas(List<DeliveryArea> deliveryAreas) {
		this.deliveryAreas = deliveryAreas;
	}

	public List<OrderType> getOrderTypes() {
		return orderTypes;
	}

	public void setOrderTypes(List<OrderType> orderTypes) {
		this.orderTypes = orderTypes;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public List<SubsidiaryModule> getSubsidiaryModules() {
		return subsidiaryModules;
	}

	public void setSubsidiaryModules(List<SubsidiaryModule> subsidiaryModules) {
		this.subsidiaryModules = subsidiaryModules;
	}

	public List<Meal> getMeals() {
		return meals;
	}

	public void setMeals(List<Meal> meals) {
		this.meals = meals;
	}

	public List<SubsidiaryMessages> getMessages() {
		return messages;
	}

	public void setMessages(List<SubsidiaryMessages> messages) {
		this.messages = messages;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public List<MealType> getMealTypes() {
		return mealTypes;
	}

	public void setSubsidiaryMealTypeList(List<MealType> mealTypes) {
		this.mealTypes = mealTypes;
	}

	public List<IngredientType> getIngredientTypes() {
		return ingredientTypes;
	}

	public void setIngredientTypes(List<IngredientType> ingredientTypes) {
		this.ingredientTypes = ingredientTypes;
	}

	public List<BeverageType> getBeverageTypes() {
		return beverageTypes;
	}

	public void setBeverageTypes(List<BeverageType> beverageTypes) {
		this.beverageTypes = beverageTypes;
	}

	public List<Beverage> getBeverages() {
		return beverages;
	}

	public void setBeverages(List<Beverage> beverages) {
		this.beverages = beverages;
	}

	public List<Board> getBoards() {
		return boards;
	}

	public void setBoards(List<Board> boards) {
		this.boards = boards;
	}

	public List<City> getCitiesAttended() {
		return citiesAttended;
	}

	public void setCitiesAttended(List<City> citiesAttended) {
		this.citiesAttended = citiesAttended;
	}

	public Boolean getSubsidiaryOpen() {
		return subsidiaryOpen;
	}

	public void setSubsidiaryOpen(Boolean subsidiaryOpen) {
		this.subsidiaryOpen = subsidiaryOpen;
	}

	public List<DiscountCoupon> getDiscountCoupons() {
		return discountCoupons;
	}

	public void setDiscountCoupons(List<DiscountCoupon> discountCoupons) {
		this.discountCoupons = discountCoupons;
	}

	public HashMap<String, Object> getAvgRating() {
		return avgRating;
	}

	public void setAvgRating(HashMap<String, Object> avgRating) {
		this.avgRating = avgRating;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accountList == null ? 0 : accountList.hashCode());
		result = prime * result + (cnpj == null ? 0 : cnpj.hashCode());
		result = prime * result + (description == null ? 0 : description.hashCode());
		result = prime * result + (employeeList == null ? 0 : employeeList.hashCode());
		result = prime * result + (headOffice ? 1231 : 1237);
		result = prime * result + (idAddress == null ? 0 : idAddress.hashCode());
		result = prime * result + (idRestaurant == null ? 0 : idRestaurant.hashCode());
		result = prime * result + (idSubsidiary == null ? 0 : idSubsidiary.hashCode());
		result = prime * result + (minOrder == null ? 0 : minOrder.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (ordersList == null ? 0 : ordersList.hashCode());
		result = prime * result + (ratingList == null ? 0 : ratingList.hashCode());
		result = prime * result + (subsidiaryhasPromotionList == null ? 0 : subsidiaryhasPromotionList.hashCode());
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
		Subsidiary other = (Subsidiary) obj;
		if (accountList == null) {
			if (other.accountList != null)
				return false;
		} else if (!accountList.equals(other.accountList))
			return false;
		if (cnpj == null) {
			if (other.cnpj != null)
				return false;
		} else if (!cnpj.equals(other.cnpj))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (employeeList == null) {
			if (other.employeeList != null)
				return false;
		} else if (!employeeList.equals(other.employeeList))
			return false;
		if (headOffice != other.headOffice)
			return false;
		if (idAddress == null) {
			if (other.idAddress != null)
				return false;
		} else if (!idAddress.equals(other.idAddress))
			return false;
		if (idRestaurant == null) {
			if (other.idRestaurant != null)
				return false;
		} else if (!idRestaurant.equals(other.idRestaurant))
			return false;
		if (idSubsidiary == null) {
			if (other.idSubsidiary != null)
				return false;
		} else if (!idSubsidiary.equals(other.idSubsidiary))
			return false;
		if (minOrder == null) {
			if (other.minOrder != null)
				return false;
		} else if (!minOrder.equals(other.minOrder))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ordersList == null) {
			if (other.ordersList != null)
				return false;
		} else if (!ordersList.equals(other.ordersList))
			return false;
		if (ratingList == null) {
			if (other.ratingList != null)
				return false;
		} else if (!ratingList.equals(other.ratingList))
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
		return "Subsidiary [idSubsidiary=" + idSubsidiary + ", name=" + name + ", cnpj=" + cnpj + ", headOffice="
				+ headOffice + ", description=" + description + ",ratingList=" + ratingList + ", idRestaurant="
				+ idRestaurant + ", idAddress=" + idAddress + ", accountList=" + accountList + ", ordersList="
				+ ordersList + ", employeeList=" + employeeList + ", subsidiaryhasPromotionList="
				+ subsidiaryhasPromotionList + "]";
	}
}
