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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import br.com.redefood.model.enumtype.OrderOrigin;
import br.com.redefood.model.enumtype.OrderStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Orders", schema = "RedeFood")
@NamedQueries({
    @NamedQuery(name = "Orders.findAllByUser", query = "SELECT o FROM Orders o WHERE o.user.idUser = :idUser ORDER BY o.orderMade DESC"),
    @NamedQuery(name = Orders.FIND_LAST_LOCAL_ORDER_NUMBER, query = "SELECT COALESCE(o.localOrderNumber,0) FROM Orders o WHERE o.idOrders =(SELECT MAX(ord.idOrders) FROM Orders ord WHERE ord.subsidiary.idSubsidiary = :idSubsidiary)"),
    @NamedQuery(name = "Orders.findBySubsidiaryAndUser", query = "SELECT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.user.idUser = :idUser"),
    @NamedQuery(name = "Orders.findByIdOrderAndUser", query = "SELECT o FROM Orders o WHERE o.idOrders = :idOrders AND o.user.idUser = :idUser"),
    @NamedQuery(name = "Orders.findByIdSubsidiaryAndStatus", query = "SELECT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus LIKE :orderStatus"),
    @NamedQuery(name = "Orders.findByIdSubsidiary", query = "SELECT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary"),
    @NamedQuery(name = "Orders.findQtyBetweenDates", query = "SELECT count(o) FROM Orders o WHERE o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = "Orders.findAvgOrderPrice", query = "SELECT AVG(o.totalPrice) FROM Orders o WHERE o.orderMade BETWEEN :from AND :to AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' AND o.orderType <> 8"),
    @NamedQuery(name = "Orders.findByOrderStatusAndTime", query = "SELECT DISTINCT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus = :orderStatus AND o.orderMade >= :fetchTime ORDER BY o.orderMade DESC"),
    @NamedQuery(name = "Orders.findOrdersBySubsidiaryAndType", query = "SELECT DISTINCT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade >= :orderTime AND o.orderType.idOrderType =:idOrderType ORDER BY o.orderMade DESC"),
    @NamedQuery(name = "Orders.findOrdersBySubsidiaryAndTime", query = "SELECT DISTINCT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade >= :orderTime ORDER BY o.orderMade DESC"),
    @NamedQuery(name = "Orders.findAvailableToRateByUser", query = "SELECT o FROM Orders o LEFT JOIN o.ratings r WHERE r.idRating IS NULL AND o.user.idUser = :idUser AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = Orders.FIND_TOTAL_ORDER_NUMBER_BY_SUBSIDIARY, query = "SELECT MAX(COALESCE(o.totalOrderNumber,0)) FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = "Orders.findAvailableToRateByOrder", query = "SELECT o FROM Orders o LEFT JOIN o.ratings r WHERE r.idRating IS NULL AND o.idOrders = :idOrders AND o.user.idUser = :idUser AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = "Orders.findCanceledBySubsidiaryAndDate", query = "SELECT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus = 'CANCELED'"),
    @NamedQuery(name = "Orders.findByOrderAndBoard", query = "SELECT o FROM Orders o WHERE o.idOrders = :idOrders AND o.subsidiary.idSubsidiary = :idSubsidiary AND o.board.idBoard = :idBoard AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = "Orders.findOpenOrderByBoard", query = "SELECT o FROM Orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.board.idBoard = :idBoard AND o.boardPayed=false"),
    @NamedQuery(name = Orders.COUNT_ORDERS_IN_NEIGHBORHOOD_AND_SUBSIDIARY_AND_PERIOD, query = "SELECT count(o.idOrders) as qty, n.name, n.lat as lat, n.lng as lng"
	    + " FROM Orders o JOIN o.address a"
	    + " JOIN a.neighborhood n "
	    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary "
	    + " AND o.orderMade BETWEEN :from AND :to "
	    + " AND o.orderStatus <> 'CANCELED' " + " GROUP BY n.name ORDER BY qty DESC"),
	    @NamedQuery(name = Orders.COUNT_ORDERS_BY_SUBSIDIARY, query = "SELECT COUNT(o) FROM Orders o "
		    + "WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade >= :orderTime "),
		    @NamedQuery(name = Orders.MONEY_PAYMENT_METHOD_BY_SUBSIDIARY_AND_PERIOD, query = "SELECT sum(o.totalPrice) as totalPrice, pm.name as name FROM Orders o"
			    + " JOIN o.orderPaymentMethod opm "
			    + " JOIN opm.paymentMethod pm "
			    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"
			    + " GROUP BY pm.name"),
			    @NamedQuery(name = Orders.PAYMENT_METHOD_USED_BY_SUBSIDIARY_AND_PERIOD, query = "SELECT count(pm.idPaymentMethod) as qty, pm.name FROM Orders o "
				    + " JOIN o.orderPaymentMethod opm"
				    + " JOIN opm.paymentMethod pm"
				    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary"
				    + " AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"
				    + " GROUP BY pm.idPaymentMethod ORDER BY qty DESC, pm.name ASC"),
				    @NamedQuery(name = Orders.BEVERAGE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD, query = "SELECT count(beverage.name) as qty, beverage.name FROM Orders o"
					    + " JOIN o.beverages beverage"
					    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"
					    + " GROUP BY beverage.name ORDER BY qty DESC, beverage.name ASC"),
					    @NamedQuery(name = Orders.BEVERAGE_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD, query = "SELECT COUNT(bt.name) as qty, bt.name FROM Orders o"
						    + " JOIN o.beverages beverage"
						    + " JOIN beverage.beverageType bt"
						    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"
						    + " GROUP BY bt.name ORDER BY qty DESC, bt.name ASC"),
						    @NamedQuery(name = Orders.MEAL_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD, query = "SELECT COUNT(meal.name) as qty, meal.name FROM Orders o"
							    + " JOIN o.meals meal"
							    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"
							    + " GROUP BY meal.name ORDER BY qty DESC, meal.name ASC"),
							    @NamedQuery(name = Orders.MEAL_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD, query = "SELECT COUNT(mt.name) as qty, mt.name FROM Orders o"
								    + " JOIN o.meals meal"
								    + " JOIN meal.mealType mt"
								    + " WHERE o.subsidiary.idSubsidiary = :idSubsidiary AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'"
								    + " GROUP BY mt.name ORDER BY qty DESC, mt.name ASC") })
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_BY_SUBSIDIARY_AND_STATUS = "Orders.findByIdSubsidiaryAndStatus";
    public static final String FIND_BY_SUBSIDIARY = "Orders.findByIdSubsidiary";
    public static final String FIND_BY_USER_AND_SUBSIDIARY = "Orders.findBySubsidiaryAndUser";
    public static final String FIND_BY_ID_ORDER_AND_USER = "Orders.findByIdOrderAndUser";
    public static final String FIND_LAST_LOCAL_ORDER_NUMBER = "FIND_LAST_LOCAL_ORDER_NUMBER";
    public static final String FIND_ALL_BY_USER = "Orders.findAllByUser";
    public static final String FIND_TOTAL_PRICE_LAST_MONTH = "Orders.findTotalPriceLastMonth";
    public static final String FIND_QTY_BETWEEN_DATES = "Orders.findQtyBetweenDates";
    public static final String FIND_AVG_ORDER_PRICE = "Orders.findAvgOrderPrice";
    public static final String FIND_BY_ORDERSTATUS_AND_TIME = "Orders.findByOrderStatusAndTime";
    public static final String FIND_BY_SUBSIDIARY_AND_ORDERTYPE = "Orders.findOrdersBySubsidiaryAndType";
    public static final String FIND_BY_SUBSIDIARY_AND_TIME = "Orders.findOrdersBySubsidiaryAndTime";
    public static final String FIND_AVAILABLE_TO_RATE_BY_USER = "Orders.findAvailableToRateByUser";
    public static final String FIND_TOTAL_ORDER_NUMBER_BY_SUBSIDIARY = "FIND_TOTAL_ORDER_NUMBER_BY_SUBSIDIARY";
    public static final String FIND_AVAILABLE_TO_RATE_BY_ORDER = "Orders.findAvailableToRateByOrder";
    public static final String FIND_CANCELED_BY_SUBSIDIARY_AND_DATE = "Orders.findCanceledBySubsidiaryAndDate";
    public static final String FIND_OPEN_BILL_TO_SUBSIDIARY_BOARD = "Orders.findOpenBillToSubsidiaryBoard";
    public static final String FIND_BY_ID_ORDER_AND_BOARD = "Orders.findByOrderAndBoard";
    public static final String FIND_OPEN_ORDER_BY_BOARD = "Orders.findOpenOrderByBoard";
    public static final String COUNT_ORDERS_IN_NEIGHBORHOOD_AND_SUBSIDIARY_AND_PERIOD = "COUNT_ORDERS_IN_NEIGHBORHOOD_AND_SUBSIDIARY_AND_PERIOD";
    public static final String COUNT_ORDERS_BY_SUBSIDIARY = "COUNT_ORDERS_BY_SUBSIDIARY";
    public static final String MONEY_PAYMENT_METHOD_BY_SUBSIDIARY_AND_PERIOD = "MONEY_PAYMENT_METHOD_BY_SUBSIDIARY_AND_PERIOD";
    public static final String PAYMENT_METHOD_USED_BY_SUBSIDIARY_AND_PERIOD = "PAYMENT_METHOD_USED_BY_SUBSIDIARY_AND_PERIOD";
    public static final String BEVERAGE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD = "BEVERAGE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD";
    public static final String BEVERAGE_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD = "BEVERAGE_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD";
    public static final String MEAL_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD = "MEAL_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD";
    public static final String MEAL_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD = "MEAL_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idOrders", nullable = false)
    private Integer idOrders;
    @Max(value = 99999)
    @Min(value = 0)
    @Basic(optional = false)
    @Column(name = "totalPrice", nullable = false, precision = 7, scale = 2)
    private Double totalPrice;
    @Basic(optional = false)
    @Column(name = "orderStatus", nullable = false, length = 11)
    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus orderStatus;
    @Column(name = "note", length = 300)
    private String note;
    @Column(name = "discount", precision = 7, scale = 2)
    private Double discount;
    @Basic(optional = false)
    @Column(name = "orderMade", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderMade;
    @Column(name = "orderSent")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderSent;
    @Column(name = "userIP")
    private String userIP;
    @JoinTable(name = "Order_has_Meal", joinColumns = { @JoinColumn(name = "idOrders", referencedColumnName = "idOrders", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idMeal", referencedColumnName = "idMeal", nullable = false) })
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Meal> meals;
    @JoinTable(name = "Orders_has_Beverage", joinColumns = { @JoinColumn(name = "idOrders", referencedColumnName = "idOrders", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idBeverage", referencedColumnName = "idBeverage", nullable = false) })
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Beverage> beverages;
    @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Subsidiary subsidiary;
    @JoinColumn(name = "idDiscountCoupon", referencedColumnName = "idDiscountCoupon")
    @ManyToOne(fetch = FetchType.LAZY)
    private DiscountCoupon discountCoupon;
    @JoinColumn(name = "idOrderType", referencedColumnName = "idOrderType", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private OrderType orderType;
    @JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "idEmployee", referencedColumnName = "idEmployee")
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    private List<BeverageOrder> beveragesOrder;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    private List<MealOrder> mealsOrder;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderPaymentMethod> orderPaymentMethod;
    @JoinColumn(name = "idAddress", referencedColumnName = "idAddress")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Address address;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    private List<Rating> ratings;
    @Column(name = "localOrderNumber")
    private Integer localOrderNumber;
    @Column(name = "orderChange", precision = 5, scale = 2)
    @Max(value = 999, message = "maximum change")
    private Double orderChange;
    @Column(name = "deliveryPrice", precision = 5, scale = 2)
    private Double deliveryPrice;
    @Column(name = "cancelReason", length = 300)
    private String cancelReason;
    @Column(name = "totalOrderNumber")
    private Integer totalOrderNumber;
    @JoinColumn(name = "idBoard", referencedColumnName = "idBoard")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Board board;
    @Column(name = "employeeLastStatus")
    private String employeeLastStatus;
    @Column(name = "boardPayed")
    private Boolean boardPayed = false;
    @Column(name = "orderOrigin", nullable = false, length = 11)
    @Enumerated(EnumType.STRING)
    private OrderOrigin orderOrigin;
    
    public Orders() {
    }
    
    public Orders(Integer idOrders) {
	this.idOrders = idOrders;
    }
    
    public Orders(Integer idOrders, Double totalPrice, OrderStatus orderStatus, Date orderMade) {
	this.idOrders = idOrders;
	this.totalPrice = totalPrice;
	this.orderStatus = orderStatus;
	this.orderMade = orderMade;
    }
    
    public Integer getId() {
	return idOrders;
    }
    
    public void setId(Integer idOrders) {
	this.idOrders = idOrders;
    }
    
    public Double getTotalPrice() {
	return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
	this.totalPrice = totalPrice;
    }
    
    public OrderStatus getOrderStatus() {
	return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
	this.orderStatus = orderStatus;
    }
    
    public String getNote() {
	return note;
    }
    
    public void setNote(String note) {
	this.note = note;
    }
    
    public Double getDiscount() {
	return discount;
    }
    
    public void setDiscount(Double discount) {
	this.discount = discount;
    }
    
    public Date getOrderMade() {
	return orderMade;
    }
    
    public void setOrderMade(Date orderMade) {
	this.orderMade = orderMade;
    }
    
    public Date getOrderSent() {
	return orderSent;
    }
    
    public void setOrderSent(Date orderSent) {
	this.orderSent = orderSent;
    }
    
    public String getUserIP() {
	return userIP;
    }
    
    public void setUserIP(String userIP) {
	this.userIP = userIP;
    }
    
    @XmlTransient
    public List<Meal> getMeals() {
	return meals;
    }
    
    public void setMeals(List<Meal> mealList) {
	meals = mealList;
    }
    
    public Subsidiary getSubsidiary() {
	return subsidiary;
    }
    
    public void setSubsidiary(Subsidiary idSubsidiary) {
	subsidiary = idSubsidiary;
    }
    
    public DiscountCoupon getDiscountCoupon() {
	return discountCoupon;
    }
    
    public void setDiscountCoupon(DiscountCoupon idDiscountCoupon) {
	discountCoupon = idDiscountCoupon;
    }
    
    public OrderType getOrderType() {
	return orderType;
    }
    
    public void setOrderType(OrderType idOrderType) {
	orderType = idOrderType;
    }
    
    public User getUser() {
	return user;
    }
    
    public void setUser(User idUser) {
	user = idUser;
    }
    
    public Employee getEmployee() {
	return employee;
    }
    
    public void setEmployee(Employee employee) {
	this.employee = employee;
    }
    
    public List<Beverage> getBeverages() {
	return beverages;
    }
    
    public void setBeverages(List<Beverage> beverages) {
	this.beverages = beverages;
    }
    
    public List<BeverageOrder> getBeveragesOrder() {
	return beveragesOrder;
    }
    
    public void setBeveragesOrder(List<BeverageOrder> beveragesOrder) {
	this.beveragesOrder = beveragesOrder;
    }
    
    public List<MealOrder> getMealsOrder() {
	return mealsOrder;
    }
    
    public void setMealsOrder(List<MealOrder> mealsOrder) {
	this.mealsOrder = mealsOrder;
    }
    
    public List<OrderPaymentMethod> getOrderPaymentMethod() {
	return orderPaymentMethod;
    }
    
    public void setOrderPaymentMethod(List<OrderPaymentMethod> orderPaymentMethod) {
	this.orderPaymentMethod = orderPaymentMethod;
    }
    
    public Address getAddress() {
	return address;
    }
    
    public void setAddress(Address address) {
	this.address = address;
    }
    
    public List<Rating> getRatings() {
	return ratings;
    }
    
    public void setRatings(List<Rating> ratings) {
	this.ratings = ratings;
    }
    
    public Integer getLocalOrderNumber() {
	return localOrderNumber;
    }
    
    public void setLocalOrderNumber(Integer orderNumber) {
	localOrderNumber = orderNumber;
    }
    
    public Double getOrderChange() {
	return orderChange;
    }
    
    public void setOrderChange(Double orderChange) {
	this.orderChange = orderChange;
    }
    
    public Double getDeliveryPrice() {
	return deliveryPrice;
    }
    
    public void setDeliveryPrice(Double deliveryPrice) {
	this.deliveryPrice = deliveryPrice;
    }
    
    public String getCancelReason() {
	return cancelReason;
    }
    
    public void setCancelReason(String cancelReason) {
	this.cancelReason = cancelReason;
    }
    
    public Integer getTotalOrderNumber() {
	return totalOrderNumber;
    }
    
    public void setTotalOrderNumber(Integer totalOrderNumber) {
	this.totalOrderNumber = totalOrderNumber;
    }
    
    @JsonIgnore
    public Board getBoard() {
	return board;
    }
    
    @JsonIgnore
    public void setBoard(Board board) {
	this.board = board;
    }
    
    public String getEmployeeLastStatus() {
	return employeeLastStatus;
    }
    
    public void setEmployeeLastStatus(String employeelastStatus) {
	employeeLastStatus = employeelastStatus;
    }
    
    public Boolean getBoardPayed() {
	return boardPayed;
    }
    
    public void setBoardPayed(Boolean boardPayed) {
	this.boardPayed = boardPayed;
    }
    
    public OrderOrigin getOrderOrigin() {
	return orderOrigin;
    }
    
    public void setOrderOrigin(OrderOrigin orderOrigin) {
	this.orderOrigin = orderOrigin;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (discount == null ? 0 : discount.hashCode());
	result = prime * result + (discountCoupon == null ? 0 : discountCoupon.hashCode());
	result = prime * result + (orderType == null ? 0 : orderType.hashCode());
	result = prime * result + (idOrders == null ? 0 : idOrders.hashCode());
	result = prime * result + (subsidiary == null ? 0 : subsidiary.hashCode());
	result = prime * result + (user == null ? 0 : user.hashCode());
	result = prime * result + (meals == null ? 0 : meals.hashCode());
	result = prime * result + (orderMade == null ? 0 : orderMade.hashCode());
	result = prime * result + (orderSent == null ? 0 : orderSent.hashCode());
	result = prime * result + (orderStatus == null ? 0 : orderStatus.hashCode());
	result = prime * result + (totalPrice == null ? 0 : totalPrice.hashCode());
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
	Orders other = (Orders) obj;
	if (discount == null) {
	    if (other.discount != null)
		return false;
	} else if (!discount.equals(other.discount))
	    return false;
	if (discountCoupon == null) {
	    if (other.discountCoupon != null)
		return false;
	} else if (!discountCoupon.equals(other.discountCoupon))
	    return false;
	if (orderType == null) {
	    if (other.orderType != null)
		return false;
	} else if (!orderType.equals(other.orderType))
	    return false;
	if (idOrders == null) {
	    if (other.idOrders != null)
		return false;
	} else if (!idOrders.equals(other.idOrders))
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
	if (meals == null) {
	    if (other.meals != null)
		return false;
	} else if (!meals.equals(other.meals))
	    return false;
	if (orderMade == null) {
	    if (other.orderMade != null)
		return false;
	} else if (!orderMade.equals(other.orderMade))
	    return false;
	if (orderSent == null) {
	    if (other.orderSent != null)
		return false;
	} else if (!orderSent.equals(other.orderSent))
	    return false;
	if (orderStatus == null) {
	    if (other.orderStatus != null)
		return false;
	} else if (!orderStatus.equals(other.orderStatus))
	    return false;
	if (totalPrice == null) {
	    if (other.totalPrice != null)
		return false;
	} else if (!totalPrice.equals(other.totalPrice))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "Orders [idOrders=" + idOrders + ", totalPrice=" + totalPrice + ", orderStatus=" + orderStatus
		+ ", discount=" + discount + ", orderMade=" + orderMade + ", orderSent=" + orderSent + ", mealList="
		+ meals + ", idSubsidiary=" + subsidiary + ", idDiscountCoupon=" + discountCoupon + ", idOrderType="
		+ orderType + ", idUser=" + user + "]";
    }
    
}
