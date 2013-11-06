package br.com.redefood.rest;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.hibernate.Hibernate;

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.user.ForgotPassNotificator;
import br.com.redefood.mail.notificator.user.NewUserNotificator;
import br.com.redefood.model.Login;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealIngredientTypes;
import br.com.redefood.model.MealIngredientTypeshasIngredient;
import br.com.redefood.model.Notifications;
import br.com.redefood.model.OrderPaymentMethod;
import br.com.redefood.model.Orders;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.User;
import br.com.redefood.model.UserOrigin;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.model.complex.RedeFoodPassword;
import br.com.redefood.util.CPFValidator;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodMailUtil;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class produces a RESTful service to read, write, update and delete
 * contents of the User table.
 */
@Path("/user")
@Stateless
public class UserResource extends HibernateMapper {
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    private static ObjectMapper mapper = HibernateMapper.getMapper();
    
    /**
     * Retrieves informations of all users present into the database. Offset and
     * Limit are required parameters.
     * 
     * @param firstName
     * @param offset
     * @param limit
     * @return
     */
    @GET
    @Produces("application/json;charset=UTF8")
    public String listByName(@HeaderParam("locale") String locale,
	    @DefaultValue("") @QueryParam("name") String firstName,
	    @DefaultValue("1") @QueryParam("offset") Integer offset,
	    @DefaultValue("20") @QueryParam("limit") Integer limit) {
	
	try {
	    return mapper.writeValueAsString(em.createNamedQuery(User.FIND_USER_BY_FIRSTNAME)
		    .setParameter("firstName", "%" + firstName + "%").setMaxResults(limit).setFirstResult(offset - 1)
		    .getResultList());
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
	}
    }
    
