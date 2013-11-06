package br.com.redefood.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;

import br.com.redefood.annotations.Owner;
import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryModule;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class OrderTypeResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order-type")
	@Produces("application/json;charset=UTF8")
	public String findOrderTypeBySubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

		try {
			Hibernate.initialize(subsidiary.getOrderTypes());

			return mapper.writeValueAsString(subsidiary.getOrderTypes());

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@SuppressWarnings("unchecked")
	@Securable
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order-type/available")
	@Produces("application/json;charset=UTF8")
	public String findAvailableOrderTypeBySubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		List<OrderType> orderTypes = em.createNamedQuery(OrderType.FIND_ALL_ORDER_TYPE).getResultList();
		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

		List<OrderType> aux = new ArrayList<OrderType>();

		for (OrderType orderType : orderTypes)
			if (!subsidiary.getOrderTypes().contains(orderType)) {
				aux.add(orderType);
			}

		try {
			return mapper.writeValueAsString(aux);
		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@Owner
	@PUT
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order-type/{idOrderType:[0-9][0-9]*}")
	public Response addOrderTypeToSubsidiary(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idOrderType") Short idOrderType) {

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			OrderType orderType = em.find(OrderType.class, idOrderType);

			if (!validateOrderType(subsidiary, orderType))
				throw new Exception("ordertype without needed module");

			subsidiary.getOrderTypes().add(orderType);

			em.merge(subsidiary);
			em.flush();

			log.log(Level.INFO, "OrderType " + orderType.getName() + " added to Subsidiary " + idSubsidiary);

			return Response.status(201).entity(mapper.writeValueAsString(orderType.getId())).build();
		} catch (Exception e) {

			return eh.orderTypeExceptionHandlerResponse(e, locale);
		}
	}

	private boolean validateOrderType(Subsidiary subsidiary, OrderType orderType) throws Exception {

		for (SubsidiaryModule sm : subsidiary.getSubsidiaryModules()) {

			// TODO acho q n precisa desse initialize
			Hibernate.initialize(sm.getModule());

			if ((orderType.getId().intValue() == 1 || orderType.getId().intValue() == 2 || orderType.getId().intValue() == 6)
					&& (sm.getModule().getId().intValue() == 2 && sm.getActive() == true)
					|| (sm.getModule().getId().intValue() == 3 && sm.getActive() == true))
				return true;
			// throw new Exception("ordertype without online module");
			if ((orderType.getId().intValue() == 4 || orderType.getId().intValue() == 5 || orderType.getId().intValue() == 8)
					&& (sm.getModule().getId().intValue() == 1 && sm.getActive() == true))
				return true;
			// throw new Exception("ordertype without local module");
			if ((orderType.getId().intValue() == 3 || orderType.getId().intValue() == 7)
					&& ((sm.getModule().getId().intValue() == 1 && sm.getActive() == true)
							|| (sm.getModule().getId().intValue() == 2 && sm.getActive() == true) || (sm.getModule()
							.getId().intValue() == 3 && sm.getActive() == true)))
				return true;
			// throw new Exception("ordertype without local module");
		}
		return false;
	}

	@Owner
	@DELETE
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order-type/{idOrderType:[0-9][0-9]*}")
	public Response removeOrderTypeToSubsidiary(@HeaderParam("token") String token,
			@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idOrderType") Short idOrderType) {

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			OrderType orderType = em.find(OrderType.class, idOrderType);

			subsidiary.getOrderTypes().remove(orderType);

			em.merge(subsidiary);
			em.flush();

			log.log(Level.INFO, "OrderType " + orderType.getName() + " removed from Subsidiary " + idSubsidiary);

			return Response.status(200).entity(mapper.writeValueAsString(orderType.getId())).build();
		} catch (Exception e) {

			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

}
