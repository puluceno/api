package br.com.redefood.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;

import br.com.redefood.annotations.Owner;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Module;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryModule;
import br.com.redefood.model.enumtype.ModuleType;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class ModuleResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/module")
    @Produces("application/json;charset=UTF8")
    public String findModules(@HeaderParam("locale") String locale, @QueryParam("moduletype") String moduleType) {
	
	try {
	    List<Module> modules = null;
	    if (moduleType == null) {
		modules = em.createNamedQuery(Module.FIND_ALL_MODULE).getResultList();
	    } else {
		modules = em.createNamedQuery(Module.FIND_ALL_BY_MODULE_TYPE)
			.setParameter("moduleType", ModuleType.valueOf(moduleType.toUpperCase())).getResultList();
	    }
	    
	    return mapper.writeValueAsString(modules);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @SuppressWarnings("unchecked")
    @Owner
    @GET
    @Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/module")
    @Produces("application/json;charset=UTF8")
    public String listModule(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("moduletype") String moduleType) {
	
	try {
	    List<SubsidiaryModule> sm = null;
	    if (moduleType == null) {
		sm = em.createNamedQuery(SubsidiaryModule.FIND_ACTIVE_BY_SUBSIDIARY)
			.setParameter("idSubsidiary", idSubsidiary).getResultList();
	    } else {
		sm = em.createNamedQuery(SubsidiaryModule.FIND_ACTIVE_BY_SUBSIDIARY_AND_MODULE_TYPE)
			.setParameter("moduleType", ModuleType.valueOf(moduleType.toUpperCase()))
			.setParameter("idSubsidiary", idSubsidiary).getResultList();
	    }
	    
	    for (SubsidiaryModule subsidiaryModule : sm) {
		Hibernate.initialize(subsidiaryModule.getModule());
	    }
	    
	    return mapper.writeValueAsString(sm);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @SuppressWarnings("unchecked")
    @Owner
    @GET
    @Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/module/available")
    @Produces("application/json;charset=UTF8")
    public String listAvailableModule(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("moduletype") String moduleType) {
	
	try {
	    List<Module> modules = null;
	    if (moduleType == null) {
		modules = em.createNamedQuery(Module.FIND_AVAILABLE_MODULE_BY_SUBSIDIARY)
			.setParameter("idSubsidiary", idSubsidiary).getResultList();
	    } else {
		modules = em.createNamedQuery(Module.FIND_AVAILABLE_MODULE_BY_SUBSIDIARY_AND_MODULE_TYPE)
			.setParameter("idSubsidiary", idSubsidiary)
			.setParameter("moduleType", ModuleType.valueOf(moduleType.toUpperCase())).getResultList();
	    }
	    
	    return mapper.writeValueAsString(modules);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @Owner
    @PUT
    @Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/module/{idModule:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response addModule(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idModule") Short idModule) {
	
	log.log(Level.INFO, "Adding Module " + idModule + " for Subsidiary " + idSubsidiary);
	
	try {
	    Module module = em.find(Module.class, idModule);
	    Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
	    
	    if (subsidiary.getOrderTypes() == null) {
		subsidiary.setOrderTypes(new ArrayList<OrderType>());
	    }
	    
	    if (idModule.intValue() == 1) {
		// Add order types relative to Local Module
		subsidiary.getOrderTypes().add(em.find(OrderType.class, OrderType.LOCAL));
		subsidiary.getOrderTypes().add(em.find(OrderType.class, OrderType.LOCAL_TO_GO));
	    } else {
		OrderType orderType = em.find(OrderType.class, OrderType.DELIVERY_ONLINE);
		if (!subsidiary.getOrderTypes().contains(orderType)) {
		    subsidiary.getOrderTypes().add(orderType);
		}
	    }
	    
	    SubsidiaryModule subModule = new SubsidiaryModule(new Date(), null, subsidiary, module, true);
	    subModule.setCharged(false);
	    subModule.setDeactivate(false);
	    if (subsidiary.getSubsidiaryModules() == null) {
		subsidiary.setSubsidiaryModules(new ArrayList<SubsidiaryModule>());
	    }
	    subsidiary.getSubsidiaryModules().add(subModule);
	    
	    for (SubsidiaryModule subsidiaryModule : subsidiary.getSubsidiaryModules())
		if (idModule.intValue() == 3 && subsidiaryModule.getModule().getId().intValue() == 2) {
		    log.log(Level.INFO, "Adding Module Site, so Module Online is not necessary anymore. Removing...");
		    subsidiaryModule.setActive(false);
		    em.merge(subsidiaryModule);
		}
	    
	    em.merge(subsidiary);
	    em.flush();
	    
	    log.log(Level.INFO, "Module " + module.getName() + " added for Subsidiary " + idSubsidiary);
	    
	    String jsonReturn = mapper.writeValueAsString(subModule);
	    
	    return Response.status(200).entity(jsonReturn).build();
	} catch (Exception e) {
	    return eh.genericExceptionHandlerResponse(e, locale);
	}
    }
    
    @Owner
    @DELETE
    @Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/module/{idModule:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response removeModule(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idModule") Short idSubsidiaryModule) {
	
	log.log(Level.INFO, "Deactivating module configurations for Module " + idSubsidiaryModule + " for Subsidiary "
		+ idSubsidiary);
	try {
	    SubsidiaryModule subsidiaryModule = em.find(SubsidiaryModule.class, idSubsidiaryModule);
	    subsidiaryModule.setDeactivate(true);
	    subsidiaryModule.setRequestDeactivateDate(Calendar.getInstance().getTime());
	    em.merge(subsidiaryModule);
	    em.flush();
	    
	    log.log(Level.INFO, "Module " + subsidiaryModule.getModule().getName() + " deactivated from Subsidiary "
		    + idSubsidiary);
	    
	    return Response.status(200).build();
	} catch (Exception e) {
	    return eh.genericExceptionHandlerResponse(e, locale);
	}
    }
}