package br.com.redefood.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Restaurant;
import br.com.redefood.model.complex.SiteDTO;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/site")
@Stateless
public class SiteResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private RedeFoodExceptionHandler eh;

	@GET
	@Path("/{idRestaurant:[0-9][0-9]*}")
	public String findRestaurantSiteData(@HeaderParam("locale") String locale,
			@PathParam("idRestaurant") Short idRestaurant) {
		try {
			SiteDTO dto = (SiteDTO) em.createNamedQuery(Restaurant.FIND_RESTAURANT_SITE_DATA_BY_IDRESTAURANT)
					.setParameter("idRestaurant", idRestaurant).getSingleResult();

			return mapper.writeValueAsString(dto);
		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);

		}
	}
}
