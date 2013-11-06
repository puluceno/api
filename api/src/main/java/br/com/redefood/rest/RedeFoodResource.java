package br.com.redefood.rest;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.DemoNotificator;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.UserContactNotificator;
import br.com.redefood.model.Faq;
import br.com.redefood.model.PossibleCustomers;
import br.com.redefood.model.Tip;
import br.com.redefood.model.complex.ContactRedeFood;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.model.enumtype.Section;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodMailUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class RedeFoodResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/faq")
    @Produces("application/json;charset=UTF8")
    public String listFaq(@HeaderParam("locale") String locale, @QueryParam("section") Section section) {
	try {
	    
	    List<Faq> faqs = em.createNamedQuery(Faq.FIND_FAQ_BY_SECTION).setParameter("section", section)
		    .getResultList();
	    
	    return mapper.writeValueAsString(faqs);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/tip")
    @Produces("application/json;charset=UTF8")
    public String listTip(@HeaderParam("locale") String locale) {
	try {
	    List<Tip> tips = em.createNamedQuery(Tip.FIND_ALL_TIPS).getResultList();
	    
	    return mapper.writeValueAsString(tips);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @POST
    @Path("/contact")
    public Response sendEmailContact(@HeaderParam("locale") String locale, ContactRedeFood contact) {
	try {
	    Notificator notificator = new UserContactNotificator();
	    log.log(Level.FINE, "Sending contact from user " + contact.getEmail());
	    notificator.send(preparareEmailMessage(null, contact));
	    return Response.status(200).build();
	} catch (Exception e) {
	    return eh.genericExceptionHandlerResponse(e, locale);
	}
    }
    
    @POST
    @Path("/demo")
    public Response registerDemoUser(@HeaderParam("locale") String locale, PossibleCustomers customer) {
	try {
	    em.persist(customer);
	    em.flush();
	    
	    sendDemoNotification(customer);
	    
	    return Response.status(201).build();
	} catch (Exception e) {
	    return eh.genericExceptionHandlerResponse(e, locale);
	}
    }
    
    private void sendDemoNotification(PossibleCustomers customer) throws Exception {
	Notificator notificator = new DemoNotificator();
	log.log(Level.INFO, "Sending Demo e-mail to potential customer.");
	notificator.send(preparareEmailMessage(customer, null));
    }
    
    private HashMap<String, String> preparareEmailMessage(PossibleCustomers customer, ContactRedeFood contact) {
	EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
	RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
	if (customer != null) {
	    emailData.put("addressee", customer.getEmail());
	    emailData.put("demoUrl", RedeFoodConstants.DEFAULT_DEMO_URL);
	    emailData.put("demoLogin", RedeFoodConstants.DEFAULT_DEMO_LOGIN);
	    emailData.put("demoPass", RedeFoodConstants.DEFAULT_DEMO_PASS);
	    emailData.put("userName", customer.getName().toUpperCase());
	}
	if (contact != null) {
	    emailData.put("addressee", RedeFoodConstants.DEFAULT_CONTACT_EMAIL);
	    emailData.put("message", contact.getMessage());
	    emailData.put("userEmail", contact.getEmail());
	    emailData.put("userName", contact.getName());
	}
	
	return emailData;
    }
}