    /**
     * Retrieves information of a single user.
     * 
     * @param id
     * @param token
     * @return
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces("application/json;charset=UTF8")
    public String lookupUserById(@HeaderParam("locale") String locale, @PathParam("id") Integer id) {
	
	try {
	    User find = em.find(User.class, id);
	    Hibernate.initialize(find.getAddresses());
	    return mapper.writeValueAsString(find);
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
	}
    }
    
    /**
     * Method used to persist a new user and then send him an e-mail to verify
     * the authenticity of his email address.
     * 
     * @param user
     * @return
     */
    @POST
    @Consumes("application/json")
    public Response newUser(@HeaderParam("locale") String locale, @QueryParam("idSubsidiary") Short idSusbidiary,
	    User user) {
	
	log.log(Level.INFO, "Registering provisory user: " + user.getEmail());
	
	try {
	    validateCreate(user);
	    
	    // set user origin
	    if (idSusbidiary == null) {
		user.setUserOrigin(em.find(UserOrigin.class, UserOrigin.SQUARE));
	    } else {
		user.setUserOrigin(em.find(UserOrigin.class, UserOrigin.STORE));
	    }
	    
	    em.persist(user);
	    em.flush();
	    String answer = "Provisory user created : " + user.getEmail();
	    log.log(Level.INFO, answer);
	    
	    String temporaryAccess = createTemporaryAccess(user, user.getOriginURL());
	    sendNewUserNotification(user, temporaryAccess, idSusbidiary);
	    
	    answer = LocaleResource.getString(locale, "user.created", user.getEmail());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswer(201, answer);
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale, user.getEmail());
	}
    }
    
    /**
     * Method responsible to merge the edited information relative to a user
     * 
     * @param id
     * @param token
     * @param user
     * @return
     */
    @Securable
    @PUT
    @Consumes("application/json")
    @Path("/{id:[0-9][0-9]*}")
    public Response editUser(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("id") Integer id, @QueryParam("idSubsidiary") Short idSusbidiary, User user) {
	
	log.log(Level.INFO, "Merging user: " + user.getEmail());
	
	try {
	    User toMerge = validateEdit(id, token, user, idSusbidiary);
	    em.merge(toMerge);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "user.saved", user.getEmail());
	    log.log(Level.INFO, answer);
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale);
	}
    }
    
    /**
     * Users are not removed from the database, they are only deactivated.
     * 
     * @param id
     * @param token
     * @return
     */
    @Securable
    @DELETE
    @Consumes("application/json")
    @Path("/{idUser:[0-9][0-9]*}")
    public Response deleteUser(@HeaderParam("locale") String locale, @PathParam("idUser") Integer idUser) {
	
	try {
	    User toDelete = em.find(User.class, idUser);
	    log.log(Level.INFO, "Deactivating user " + toDelete.getEmail());
	    
	    toDelete.setEmailActive(false);
	    
	    em.merge(toDelete);
	    em.flush();
	    
	    String answer = LocaleResource.getProperty(locale).getProperty("user.removed");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(200, answer);
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale);
	}
	
    }
    
    @POST
    @Path("/reset")
    @Consumes("application/json")
    public Response resetUserPass(@HeaderParam("locale") String locale, @QueryParam("idSubsidiary") Short idSubsidiary,
	    User originaluser) {
	
	try {
	    
	    log.log(Level.INFO, "Resetting access for user " + originaluser.getEmail());
	    
	    User user = (User) em.createNamedQuery(User.FIND_USER_BY_EMAIL)
		    .setParameter("email", originaluser.getEmail()).getSingleResult();
	    
	    String temporaryAccess = createTemporaryAccess(user, originaluser.getOriginURL());
	    sendResetNotification(user, temporaryAccess, idSubsidiary);
	    
	    String answer = LocaleResource.getString(locale, "user.reset", user.getEmail());
	    log.log(Level.INFO, answer);
	    
	    String jsonReturn = "{\"email\":\"" + user.getEmail() + "\"}";
	    return Response.status(200).entity(jsonReturn).build();
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale, originaluser.getEmail());
	}
	
    }
    
    @Securable
    @PUT
    @Path("/confirm")
    @Consumes("application/json")
    public Response confirmUser(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    RedeFoodPassword newPass) {
	
	User user = null;
	try {
	    
	    Login login = em.find(Login.class, token);
	    if (login == null)
		throw new Exception("token not found");
	    
	    user = em.find(User.class, login.getIdUser());
	    if (user == null)
		throw new Exception("user not found");
	    
	    log.log(Level.INFO, "Confirming new user " + user.getFirstName());
	    
	    user.setPassword(newPass.getNewpass());
	    user.setEmailActive(true);
	    
	    em.merge(user);
	    em.remove(login);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "user.updated", user.getEmail());
	    log.log(Level.INFO, answer);
	    String jsonReturn = "{\"email\":\"" + user.getEmail() + "\"}";
	    
	    return Response.status(200).entity(jsonReturn).build();
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale);
	}
    }
    
    @Securable
    @GET
    @Path("/{idUser:[0-9][0-9]*}/orders/{idOrder:[0-9][0-9]*}/")
    @Produces("application/json;charset=UTF8")
    public String findUserOrderById(@HeaderParam("locale") String locale, @PathParam("idUser") Integer idUser,
	    @PathParam("idOrder") Integer idOrder) {
	
	try {
	    Orders order = em.find(Orders.class, idOrder);
	    if (order.getUser().getId().intValue() != idUser.intValue())
		throw new Exception("not allowed order user");
	    
	    Hibernate.initialize(order.getAddress().getNeighborhood());
	    Hibernate.initialize(order.getAddress().getCity());
	    Hibernate.initialize(order.getBeverages());
	    for (Meal meal : order.getMeals()) {
		List<MealIngredientTypes> mealIngredientTypes = meal.getMealIngredientTypes();
		for (MealIngredientTypes mit : mealIngredientTypes) {
		    Hibernate.initialize(mit.getIngredientType());
		    for (MealIngredientTypeshasIngredient mithi : mit.getMealIngredientTypeshasIngredient()) {
			Hibernate.initialize(mithi.getIngredient());
		    }
		}
	    }
	    Hibernate.initialize(order.getOrderType());
	    
	    for (OrderPaymentMethod opm : order.getOrderPaymentMethod()) {
		Hibernate.initialize(opm.getPaymentMethod());
	    }
	    
	    return mapper.writeValueAsString(order);
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale).toString();
	}
    }
    
    /**
     * Validates users attributes when they are edited. Thrown exception is
     * caught in the above method.
     * 
     * @param id
     * @param token
     * @param user
     * @return
     * @throws Exception
     */
    private User validateEdit(Integer id, String token, User user, Short idSubsidiary) throws Exception {
	
	User toMerge = em.find(User.class, id);
	
	if (!toMerge.getEmail().equalsIgnoreCase(user.getEmail())) {
	    toMerge.setEmail(user.getEmail());
	    sendNewUserNotification(toMerge, token, idSubsidiary);
	}
	toMerge.setBirthdate(user.getBirthdate());
	toMerge.setBonusPoints(user.getBonusPoints());
	toMerge.setCellphone(user.getCellphone());
	if (CPFValidator.isCPF(user.getCpf())) {
	    toMerge.setCpf(user.getCpf());
	}
	toMerge.setImage(user.getImage());
	toMerge.setFirstName(user.getFirstName());
	toMerge.setLastName(user.getLastName());
	// User pass received is already hashed
	// toMerge.setPassword(user.getPassword());
	toMerge.setPhone(user.getPhone());
	toMerge.setPhoto(user.getPhoto());
	toMerge.setSex(user.getSex());
	return toMerge;
    }
    
    /**
     * Create temporary token and returns a URI so that the system could verify
     * the provided e-mail authenticity
     * 
     * @param newUser
     * @return
     * @throws Exception
     */
    private String createTemporaryAccess(User newUser, String originURL) throws Exception {
	
	Timestamp lastSeen = new Timestamp(new Date().getTime());
	
	String token = RedeFoodUtils.doHash(Integer.toString(newUser.hashCode()) + lastSeen);
	
	Login loginToken = new Login(token, newUser.getId(), lastSeen, RedeFoodConstants.DEFAULT_CLIENT_HOST);
	
	em.persist(loginToken);
	em.flush();
	
	return createTemporaryUriAccess(loginToken, originURL);
    }
    
    /**
     * Create a temporary URI that is going to be sent to the user to verify his
     * e-mail address.
     * 
     * @param loginToken
     * @return
     */
    private String createTemporaryUriAccess(Login loginToken, String originURL) {
	
	String temporaryUri = originURL + "&" + RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR + "="
		+ loginToken.getToken();
	
	return temporaryUri;
    }
    
    /**
     * Send e-mail to user's e-mail address. Used both when a new user register
     * and when a user change his e-mail address. Exception is caught in the
     * above method.
     * 
     * @param toPersist
     * @param temporaryAccess
     * @throws Exception
     */
    private void sendNewUserNotification(User user, String temporaryAccess, Short idSubsidiary) throws Exception {
	Notificator notificator = new NewUserNotificator();
	notificator.send(preparareEmailMessage(user, temporaryAccess, idSubsidiary));
    }
    
    private HashMap<String, String> preparareEmailMessage(User user, String temporaryAccess, Short idSubsidiary) {
	EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
	if (idSubsidiary != null) {
	    RedeFoodMailUtil.prepareSubsidiaryLogoAndFooter(emailData, em.find(Subsidiary.class, idSubsidiary));
	} else {
	    RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
	}
	emailData.put("addressee", user.getEmail());
	emailData.put("userName", user.getFirstName().toUpperCase());
	emailData.put("userPass", user.getPassword());
	emailData.put("urlPass", temporaryAccess);
	return emailData;
    }
    
    private void sendResetNotification(User user, String temporaryAccess, Short idSubsidiary) throws Exception {
	Notificator notificator = new ForgotPassNotificator();
	log.log(Level.INFO, "Sending e-mail to recover pass of the user " + user.getEmail());
	notificator.send(preparareEmailMessage(user, temporaryAccess, idSubsidiary));
    }
    
    /**
     * Validates a new created user, setting a temporary password, active to
     * false and @param(numberOfLogins) to zero
     * 
     * @param user
     * @return
     * @throws Exception
     */
    private User validateCreate(User user) throws Exception {
	
	if (user.getCpf() != null && user.getCpf().length() >= 11)
	    if (!CPFValidator.isCPF(user.getCpf()))
		throw new Exception("invalid cpf");
	
	SecureRandom random = new SecureRandom();
	user.setPassword(new BigInteger(30, random).toString(32));
	user.setEmailActive(false);
	user.setNumberOfLogins(new Integer("0"));
	user.setDateRegistered(new Date());
	user.setNotifications(new Notifications(true, true, true, user));
	return user;
    }
}