package br.com.redefood.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.user.EmployeeForgotPassNotificator;
import br.com.redefood.model.Address;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Login;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealIngredientTypes;
import br.com.redefood.model.MealIngredientTypeshasIngredient;
import br.com.redefood.model.Orders;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.User;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.model.complex.RedeFoodPassword;
import br.com.redefood.service.FileUploadService;
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
 * This class produces a RESTful service to read the contents of the Cities
 * table.
 */
@Path("/me")
@Stateless
public class LoginIdentificationResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @Securable
    @GET
    @Produces("application/json;charset=UTF8")
    public String lookupUserByToken(@HeaderParam("token") String token, @HeaderParam("locale") String locale) {
	
	try {
	    Login loggedUser = null;
	    Employee employee = null;
	    
	    try {
		loggedUser = em.find(Login.class, token);
		
	    } catch (Exception e) {
		String answer = LocaleResource.getProperty(locale).getProperty("exception.me.token");
		log.log(Level.WARNING, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
	    }
	    
	    try {
		employee = em.find(Employee.class, (short) loggedUser.getIdUser());
		
	    } catch (Exception e) {
		String answer = LocaleResource.getString(locale, "exception.employee.bad.id", loggedUser.getIdUser());
		log.log(Level.WARNING, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswerString(400, answer);
	    }
	    
	    if (employee.getAddress() != null) {
		Hibernate.initialize(employee.getAddress().getCity());
		Hibernate.initialize(employee.getAddress().getNeighborhood());
	    }
	    
	    Hibernate.initialize(employee.getProfile());
	    
	    return mapper.writeValueAsString(employee);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @Securable
    @GET
    @Path("/employee")
    @Produces("application/json;charset=UTF8")
    public String findEmployeeByToken(@HeaderParam("token") String token, @HeaderParam("locale") String locale) {
	
	try {
	    if (token == null)
		throw new Exception("token null");
	    
	    Login loggedUser = em.find(Login.class, token);
	    Employee employee = em.find(Employee.class, (short) loggedUser.getIdUser());
	    
	    if (employee.getAddress() != null) {
		Hibernate.initialize(employee.getAddress().getCity());
		Hibernate.initialize(employee.getAddress().getNeighborhood());
	    }
	    
	    Hibernate.initialize(employee.getProfile());
	    
	    return mapper.writeValueAsString(employee);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
	
    }
    
    @Securable
    @PUT
    @Path("/employee")
    @Produces("application/json;charset=UTF8")
    @Consumes("application/json")
    public Response editEmployeeByToken(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    Employee employee) {
	
	try {
	    
	    if (employee == null)
		throw new Exception("employee null");
	    
	    Employee toMerge = validateEdit(token, employee);
	    
	    em.merge(toMerge);
	    em.flush();
	    
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    return eh.employeeExceptions(e, locale);
	}
	
    }
    
    @POST
    @Path("/employee/reset")
    @Produces("application/json;charset=UTF8")
    @Consumes("application/json")
    public Response resetEmployeePass(@HeaderParam("locale") String locale,
	    @QueryParam("idSubsidiary") Short idSubsidiary, @QueryParam("originUrl") String originUrl,
	    HashMap<String, String> cpf) {
	
	try {
	    log.log(Level.INFO, "Resetting access for employee with cpf " + cpf.get("cpf"));
	    
	    Employee employee = (Employee) em.createNamedQuery(Employee.FIND_BY_CPF)
		    .setParameter("cpf", cpf.get("cpf")).getSingleResult();
	    
	    String addressee = "";
	    
	    if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
		addressee = em.find(Subsidiary.class, idSubsidiary).getEmail();
	    } else {
		addressee = employee.getEmail();
	    }
	    
	    EmailDataDTO<String, String> emailData = prepareMailData(idSubsidiary, employee, addressee, originUrl);
	    
	    sendResetNotification(emailData);
	    
	    String answer = LocaleResource.getString(locale, "user.reset", addressee);
	    log.log(Level.INFO, answer);
	    
	    String jsonReturn = "{\"cpf\":\"" + cpf + "\"}";
	    return Response.status(200).entity(jsonReturn).build();
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerResponse(e, locale);
	}
    }
    
    @Securable
    @POST
    @Path("/employee/photo")
    @Consumes("multipart/form-data")
    public Response addEmployeePhoto(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    MultipartFormDataInput photo) {
	
	try {
	    Login loggedUser = em.find(Login.class, token);
	    Employee employee = em.find(Employee.class, (short) loggedUser.getIdUser());
	    
	    if (employee == null)
		throw new Exception("employee null");
	    
	    if (employee.getPhoto() != null && !employee.getPhoto().contains("default")) {
		FileUploadService.deleteOldFile(employee.getPhoto());
	    }
	    
	    String uploadFile = FileUploadService.uploadFile(employee.getClass().getSimpleName().toLowerCase(),
		    employee.getId().toString(), photo);
	    if (uploadFile.contains("error"))
		throw new Exception("file error");
	    
	    employee.setPhoto(uploadFile);
	    
	    em.merge(employee);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "employee.photo", employee.getFirstName());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswerWithoutSuccess(200, uploadFile);
	    
	} catch (Exception e) {
	    return eh.employeeExceptions(e, locale);
	}
	
    }
    
    /**
     * Validates users attributes when they are edited. Thrown exception is
     * caught in the above method.
     * 
     * @param idEmployee
     * @param token
     * @param employee
     * @return
     * @throws Exception
     */
    private Employee validateEdit(String token, Employee employee) throws Exception {
	
	Login loggedUser = em.find(Login.class, token);
	Employee toMerge = em.find(Employee.class, (short) loggedUser.getIdUser());
	
	toMerge.setFirstName(employee.getFirstName());
	toMerge.setLastName(employee.getLastName());
	toMerge.setEmail(employee.getEmail());
	toMerge.setPhone(employee.getPhone());
	toMerge.setCellphone(employee.getCellphone());
	toMerge.setRg(employee.getRg());
	toMerge.setSex(employee.isSex());
	if (employee.getPassword() != null && !employee.getPassword().equals("")) {
	    toMerge.setPassword(employee.getPassword());
	}
	if (employee.getAddress() != null && employee.getAddress().getStreet() != null
		&& !employee.getAddress().getStreet().isEmpty()) {
	    toMerge.setAddress(employee.getAddress());
	} else {
	    toMerge.setAddress(null);
	}
	
	if (employee.getProfile() != null) {
	    toMerge.setProfile(employee.getProfile());
	}
	toMerge.setActive(employee.getActive());
	
	// CPF can't be changed. It is disabled at the interfaces and verified
	// when inserted. toMerge.setCpf(employee.getCpf());
	
	return toMerge;
    }
    
    @Securable
    @PUT
    @Path("/user")
    @Produces("application/json;charset=UTF8")
    @Consumes("application/json")
    public Response editUserByToken(@HeaderParam("token") String token, @HeaderParam("locale") String locale, User user) {
	
	try {
	    if (user == null)
		throw new Exception("user null");
	    
	    User toMerge = validateEdit(token, user);
	    em.merge(toMerge);
	    em.flush();
	    
	    for (Address address : toMerge.getAddresses()) {
		Hibernate.initialize(address.getCity());
		Hibernate.initialize(address.getNeighborhood());
	    }
	    for (Orders order : toMerge.getOrders()) {
		for (Meal meal : order.getMeals()) {
		    List<MealIngredientTypes> mealIngredientTypes = meal.getMealIngredientTypes();
		    for (MealIngredientTypes mit : mealIngredientTypes) {
			Hibernate.initialize(mit.getIngredientType());
			for (MealIngredientTypeshasIngredient mithi : mit.getMealIngredientTypeshasIngredient()) {
			    Hibernate.initialize(mithi.getIngredient());
			}
		    }
		}
		Hibernate.initialize(order.getBeverages());
	    }
	    Hibernate.initialize(user.getNotifications());
	    
	    return Response.status(200).entity(mapper.writeValueAsString(toMerge)).build();
	    
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale);
	}
    }
    
    @Securable
    @POST
    @Path("/user/photo")
    @Consumes("multipart/form-data")
    public Response addUserPhoto(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    MultipartFormDataInput photo) {
	
	try {
	    Login loggedUser = em.find(Login.class, token);
	    User user = em.find(User.class, loggedUser.getIdUser());
	    
	    if (user == null) {
		String answer = LocaleResource.getProperty(locale).getProperty("exception.user.null");
		log.log(Level.INFO, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	    }
	    
	    if (user.getPhoto() != null && !user.getPhoto().contains("default")) {
		FileUploadService.deleteOldFile(user.getPhoto());
	    }
	    
	    String uploadFile = FileUploadService.uploadFile(user.getClass().getSimpleName().toLowerCase(), user
		    .getId().toString(), photo);
	    if (uploadFile.contains("error"))
		throw new Exception("file error");
	    
	    user.setPhoto(uploadFile);
	    
	    em.merge(user);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "user.photo", user.getFirstName());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswerWithoutSuccess(200, uploadFile);
	    
	} catch (Exception e) {
	    return eh.fileExceptionHandler(e, locale);
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
    private User validateEdit(String token, User user) throws Exception {
	
	if (user.getCpf() != null && !CPFValidator.isCPF(user.getCpf()))
	    throw new Exception("invalid cpf");
	
	Login loggedUser = em.find(Login.class, token);
	User toMerge = em.find(User.class, loggedUser.getIdUser());
	
	toMerge.setCpf(user.getCpf());
	
	if (user.getBirthdate() != null) {
	    toMerge.setBirthdate(user.getBirthdate());
	}
	if (user.getBonusPoints() != null) {
	    toMerge.setBonusPoints(user.getBonusPoints());
	}
	if (user.getCellphone() != null) {
	    toMerge.setCellphone(user.getCellphone());
	}
	
	if (user.getImage() != null) {
	    toMerge.setImage(user.getImage());
	}
	if (user.getFirstName() != null) {
	    toMerge.setFirstName(user.getFirstName());
	}
	if (user.getLastName() != null) {
	    toMerge.setLastName(user.getLastName());
	}
	// User pass received is already hashed
	// toMerge.setPassword(user.getPassword());
	if (user.getPhone() != null) {
	    toMerge.setPhone(user.getPhone());
	}
	if (user.getSex() != null) {
	    toMerge.setSex(user.getSex());
	}
	// Notifications
	if (user.getNotifications() != null) {
	    toMerge.getNotifications().setEmailAction(user.getNotifications().getEmailAction());
	    toMerge.getNotifications().setEmailNewsletter(user.getNotifications().getEmailNewsletter());
	    toMerge.getNotifications().setSmsNewsletter(user.getNotifications().getSmsNewsletter());
	}
	if (user.getAddresses() != null) {
	    if (!user.getAddresses().isEmpty()) {
		for (Address address : toMerge.getAddresses()) {
		    address.setUser(null);
		}
	    } else {
		for (Address address : toMerge.getAddresses()) {
		    address.setUser(null);
		}
	    }
	    for (Address address : user.getAddresses()) {
		address.setUser(user);
		toMerge.getAddresses().add(address);
	    }
	}
	return toMerge;
    }
    
    @Securable
    @PUT
    @Path("/user/password")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF8")
    public Response changeUserPassword(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    RedeFoodPassword updatePass) {
	
	try {
	    Login login = em.find(Login.class, token);
	    
	    if (login.isExpired())
		throw new Exception("token expired");
	    
	    User user = em.find(User.class, login.getIdUser());
	    
	    if (user == null)
		throw new Exception("user not found");
	    
	    if (user.getPassword().equals(updatePass.getCurrent()) || updatePass.getCurrent() == null) {
		
		user.setPassword(updatePass.getNewpass());
		
		if (updatePass.getCurrent() == null) {
		    login.setExpired(true);
		}
		
		em.merge(user);
		em.flush();
		
		String jsonUser = mapper.writeValueAsString(user);
		return Response.status(200).entity(jsonUser).build();
		
	    } else
		throw new Exception("password invalid");
	} catch (Exception e) {
	    return eh.userExceptionHandler(e, locale);
	}
    }
    
    @Securable
    @PUT
    @Path("/employee/password")
    @Consumes("application/json")
    @Produces("application/json;charset=UTF8")
    public Response changeEmployeePassword(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    RedeFoodPassword updatePass) {
	
	try {
	    
	    Login login = em.find(Login.class, token);
	    
	    if (login.isExpired())
		throw new Exception("token expired");
	    
	    Employee employee = em.find(Employee.class, (short) login.getIdUser());
	    
	    if (employee == null)
		throw new Exception("employee null");
	    
	    if (employee.getPassword().equals(updatePass.getCurrent()) || updatePass.getCurrent() == null) {
		log.log(Level.INFO, "Setting new password to employee " + employee.getCpf());
		employee.setPassword(updatePass.getNewpass());
		
		if (updatePass.getCurrent() == null) {
		    login.setExpired(true);
		}
		
		em.merge(employee);
		em.flush();
		
		String jsonUser = mapper.writeValueAsString(employee);
		return Response.status(200).entity(jsonUser).build();
	    } else
		throw new Exception("invalid password");
	} catch (Exception e) {
	    return eh.employeeExceptions(e, locale);
	}
    }
    
    @Securable
    @GET
    @Path("/user")
    @Produces("application/json;charset=UTF8")
    public String findUser(@HeaderParam("locale") String locale, @HeaderParam("token") String token) {
	
	try {
	    Login login = em.find(Login.class, token);
	    if (login == null)
		throw new Exception("token null");
	    
	    User user = em.find(User.class, login.getIdUser());
	    if (user == null)
		throw new Exception("user null");
	    
	    for (Address address : user.getAddresses()) {
		Hibernate.initialize(address.getCity());
		Hibernate.initialize(address.getNeighborhood());
	    }
	    Hibernate.initialize(user.getNotifications());
	    
	    return mapper.writeValueAsString(user);
	    
	} catch (Exception e) {
	    return eh.loginExceptionHandler(e, locale, "").getEntity().toString();
	}
    }
    
    @SuppressWarnings("unchecked")
    @Securable
    @GET
    @Path("/user/orders")
    @Produces("application/json;charset=UTF8")
    public String findUserOrders(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @QueryParam("idSubsidiary") Short idSubsidiary) {
	
	if (token == null) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.me.token");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
	}
	
	Login loggedUser = em.find(Login.class, token);
	
	try {
	    String sub = "";
	    if (idSubsidiary != null) {
		sub = "AND o.subsidiary.idSubsidiary = :idSubsidiary";
	    }
	    String query = "SELECT o FROM Orders o WHERE o.user.idUser = :idUser " + sub + " ORDER BY o.orderMade DESC";
	    Query queryOrders = em.createQuery(query).setParameter("idUser", loggedUser.getIdUser());
	    if (idSubsidiary != null) {
		queryOrders.setParameter("idSubsidiary", idSubsidiary);
	    }
	    
	    List<Orders> userOrders = queryOrders.getResultList();
	    
	    for (Orders orders : userOrders) {
		Hibernate.initialize(orders.getMealsOrder());
		Hibernate.initialize(orders.getSubsidiary());
	    }
	    
	    return mapper.writeValueAsString(userOrders);
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    /**
     * Create temporary token and returns a URI so that the system could verify
     * the provided e-mail authenticity
     * 
     * @param employee
     * @return
     * @throws Exception
     */
    private String createTemporaryAccess(Employee employee, String originURL) throws Exception {
	
	Timestamp lastSeen = new Timestamp(new Date().getTime());
	
	String token = RedeFoodUtils.doHash(Integer.toString(employee.hashCode()) + lastSeen);
	
	Login loginToken = new Login(token, employee.getId(), lastSeen, RedeFoodConstants.DEFAULT_CLIENT_HOST);
	
	em.persist(loginToken);
	em.flush();
	
	return createTemporaryUriAccess(loginToken, originURL);
    }
    
    /**
     * Create a temporary URI that is going to be sent to the employee to verify
     * his e-mail address.
     * 
     * @param loginToken
     * @return
     */
    private String createTemporaryUriAccess(Login loginToken, String originURL) {
	
	String temporaryUri = originURL + "&" + RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR + "="
		+ loginToken.getToken();
	
	return temporaryUri;
    }
    
    private void sendResetNotification(HashMap<String, String> emailData) throws Exception {
	Notificator notificator = new EmployeeForgotPassNotificator();
	log.log(Level.INFO, "Sending e-mail to recover pass of the user " + emailData.get("userDataLog"));
	notificator.send(emailData);
    }
    
    /**
     * Method responsible for preparing all data to send email
     * 
     * @param idSubsidiary
     *            idSubsidiary
     * @param employee
     *            employee
     * @param addressee
     *            addressee
     * @param originUrl
     *            Requester origin URL.
     * @return EmailDataDTO emailData
     * @throws Exception
     *             exception
     */
    private EmailDataDTO<String, String> prepareMailData(Short idSubsidiary, Employee employee, String addressee,
	    String originUrl) throws Exception {
	
	EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
	
	if (idSubsidiary != null) {
	    Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
	    RedeFoodMailUtil.prepareSubsidiaryLogoAndFooter(emailData, subsidiary);
	    
	} else {
	    // get redefood data
	    RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
	}
	
	emailData.put("userName", employee.getFirstName());
	emailData.put("urlPass", createTemporaryAccess(employee, originUrl));
	emailData.put("userDataLog", employee.getCpf());
	emailData.put("addressee", addressee);
	return emailData;
    }
}