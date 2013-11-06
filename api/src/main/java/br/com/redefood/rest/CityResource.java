package br.com.redefood.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.City;
import br.com.redefood.model.Neighborhood;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class produces a RESTful service to read the contents of the Cities
 * table.
 */
@Path("/city")
@Stateless
public class CityResource extends HibernateMapper {
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
	public String listByName(@HeaderParam("locale") String locale, @QueryParam("name") String name,
			@DefaultValue("1") @QueryParam("offset") Integer offset,
			@DefaultValue("20") @QueryParam("limit") Integer limit) {

		List<City> cities = null;

		if (name == null || name.isEmpty()) {
			cities = em.createNamedQuery(City.FIND_ALL_CITY).setMaxResults(limit).setFirstResult(offset - 1).getResultList();
		} else {
			cities = em.createNamedQuery(City.FIND_CITY_BY_NAME).setParameter("name", "%" + name + "%").setMaxResults(limit)
					.setFirstResult(offset - 1).getResultList();
		}

		try {
			return mapper.writeValueAsString(cities);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String lookupCityById(@HeaderParam("locale") String locale, @PathParam("id") Short id,
			@QueryParam("neighborhood") Short idNeighborhood) {

		try {
			City city = em.find(City.class, id);
			if (city == null)
				throw new Exception("invalid city");
			Neighborhood n = null;
			if (idNeighborhood != null) {
				n = em.find(Neighborhood.class, idNeighborhood);
			}
			Map<String, Object> mapCity = new HashMap<String, Object>();
			mapCity.put("name", city.getName());
			mapCity.put("image", city.getImage());
			mapCity.put("id", city.getId());
			mapCity.put("neighborhoods", n);

			return mapper.writeValueAsString(mapCity);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@Securable
	@POST
	@Consumes("application/json")
	public Response addCity(@HeaderParam("locale") String locale, City city) {

		try {
			em.persist(city);
			em.flush();
			String answer = LocaleResource.getString(locale, "city.added", city.getName());
			log.log(Level.INFO, answer);
			return Response.status(201).entity(answer).build();

		} catch (Exception e) {
			return eh.cityExceptionHandler(e, locale);
		}
	}

}