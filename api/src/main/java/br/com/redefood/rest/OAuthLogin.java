package br.com.redefood.rest;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.user.NewFacebookUserNotificator;
import br.com.redefood.model.Address;
import br.com.redefood.model.Login;
import br.com.redefood.model.Notifications;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.UserOrigin;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodMailUtil;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

@Stateless
@Path("/login/oauth")
public class OAuthLogin extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @POST
    @Path("/facebook")
    @Produces("application/json;charset=UTF8")
    public Response facebookLogin(@HeaderParam("locale") String locale, @QueryParam("idSubsidiary") Short idSubsidiary,
	    String accessToken) {
	
	br.com.redefood.model.User user = null;
	try {
	    String fbToken = prepareAccessToken(accessToken);
	    FacebookClient facebookClient = new DefaultFacebookClient(fbToken);
	    User facebookUser = facebookClient.fetchObject("me", User.class);
	    
	    try {
		user = (br.com.redefood.model.User) em.createNamedQuery(br.com.redefood.model.User.FIND_USER_BY_EMAIL)
			.setParameter("email", facebookUser.getEmail()).getSingleResult();
	    } catch (Exception e) {
		if (user == null) {
		    user = createFacebookUser(facebookUser, idSubsidiary);
		}
		if (user == null)
		    throw new Exception("insufficient data from facebook");
	    }
	    
	    return doLogin(user, locale);
	    
	} catch (Exception e) {
	    if (e.getMessage().contentEquals("insufficient data from facebook"))
		return eh.loginExceptionHandler(e, locale, "");
	    if (e.getMessage().contains("Facebook"))
		return eh.loginExceptionHandler(e, locale, "");
	    return eh.userExceptionHandler(e, locale, String.valueOf(user.getEmail()));
	}
    }
    
    private Response doLogin(br.com.redefood.model.User user, String locale) {
	Timestamp lastSeen = new Timestamp(new Date().getTime());
	String token = RedeFoodUtils.doHash(Integer.toString(user.hashCode()) + lastSeen);
	Login loginToken = new Login(token, user.getId(), lastSeen, "login from facebook");
	user.setLastLogin(lastSeen);
	user.setNumberOfLogins(user.getNumberOfLogins() + 1);
	String jsonUser = "";
	try {
	    em.persist(loginToken);
	    em.merge(user);
	    em.flush();
	    
	    if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
		for (Address address : user.getAddresses()) {
		    Hibernate.initialize(address.getCity());
		    Hibernate.initialize(address.getNeighborhood());
		}
	    }
	    if (user.getOrders() != null && !user.getOrders().isEmpty()) {
		Hibernate.initialize(user.getOrders());
	    }
	    if (user.getNotifications() != null) {
		Hibernate.initialize(user.getNotifications());
	    }
	    
	    jsonUser = mapper.writeValueAsString(user);
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.authentication");
	    log.log(Level.SEVERE, answer + ". Failed to persist user token or merge user.");
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	
	log.log(Level.INFO, "User " + user.getEmail() + " logged in at " + loginToken.getLastSeen() + " with IP "
		+ loginToken.getIp());
	
	return Response
		.status(200)
		.entity("{\"" + RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR + "\":" + "\"" + token + "\",\"user\":"
			+ jsonUser + "}").build();
    }
    
    private String prepareAccessToken(String accessToken) {
	return accessToken.replace("accessToken", "").replace("\"", "").replace("{", "").replace("}", "")
		.replace(":", "");
    }
    
    private br.com.redefood.model.User createFacebookUser(User facebookUser, Short idSubsidiary) {
	br.com.redefood.model.User user = new br.com.redefood.model.User();
	user.setDateRegistered(new Date());
	user.setEmail(facebookUser.getEmail());
	user.setEmailActive(true);
	user.setFirstName(facebookUser.getFirstName());
	user.setLastName(facebookUser.getLastName());
	user.setNumberOfLogins(0);
	user.setSex(facebookUser.getGender().equalsIgnoreCase("male"));
	user.setNotifications(new Notifications(true, true, true, user));
	user.setUserOrigin(em.find(UserOrigin.class, UserOrigin.FACEBOOK));
	try {
	    SecureRandom random = new SecureRandom();
	    user.setPassword(new BigInteger(30, random).toString(32));
	    
	    em.persist(user);
	    em.flush();
	    
	    sendNewFacebookUserNotification(user, idSubsidiary);
	    user.setPassword(RedeFoodUtils.doHash(user.getPassword()));
	    em.merge(user);
	    em.flush();
	    
	    return user;
	} catch (Exception e) {
	    log.log(Level.SEVERE, "Failed to create Facebook user.");
	    return null;
	}
    }
    
    @POST
    @Path("/google")
    public Response googleLogin() {
	return null;
    }
    
    private void sendNewFacebookUserNotification(br.com.redefood.model.User user, Short idSubsidiary) throws Exception {
	Notificator notificator = new NewFacebookUserNotificator();
	log.log(Level.INFO, "Sending e-mail to user registered through facebook.");
	notificator.send(preparareEmailMessage(user, idSubsidiary));
    }
    
    private HashMap<String, String> preparareEmailMessage(br.com.redefood.model.User user, Short idSubsidiary) {
	EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
	if (idSubsidiary != null) {
	    RedeFoodMailUtil.prepareSubsidiaryLogoAndFooter(emailData, em.find(Subsidiary.class, idSubsidiary));
	} else {
	    RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
	}
	emailData.put("addressee", user.getEmail());
	emailData.put("userName", user.getFirstName().toUpperCase());
	emailData.put("userPass", user.getPassword());
	return emailData;
    }
}
