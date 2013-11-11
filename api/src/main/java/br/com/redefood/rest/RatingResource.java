package br.com.redefood.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.restaurant.SubsidiaryCommentReceivedNotificator;
import br.com.redefood.mail.notificator.user.UserCommentReceivedNotificator;
import br.com.redefood.model.Login;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealOrder;
import br.com.redefood.model.MealOrderIngredient;
import br.com.redefood.model.MealRating;
import br.com.redefood.model.Orders;
import br.com.redefood.model.Rating;
import br.com.redefood.model.User;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodMailUtil;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class RatingResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/rating")
    @Produces("application/json;charset=UTF8")
    public String findSubsidiaryRating(@HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @DefaultValue("1") @QueryParam("offset") Integer offset,
	    @DefaultValue("20") @QueryParam("limit") Integer limit) {
	
	try {
	    List<Rating> ratingsList = em.createNamedQuery(Rating.FIND_BY_SUBSIDIARY)
		    .setParameter("idSubsidiary", idSubsidiary).setMaxResults(limit).setFirstResult(offset - 1)
		    .getResultList();
	    
	    List<Object> ratingList = new ArrayList<Object>();
	    
	    for (Rating rating : ratingsList) {
		List<HashMap<String, Object>> mealRatings = new ArrayList<HashMap<String, Object>>();
		
		for (MealRating mealRating : rating.getMealRatings()) {
		    HashMap<String, Object> mealRatingObj = new HashMap<String, Object>();
		    mealRatingObj.put("mealRating", mealRating.getMealRating());
		    mealRatingObj.put("comment", mealRating.getComment());
		    mealRatingObj.put("meal", mealRating.getMeal().getName());
		    mealRatingObj.put("id", mealRating.getIdMealRating());
		    mealRatings.add(mealRatingObj);
		}
		HashMap<String, Object> user = new HashMap<String, Object>();
		user.put("firstName", rating.getUser().getFirstName());
		user.put("lastName", rating.getUser().getLastName());
		
		HashMap<String, Object> ratingObj = new HashMap<String, Object>();
		ratingObj.put("delivery", rating.getDelivery());
		ratingObj.put("costBenefit", rating.getCostBenefit());
		ratingObj.put("experience", rating.getExperience());
		ratingObj.put("comment", rating.getComment());
		
		if (rating.getRatingDate() != null) {
		    ratingObj.put("ratingDate", RedeFoodUtils.formatDateOnly(rating.getRatingDate()));
		}
		
		ratingObj.put("reply", rating.getReply());
		if (rating.getReplyDate() != null) {
		    ratingObj.put("replyDate", RedeFoodUtils.formatDateOnly(rating.getReplyDate()));
		}
		
		ratingObj.put("rejoinder", rating.getRejoinder());
		if (rating.getRejoinderDate() != null) {
		    ratingObj.put("rejoinderDate", RedeFoodUtils.formatDateOnly(rating.getRejoinderDate()));
		}
		
		ratingObj.put("idOrders", rating.getOrder().getId());
		ratingObj.put("orderNumber", rating.getOrder().getTotalOrderNumber());
		ratingObj.put("id", rating.getIdRating());
		ratingObj.put("mealRatings", mealRatings);
		ratingObj.put("user", user);
		ratingList.add(ratingObj);
	    }
	    
	    HashMap<String, Object> ratings = new HashMap<String, Object>();
	    ratings.put("count",
		    em.createNamedQuery(Rating.COUNT_BY_SUBSIDIARY).setParameter("idSubsidiary", idSubsidiary)
		    .getSingleResult());
	    ratings.put("ratings", ratingList);
	    
	    return mapper.writeValueAsString(ratings);
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandler(e, locale);
	}
    }
    
    @Securable
    @GET
    @Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order/{idOrder:[0-9][0-9]*}/rating")
    @Produces("application/json;charset=UTF8")
    public String findOrderRating(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
	    @PathParam("idOrder") Integer idOrder) {
	
	try {
	    Rating rating = (Rating) em.createNamedQuery(Rating.FIND_BY_ORDER).setParameter("idOrder", idOrder)
		    .getSingleResult();
	    
	    HashMap<String, Object> json = new HashMap<String, Object>();
	    json.put("delivery", rating.getDelivery());
	    json.put("costBenefit", rating.getCostBenefit());
	    json.put("experience", rating.getExperience());
	    json.put("orderMade", RedeFoodUtils.formatDateTime(rating.getOrder().getOrderMade()));
	    json.put("comment", rating.getComment());
	    json.put("ratingDate", RedeFoodUtils.formatDateOnly(rating.getRatingDate()));
	    json.put("reply", rating.getReply());
	    json.put("replyDate", RedeFoodUtils.formatDateOnly(rating.getReplyDate()));
	    json.put("rejoinder", rating.getRejoinder());
	    json.put("rejoinderDate", RedeFoodUtils.formatDateOnly(rating.getRejoinderDate()));
	    json.put("id", rating.getIdRating());
	    json.put("idOrder", rating.getOrder().getId());
	    json.put("orderNumber", rating.getOrder().getTotalOrderNumber());
	    
	    List<HashMap<String, Object>> listMealRating = new ArrayList<HashMap<String, Object>>();
	    for (MealRating mealRating : rating.getMealRatings()) {
		HashMap<String, Object> mr = new HashMap<String, Object>();
		mr.put("id", mealRating.getIdMealRating());
		mr.put("comment", mealRating.getComment());
		mr.put("mealRating", mealRating.getMealRating());
		mr.put("name", mealRating.getMeal().getName());
		listMealRating.add(mr);
	    }
	    json.put("mealRatings", listMealRating);
	    
	    return mapper.writeValueAsString(json);
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandlerResponse(e, locale, String.valueOf(idOrder)).getEntity().toString();
	}
    }
    
    @SuppressWarnings("unchecked")
    @Securable
    @GET
    @Path("/user/{idUser:[0-9][0-9]*}/rating")
    @Produces("application/json;charset=UTF8")
    public String findUserRating(@HeaderParam("locale") String locale, @PathParam("idUser") Integer idUser,
	    @DefaultValue("1") @QueryParam("offset") Integer offset,
	    @DefaultValue("20") @QueryParam("limit") Integer limit) {
	
	try {
	    List<Rating> ratings = em.createNamedQuery(Rating.FIND_BY_USER).setParameter("idUser", idUser)
		    .setMaxResults(limit).setFirstResult(offset - 1).getResultList();
	    
	    return mapper.writeValueAsString(ratings);
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandler(e, locale);
	}
    }
    
    @SuppressWarnings("unchecked")
    @Securable
    @GET
    @Path("/me/user/rating/available")
    @Produces("application/json;charset=UTF8")
    public String findOrdersToRate(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @QueryParam("idSubsidiary") Short idSubsidiary, @DefaultValue("1") @QueryParam("offset") Integer offset,
	    @DefaultValue("20") @QueryParam("limit") Integer limit) {
	try {
	    Login login = em.find(Login.class, token);
	    User user = em.find(User.class, login.getIdUser());
	    if (user == null)
		throw new Exception("user not found");
	    
	    String sub = "";
	    if (idSubsidiary != null) {
		sub = "AND o.subsidiary.idSubsidiary = :idSubsidiary";
	    }
	    String query = "SELECT o FROM Orders o LEFT JOIN o.rating r WHERE r.idRating IS NULL AND o.user.idUser = :idUser AND o.orderStatus <> 'CANCELED' "
		    + sub + " ORDER BY o.orderMade DESC";
	    Query queryOrders = em.createQuery(query).setParameter("idUser", user.getId());
	    if (idSubsidiary != null) {
		queryOrders.setParameter("idSubsidiary", idSubsidiary);
	    }
	    
	    List<Orders> orders = queryOrders.setMaxResults(limit).setFirstResult(offset - 1).getResultList();
	    
	    return mapper.writeValueAsString(findAvailableOrders(orders));
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandler(e, locale);
	}
    }
    
    private List<HashMap<String, Object>> findAvailableOrders(List<Orders> orders) {
	
	List<HashMap<String, Object>> availableOrders = new ArrayList<HashMap<String, Object>>();
	for (Orders order : orders) {
	    HashMap<String, Object> ordersToRate = new HashMap<String, Object>();
	    HashMap<String, Object> subsidiary = new HashMap<String, Object>();
	    subsidiary.put("name", order.getSubsidiary().getName());
	    subsidiary.put("id", order.getSubsidiary().getId());
	    ordersToRate.put("subsidiary", subsidiary);
	    
	    List<HashMap<String, Object>> mealsAvailable = new ArrayList<HashMap<String, Object>>();
	    for (MealOrder mealOrder : order.getMealsOrder()) {
		HashMap<String, Object> meals = new HashMap<String, Object>();
		meals.put("name", mealOrder.getName());
		meals.put("id", mealOrder.getIdMeal());
		
		List<HashMap<String, Object>> ingredients = new ArrayList<HashMap<String, Object>>();
		for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
		    HashMap<String, Object> ingredient = new HashMap<String, Object>();
		    ingredient.put("name", mealOrderIngredient.getName());
		    ingredients.add(ingredient);
		}
		meals.put("ingredients", ingredients);
		mealsAvailable.add(meals);
		
	    }
	    ordersToRate.put("meals", mealsAvailable);
	    ordersToRate.put("orderStatus", order.getOrderStatus());
	    ordersToRate.put("orderMade", order.getOrderMade());
	    ordersToRate.put("totalPrice", order.getTotalPrice());
	    ordersToRate.put("deliveryPrice", order.getDeliveryPrice());
	    ordersToRate.put("id", order.getId());
	    availableOrders.add(ordersToRate);
	}
	return availableOrders;
    }
    
    @Securable
    @GET
    @Path("/me/user/rating/available/{idOrder:[0-9][0-9]*}")
    @Produces("application/json;charset=UTF8")
    public String findOrderToRate(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idOrder") Integer idOrder) {
	try {
	    Login login = em.find(Login.class, token);
	    User user = em.find(User.class, login.getIdUser());
	    if (user == null)
		throw new Exception("user not found");
	    
	    Orders order = (Orders) em.createNamedQuery(Orders.FIND_AVAILABLE_TO_RATE_BY_ORDER)
		    .setParameter("idOrders", idOrder).setParameter("idUser", user.getId()).getSingleResult();
	    
	    List<Orders> orders = new ArrayList<Orders>();
	    orders.add(order);
	    
	    return mapper.writeValueAsString(findAvailableOrders(orders));
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandler(e, locale, String.valueOf(idOrder));
	}
    }
    
    @Securable
    @POST
    @Path("/order/{idOrder:[0-9][0-9]*}/rating")
    public Response createFeedback(@HeaderParam("locale") String locale, @PathParam("idOrder") Integer idOrder,
	    @QueryParam("idSubsidiary") Short idSubsidiary, @QueryParam("originUrl") String originUrl, Rating rating) {
	
	try {
	    Orders order = em.find(Orders.class, idOrder);
	    if (order == null || order.getRating() != null)
		throw new Exception("Existent rating");
	    
	    log.log(Level.INFO, "Creating feedback for order " + idOrder);
	    
	    rating.setOrder(order);
	    rating.setUser(order.getUser());
	    rating.setSubsidiary(order.getSubsidiary());
	    rating.setRatingDate(new Date());
	    
	    for (MealRating mealRating : rating.getMealRatings())
		if (em.find(Meal.class, mealRating.getMeal().getId()) != null) {
		    mealRating.setRating(rating);
		} else {
		    mealRating = null;
		}
	    
	    em.persist(rating);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "feedback.created", idOrder);
	    log.log(Level.INFO, answer);
	    sendSubsidiaryRatingNotification(prepareMessage(rating, originUrl, idSubsidiary));
	    return Response.status(201).build();
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandlerResponse(e, locale,
		    String.valueOf(em.find(Orders.class, idOrder).getTotalOrderNumber()));
	}
    }
    
    @Securable
    @PUT
    @Path("/order/{idOrder:[0-9][0-9]*}/rating")
    public Response createFeedbackReplyAndRejoinder(@HeaderParam("locale") String locale,
	    @PathParam("idOrder") Integer idOrder, @QueryParam("originUrl") String originUrl,
	    @QueryParam("idSubsidiary") Short idSubsidiary, HashMap<String, String> ratingReply) {
	
	try {
	    log.log(Level.INFO, "Creating feedback reply for order id " + idOrder);
	    
	    // find rating, set reply and replyDate;
	    Rating rating = (Rating) em.createNamedQuery(Rating.FIND_BY_ORDER).setParameter("idOrder", idOrder)
		    .getSingleResult();
	    
	    String answer = "";
	    
	    if (ratingReply.get("reply") != null && !ratingReply.get("reply").isEmpty()) {
		if (rating.getReply() != null && !rating.getReply().isEmpty())
		    throw new Exception("Existent reply");
		rating.setReply(ratingReply.get("reply"));
		rating.setReplyDate(new Date());
		answer = LocaleResource.getString(locale, "feedback.reply.created", idOrder);
		sendUserRatingNotification(prepareMessage(rating, originUrl, idSubsidiary));
	    }
	    
	    if (ratingReply.get("rejoinder") != null && !ratingReply.get("rejoinder").isEmpty()) {
		if (rating.getRejoinder() != null && !rating.getRejoinder().isEmpty())
		    throw new Exception("Existent rejoinder");
		if (rating.getReply() == null || rating.getReply().isEmpty())
		    throw new Exception("Inexistent reply");
		rating.setRejoinder(ratingReply.get("rejoinder"));
		rating.setRejoinderDate(new Date());
		answer = LocaleResource.getString(locale, "feedback.rejoinder.created", idOrder);
		sendSubsidiaryRatingNotification(prepareMessage(rating, originUrl, idSubsidiary));
	    }
	    
	    log.log(Level.INFO, answer);
	    return Response.status(201).build();
	    
	} catch (Exception e) {
	    return eh.ratingExceptionHandlerResponse(e, locale, String.valueOf(idOrder));
	}
    }
    
    private void sendSubsidiaryRatingNotification(HashMap<String, String> emailData) throws Exception {
	Notificator notificator = new SubsidiaryCommentReceivedNotificator();
	log.log(Level.INFO, "Sending e-mail about Rating to subsidiary " + emailData.get("subsidiaryName"));
	notificator.send(emailData);
    }
    
    private void sendUserRatingNotification(HashMap<String, String> emailData) throws Exception {
	Notificator notificator = new UserCommentReceivedNotificator();
	log.log(Level.INFO, "Sending e-mail about reply to user " + emailData.get("userName"));
	notificator.send(emailData);
    }
    
    private HashMap<String, String> prepareMessage(Rating rating, String originUrl, Short idSubsidiary) {
	EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
	emailData.put("originUrl", originUrl);
	
	if (idSubsidiary != null) {
	    RedeFoodMailUtil.prepareSubsidiaryLogoAndFooter(emailData, rating.getSubsidiary());
	} else {
	    RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
	}
	
	emailData.put("orderNumber", String.valueOf(rating.getOrder().getTotalOrderNumber()));
	emailData.put("userName", rating.getUser().getFirstName().toUpperCase());
	
	emailData.put("restaurantName", rating.getSubsidiary().getName());
	emailData.put("addressee", rating.getSubsidiary().getEmail());
	
	if (rating.getReply() == null) {
	    emailData.put("title", RedeFoodConstants.COMMENT_TITLE + emailData.get("orderNumber"));
	    emailData.put("comment", rating.getComment());
	    createUserRatingUrl(rating, idSubsidiary, emailData, RedeFoodConstants.ADMIN_COMMENT_ANSWER_MSG,
		    RedeFoodConstants.DEFAULT_ADMIN_RATING_SUFFIX);
	} else if (rating.getRejoinder() == null) {
	    emailData.put("title", RedeFoodConstants.REPLY_TITLE + emailData.get("orderNumber"));
	    emailData.put("comment", rating.getReply());
	    emailData.put("addressee", rating.getUser().getEmail());
	    createUserRatingUrl(rating, idSubsidiary, emailData, RedeFoodConstants.USER_REPLY_ANSWER_MSG,
		    RedeFoodConstants.USER_RATING_URL_SUFFIX);
	} else {
	    emailData.put("title", RedeFoodConstants.REJOINDER_TITLE + emailData.get("orderNumber"));
	    emailData.put("comment", rating.getRejoinder());
	    createUserRatingUrl(rating, idSubsidiary, emailData, RedeFoodConstants.ADMIN_REJOINDER_ANSWER_MSG,
		    RedeFoodConstants.DEFAULT_ADMIN_RATING_SUFFIX);
	}
	
	return emailData;
    }
    
    private void createUserRatingUrl(Rating rating, Short idSubsidiary, EmailDataDTO<String, String> emailData,
	    String msg, String suffix) {
	if (idSubsidiary != null) {
	    emailData.put(
		    "ratingUrl",
		    msg.replace("*", RedeFoodUtils.urlBuilder(rating.getSubsidiary().getRestaurant().getSubdomain())
			    + suffix));
	} else {
	    emailData
	    .put("ratingUrl",
		    msg.replace("*", RedeFoodConstants.DEFAULT_REDEFOOD_URL
			    + RedeFoodConstants.USER_RATING_URL_SUFFIX));
	}
    }
}