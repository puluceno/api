package br.com.redefood.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.hibernate.Hibernate;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.City;
import br.com.redefood.model.DeliveryArea;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealRating;
import br.com.redefood.model.Module;
import br.com.redefood.model.Neighborhood;
import br.com.redefood.model.OpenTime;
import br.com.redefood.model.Rating;
import br.com.redefood.model.Restaurant;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryMessages;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("/square")
public class SquareResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @SuppressWarnings("unchecked")
    // @Cache(maxAge = 10)
    @GET
    @Produces("application/json;charset=UTF8")
    public String populateSquareRestaurants(@HeaderParam("locale") String locale,
	    @QueryParam("name") @DefaultValue("") String name, @QueryParam("restaurantType") String restaurantTypesID,
	    @QueryParam("open") @DefaultValue("true") boolean open, @QueryParam("neighborhood") Short idNeighborhood,
	    @QueryParam("city") Short idCity, @QueryParam("trackOrder") boolean trackOrder,
	    @QueryParam("paymentMethod") String paymentMethodsID) {
	
	try {
	    StringBuilder query = new StringBuilder();
	    query.append("SELECT DISTINCT s FROM Subsidiary s ");
	    query.append("INNER JOIN FETCH s.idRestaurant r ");
	    query.append("INNER JOIN s.openTime ot ");
	    query.append("LEFT JOIN r.restaurantTypes rt ");
	    query.append("INNER JOIN s.subsidiaryModules sm ");
	    // Neighborhood filter OR City filter
	    if (idNeighborhood != null || idCity != null) {
		query.append("INNER JOIN s.deliveryAreas d ");
	    }
	    if (paymentMethodsID != null && !paymentMethodsID.isEmpty()) {
		query.append("INNER JOIN s.paymentMethodList pm ");
	    }
	    
	    // // Wheres clauses
	    // Module clause
	    query.append("WHERE sm.module.idModule IN (");
	    query.append(Module.MODULE_SQUARE);
	    query.append(",");
	    query.append(Module.MODULE_SITE);
	    query.append(") ");
	    // Square active parameter
	    query.append(" AND s.squareActive = true ");
	    // Online OpenTime clause
	    query.append("AND ot.localOpenTime = false ");
	    // Restaurant name filter
	    if (name != null && !name.isEmpty()) {
		query.append("AND s.name LIKE :name ");
	    }
	    // Open filter
	    if (open) {
		query.append("AND  DAYOFWEEK(NOW()) = ot.daysOfWeek.id AND CURTIME() BETWEEN ot.open AND ot.close ");
	    }
	    // RestaurantType filter
	    if (restaurantTypesID != null && !restaurantTypesID.isEmpty()) {
		query.append("AND rt.idRestaurantType IN (" + restaurantTypesID + ") ");
	    }
	    // City filter
	    if (idCity != null) {
		query.append("AND d.neighborhood.idCity.idCity = :idCity ");
	    }
	    // Neighborhood filter
	    if (idNeighborhood != null) {
		query.append("AND d.neighborhood.id = :idNeighborhood ");
	    }
	    // TrackOrder filter
	    if (trackOrder) {
		query.append("AND s.trackOrder = true ");
	    }
	    // PaymentMethod filter
	    if (paymentMethodsID != null && !paymentMethodsID.isEmpty()) {
		query.append(" AND pm.idPaymentMethod IN (" + paymentMethodsID + ") ");
	    }
	    
	    Query createQuery = em.createQuery(query.toString().replace("[", "").replace("]", ""));
	    
	    if (idCity != null) {
		createQuery.setParameter("idCity", idCity);
	    }
	    
	    if (idNeighborhood != null) {
		createQuery.setParameter("idNeighborhood", idNeighborhood);
	    }
	    
	    if (name != null && !name.isEmpty()) {
		createQuery.setParameter("name", RedeFoodConstants.SQL_LIKE_WILDCARD + name
			+ RedeFoodConstants.SQL_LIKE_WILDCARD);
	    }
	    
	    // Query without pagination
	    List<Subsidiary> subsidiaryList = createQuery.getResultList();
	    
	    // String queryDT =
	    // "SELECT AVG(TIME_TO_SEC(TIMEDIFF(o.orderSent, o.orderMade))) FROM Orders o WHERE o.orderSent IS NOT NULL AND o.orderMade BETWEEN DATE_SUB(NOW(), INTERVAL 10 DAY) AND NOW() AND o.idSubsidiary = :idSubsidiary";
	    
	    List<Map<String, Object>> squareList = new ArrayList<Map<String, Object>>();
	    
	    if (subsidiaryList != null && !subsidiaryList.isEmpty()) {
		for (Subsidiary sub : subsidiaryList) {
		    
		    Map<String, Object> rest = new HashMap<String, Object>();
		    
		    rest.put("name", sub.getName());
		    rest.put("logo", sub.getRestaurant().getLogo());
		    rest.put("idRestaurant", sub.getRestaurant().getIdRestaurant());
		    rest.put("idSubsidiary", sub.getId());
		    
		    Hibernate.initialize(sub.getRestaurant().getRestaurantTypes());
		    rest.put("restaurantType", sub.getRestaurant().getRestaurantTypes());
		    
		    for (OpenTime ot : sub.getOpenTime()) {
			Hibernate.initialize(ot.getDayOfWeek());
		    }
		    rest.put("openTime", sub.getOpenTime());
		    
		    Hibernate.initialize(sub.getPaymentMethod());
		    rest.put("paymentMethod", sub.getPaymentMethod());
		    
		    rest.put("minOrder", sub.getMinOrder());
		    rest.put("deliveryTime", sub.getAvgDeliveryTime());
		    rest.put("trackOrder", sub.isTrackOrder());
		    rest.put("contact", sub.getContact());
		    rest.put("slug", LocaleResource.deAccent(sub.getName().toLowerCase().replace(" ", "-")));
		    rest.put(
			    "minPrice",
			    em.createNamedQuery(Meal.FIND_MIN_PRICE_BY_SUBSIDIARY)
			    .setParameter("idSubsidiary", sub.getId()).getSingleResult());
		    rest.put(
			    "maxPrice",
			    em.createNamedQuery(Meal.FIND_MAX_PRICE_BY_SUBSIDIARY)
			    .setParameter("idSubsidiary", sub.getId()).getSingleResult());
		    // Delivery Tax
		    if (idNeighborhood != null) {
			for (DeliveryArea deliveryArea : sub.getDeliveryAreas())
			    if (deliveryArea.getNeighborhood().getId().intValue() == idNeighborhood.intValue()) {
				rest.put("deliveryTax", deliveryArea.getTax());
			    }
		    }
		    
		    // Average Rating
		    rest.put("averageRating", calculateSubsidiaryAvgRating(sub));
		    
		    squareList.add(rest);
		}
	    }
	    
	    return mapper.writeValueAsString(squareList);
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    private Map<String, Object> calculateSubsidiaryAvgRating(Subsidiary sub) {
	HashMap<String, Object> avgRating = new HashMap<String, Object>();
	
	int sumDelivery = 0;
	Double avgDelivery = 0.0;
	int sumCostBenefit = 0;
	Double avgCostBenefit = 0.0;
	int sumExperience = 0;
	Double avgExperience = 0.0;
	int sumMealRating = 0;
	Double avgMealRating = 0.0;
	int sizeMealRating = 0;
	try {
	    if (sub.getRatingList() != null && !sub.getRatingList().isEmpty()) {
		for (Rating rating : sub.getRatingList()) {
		    sumDelivery += rating.getDelivery() == null ? 0 : rating.getDelivery();
		    sumCostBenefit += rating.getCostBenefit() == null ? 0 : rating.getCostBenefit();
		    sumExperience += rating.getExperience() == null ? 0 : rating.getExperience();
		    
		    for (MealRating mealRating : rating.getMealRatings()) {
			sumMealRating += mealRating.getMealRating() == null ? 0 : mealRating.getMealRating();
		    }
		    sizeMealRating += rating.getMealRatings().size();
		}
		if (sub.getRatingList().size() != 0) {
		    avgDelivery = (double) (sumDelivery / sub.getRatingList().size());
		    avgCostBenefit = (double) (sumCostBenefit / sub.getRatingList().size());
		    avgExperience = (double) (sumExperience / sub.getRatingList().size());
		}
		if (sizeMealRating != 0) {
		    avgMealRating = (double) (sumMealRating / sizeMealRating);
		}
	    }
	} catch (Exception e) {
	    return null;
	}
	
	avgRating.put("avgDelivery", avgDelivery);
	avgRating.put("avgCostBenefit", avgCostBenefit);
	avgRating.put("avgExperience", avgExperience);
	avgRating.put("avgMealRating", avgMealRating);
	avgRating.put("avgRating", (avgDelivery + avgCostBenefit + avgExperience + avgMealRating) / 4);
	
	return avgRating;
    }
    
    @GET
    @Path("/cities")
    // @Cache(maxAge = 600)
    @Produces("application/json;charset=UTF8")
    public String findCitiesWithRestaurants(@HeaderParam("locale") String locale) {
	try {
	    
	    return mapper.writeValueAsString(em.createNamedQuery(City.FIND_ATTENDED_CITY).getResultList());
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @GET
    // @Cache(maxAge = 600)
    @Path("/city/{idCity:[0-9][0-9]*}/neighborhoods/attended")
    @Produces("application/json;charset=UTF8")
    public String findAttendedNeighborhoods(@HeaderParam("locale") String locale, @PathParam("idCity") Short idCity) {
	try {
	    
	    return mapper.writeValueAsString(em.createNamedQuery(Neighborhood.FIND_ATTENDED_NEIGHBORHOOD_BY_CITY)
		    .setParameter("idCity", idCity).getResultList());
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @GET
    // @Cache
    @Path("/city/{idCity:[0-9][0-9]*}/neighborhoods")
    @Produces("application/json;charset=UTF8")
    public String findAllNeighborhoodsByCity(@HeaderParam("locale") String locale, @PathParam("idCity") Short idCity) {
	try {
	    
	    return mapper.writeValueAsString(em.createNamedQuery(Neighborhood.FIND_NEIGHBORHOOD_BY_CITY)
		    .setParameter("idCity", idCity).getResultList());
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @GET
    // @Cache(maxAge = 600)
    @Path("/{idRestaurant:[0-9][0-9]*}/subsidiary/{idSubsidiary:[0-9][0-9]*}")
    @Produces("application/json;charset=UTF8")
    public String findRestaurantDetails(@HeaderParam("locale") String locale,
	    @PathParam("idRestaurant") Short idRestaurant, @PathParam("idSubsidiary") Short idSubsidiary) {
	
	try {
	    List<Map<String, Object>> square = new ArrayList<Map<String, Object>>();
	    Restaurant restaurant = em.find(Restaurant.class, idRestaurant);
	    Subsidiary sub = em.find(Subsidiary.class, idSubsidiary);
	    Map<String, Object> rest = new HashMap<String, Object>();
	    rest.put("name", sub.getName());
	    rest.put("logo", restaurant.getLogo());
	    rest.put("idRestaurant", restaurant.getIdRestaurant());
	    rest.put("idSubsidiary", sub.getId().toString());
	    
	    Hibernate.initialize(restaurant.getRestaurantTypes());
	    rest.put("restaurantType", restaurant.getRestaurantTypes());
	    
	    for (OpenTime ot : sub.getOpenTime()) {
		Hibernate.initialize(ot.getDayOfWeek());
	    }
	    rest.put("openTime", sub.getOpenTime());
	    
	    List<SubsidiaryMessages> messages = new ArrayList<SubsidiaryMessages>();
	    for (SubsidiaryMessages subsidiaryMessages : sub.getMessages()) {
		Hibernate.initialize(subsidiaryMessages.getMessage());
		messages.add(subsidiaryMessages);
	    }
	    rest.put("messages", messages);
	    
	    Hibernate.initialize(sub.getAddress().getCity());
	    rest.put("address", sub.getAddress());
	    
	    Hibernate.initialize(sub.getPaymentMethod());
	    rest.put("paymentMethod", sub.getPaymentMethod());
	    
	    rest.put("minOrder", sub.getMinOrder());
	    /*
	     * if (sub.isTrackOrder()) { String queryDT =
	     * "SELECT AVG(TIME_TO_SEC(TIMEDIFF(o.orderSent, o.orderMade))) FROM Orders o WHERE o.orderSent IS NOT NULL AND o.orderMade BETWEEN DATE_SUB(NOW(), INTERVAL 10 DAY) AND NOW() AND o.idSubsidiary = :idSubsidiary"
	     * ; BigDecimal singleResult = (BigDecimal)
	     * em.createNativeQuery(queryDT) .setParameter("idSubsidiary",
	     * sub.getId()).getSingleResult(); rest.put("deliveryTime",
	     * singleResult == null ? null : singleResult.divide(new
	     * BigDecimal(60), 0, 4)); }
	     */
	    rest.put("deliveryTime", sub.getAvgDeliveryTime());
	    rest.put("trackOrder", sub.isTrackOrder());
	    rest.put("contact", sub.getContact());
	    rest.put("description", sub.getDescription());
	    
	    for (DeliveryArea deliveryArea : sub.getDeliveryAreas()) {
		Hibernate.initialize(deliveryArea.getNeighborhood());
	    }
	    rest.put("deliveryAreas", sub.getDeliveryAreas());
	    
	    // Average Rating
	    rest.put("averageRating", calculateSubsidiaryAvgRating(sub));
	    
	    square.add(rest);
	    
	    return mapper.writeValueAsString(rest);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
}
