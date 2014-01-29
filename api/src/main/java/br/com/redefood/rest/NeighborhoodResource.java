package br.com.redefood.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.City;
import br.com.redefood.model.Neighborhood;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class produces a RESTful service to read the contents of the Cities
 * table.
 */
@Path("/neighborhood")
@Stateless
public class NeighborhoodResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json;charset=UTF8")
	public String lookupNeighborhood(@HeaderParam("locale") String locale,
			@DefaultValue("") @QueryParam("city") String city, @DefaultValue("") @QueryParam("name") String name,
			@DefaultValue("1") @QueryParam("offset") Integer offset,
			@DefaultValue("20") @QueryParam("limit") Integer limit) {

		List<Neighborhood> neighbors = null;

		if (name.equals("") && city.equals("")) {
			neighbors = em.createNamedQuery(Neighborhood.FIND_ALL_NEIGHBORHOOD).setMaxResults(limit)
					.setFirstResult(offset - 1).getResultList();
		} else if (city.equals("") && !name.equals("")) {
			neighbors = em
					.createNamedQuery(Neighborhood.FIND_NEIGHBORHOOD_BY_NAME)
					.setParameter("name",
							RedeFoodConstants.SQL_LIKE_WILDCARD + name + RedeFoodConstants.SQL_LIKE_WILDCARD)
							.setMaxResults(limit).setFirstResult(offset - 1).getResultList();
		} else if (name.equals("") && !city.equals("")) {
			neighbors = em
					.createNamedQuery(Neighborhood.FIND_NEIGHBORHOOD_BY_CITY_NAME)
					.setParameter("city",
							RedeFoodConstants.SQL_LIKE_WILDCARD + city + RedeFoodConstants.SQL_LIKE_WILDCARD)
							.setMaxResults(limit).setFirstResult(offset - 1).getResultList();
		} else if (!name.equals("") && !city.equals("")) {
			neighbors = em
					.createNamedQuery(Neighborhood.FIND_NEIGHBORHOOD_BY_NAME_AND_CITY)
					.setParameter("name",
							RedeFoodConstants.SQL_LIKE_WILDCARD + name + RedeFoodConstants.SQL_LIKE_WILDCARD)
							.setParameter("city",
									RedeFoodConstants.SQL_LIKE_WILDCARD + city + RedeFoodConstants.SQL_LIKE_WILDCARD)
									.setMaxResults(limit).setFirstResult(offset - 1).getResultList();
		}

		try {
			return mapper.writeValueAsString(neighbors);

		} catch (Exception e) {
			return eh.neighborhoodExceptions(e, locale).toString();
		}
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String lookupNeighborhoodById(@HeaderParam("locale") String locale, @PathParam("id") Short id) {

		try {

			return mapper.writeValueAsString(em.find(Neighborhood.class, id));

		} catch (Exception e) {
			return eh.neighborhoodExceptions(e, locale).toString();
		}
	}

	@POST
	@Path("/new/{idCity:[0-9][0-9]")
	@Consumes("application/json")
	public Response addNeighborhood(@HeaderParam("locale") String locale, @PathParam("idCity") Short idCity,
			Neighborhood neighborhood) {

		log.log(Level.INFO, "Registering " + neighborhood.getName());
		City city = null;

		try {
			// validate the city
			city = em.find(City.class, idCity);
			if (city == null)
				throw new Exception("invalid city");

			em.persist(neighborhood);
			String answer = LocaleResource.getProperty(locale).getProperty("neighborhood.added");
			log.log(Level.INFO, answer);
			return Response.status(201).entity(answer).build();

		} catch (Exception e) {
			return eh.neighborhoodExceptions(e, locale);
		}
	}

}