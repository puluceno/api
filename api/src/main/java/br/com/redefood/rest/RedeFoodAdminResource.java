package br.com.redefood.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import br.com.redefood.annotations.RedeFoodAdmin;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.restaurant.NewRestaurantRefusedNotificator;
import br.com.redefood.mail.notificator.restaurant.NewRestaurantSuccessNotificator;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Parameter;
import br.com.redefood.model.Restaurant;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodMailUtil;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("/admin")
public class RedeFoodAdminResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    
    @SuppressWarnings("unchecked")
    @RedeFoodAdmin
    @GET
    @Path("/pending")
    @Produces("application/json;charset=UTF8")
    public String findPendingRestaurants(@HeaderParam("token") String token, @HeaderParam("locale") String locale) {
	
	try {
	    List<Restaurant> pendingRestaurants = em.createNamedQuery(Restaurant.FIND_PENDING_RESTAURANTS)
		    .getResultList();
	    
	    return mapper.writeValueAsString(pendingRestaurants);
	} catch (Exception e) {
	    return "deu pau";
	}
    }
    
    @RedeFoodAdmin
    @POST
    @Path("/pending/{idRestaurant:[0-9][0-9]*}/accept")
    public Response acceptPendingRestaurant(@HeaderParam("locale") String locale,
	    @PathParam("idRestaurant") Short idRestaurant) {
	Subsidiary subsidiary = null;
	try {
	    
	    subsidiary = (Subsidiary) em.createNamedQuery(Subsidiary.FIND_PENDING_BY_RESTAURANT)
		    .setParameter("idRestaurant", idRestaurant).getSingleResult();
	    
	    subsidiary.setActive(true);
	    subsidiary.setDenied(false);
	    em.merge(subsidiary);
	    
	    Restaurant restaurant = em.find(Restaurant.class, idRestaurant);
	    Employee owner = subsidiary.getEmployees().get(0);
	    
	    sendSuccessRestaurantNotification(owner, restaurant, subsidiary);
	    
	    owner.setPassword(RedeFoodUtils.doHash(owner.getPassword()));
	    
	    restaurant.setAnswerDate(new Date());
	    em.merge(restaurant);
	    em.merge(owner);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.accepted", subsidiary.getRestaurant()
		    .getName());
	    log.log(Level.INFO, answer);
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.accept",
		    subsidiary != null ? subsidiary.getRestaurant().getName() : "null");
	    log.log(Level.SEVERE, answer);
	    return Response.status(500).entity(answer).build();
	}
    }
    
    private void sendSuccessRestaurantNotification(Employee owner, Restaurant restaurant, Subsidiary subsidiary)
	    throws Exception {
	
	Notificator notificator = new NewRestaurantSuccessNotificator();
	
	log.log(Level.INFO, "Sending success e-mail to " + owner.getFirstName() + ", owner of " + restaurant.getName()
		+ " with cnpj " + subsidiary.getCnpj());
	
	notificator.send(prepareEmailData(owner, restaurant, null));
    }
    
    @RedeFoodAdmin
    @POST
    @Path("/pending/{idRestaurant:[0-9][0-9]*}/deny")
    public Response refusePendingRestaurant(String token, @HeaderParam("locale") String locale,
	    @PathParam("idRestaurant") Short idRestaurant, @QueryParam("reason") String reason) {
	
	Subsidiary subsidiary = null;
	
	try {
	    
	    Restaurant restaurant = em.find(Restaurant.class, idRestaurant);
	    restaurant.setAnswerDate(new Date());
	    subsidiary = restaurant.getSubsidiaries().get(0);
	    Employee owner = subsidiary.getEmployees().get(0);
	    
	    subsidiary.setDenied(true);
	    em.merge(subsidiary);
	    
	    sendRefusedRestaurantNotification(owner, restaurant, subsidiary, reason);
	    
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.refused",
		    subsidiary.getRestaurant().getName(), reason == null ? "" : reason.toLowerCase());
	    log.log(Level.INFO, answer);
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.refuse",
		    subsidiary != null ? subsidiary.getRestaurant().getName() : "null");
	    log.log(Level.SEVERE, answer);
	    return Response.status(500).entity(answer).build();
	}
    }
    
    private void sendRefusedRestaurantNotification(Employee owner, Restaurant restaurant, Subsidiary subsidiary,
	    String reason) throws Exception {
	
	Notificator notificator = new NewRestaurantRefusedNotificator();
	
	log.log(Level.INFO, "Sending refused e-mail to " + owner.getFirstName() + ", owner of " + restaurant.getName()
		+ " with cnpj " + subsidiary.getCnpj());
	
	notificator.send(prepareEmailData(owner, restaurant, reason));
    }
    
    private EmailDataDTO<String, String> prepareEmailData(Employee owner, Restaurant restaurant, String reason) {
	EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
	RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
	
	emailData.put("userName", owner.getFirstName().toUpperCase());
	emailData.put("restaurantName", restaurant.getName().toUpperCase());
	emailData.put("reason", reason);
	emailData.put("password", owner.getPassword());
	emailData.put("addressee", owner.getEmail());
	
	return emailData;
    }
    
    @RedeFoodAdmin
    @DELETE
    @Path("/pending/{idRestaurant:[0-9][0-9]*}")
    public Response deletePendingRestaurant(String token, @HeaderParam("locale") String locale,
	    @PathParam("idRestaurant") Short idRestaurant) {
	
	Subsidiary subsidiary = null;
	
	try {
	    
	    Restaurant restaurant = em.find(Restaurant.class, idRestaurant);
	    subsidiary = restaurant.getSubsidiaries().get(0);
	    Employee owner = subsidiary.getEmployees().get(0);
	    
	    subsidiary.getEmployees().clear();
	    restaurant.getSubsidiaries().clear();
	    em.remove(owner);
	    em.remove(subsidiary);
	    em.remove(restaurant);
	    em.flush();
	    
	    String answer = LocaleResource
		    .getString(locale, "restaurant.removed", subsidiary.getRestaurant().getName());
	    log.log(Level.INFO, answer);
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.remove",
		    subsidiary != null ? subsidiary.getRestaurant().getName() : "null");
	    log.log(Level.SEVERE, answer);
	    return Response.status(500).entity(answer).build();
	}
    }
    
    @GET
    @Path("/warning")
    @Produces("application/json;charset=UTF8")
    public HashMap<String, String> messageToSubsidiaries() {
	Parameter parameter = em.find(Parameter.class, "subsidiaryMessage");
	HashMap<String, String> message = new HashMap<String, String>();
	message.put("message", parameter.getValue());
	return message;
    }
    
    @RedeFoodAdmin
    @PUT
    @Path("/warning")
    public Response editMessageToSubsidiaries(Object message) {
	try {
	    Parameter parameter = em.find(Parameter.class, "subsidiaryMessage");
	    parameter.setValue(message.toString().split("=")[1].replace("}", ""));
	    em.merge(parameter);
	    em.flush();
	    return Response.status(200).build();
	} catch (Exception e) {
	    log.log(Level.SEVERE, e.getMessage());
	    return Response.status(500).entity("Falha ao traduzir os dados.").build();
	}
    }
}
