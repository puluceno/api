package br.com.redefood.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.enumtype.OrderStatus;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("/order/status")
public class OrderStatusResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private RedeFoodExceptionHandler eh;

	@GET
	@Produces("application/json;charset=UTF8")
	public String findOrderStatus(@HeaderParam("locale") String locale) {

		try {
			Map<String, String> status = new HashMap<String, String>();

			for (OrderStatus oStatus : OrderStatus.values()) {
				status.put(oStatus.name(), oStatus.toString(locale));
			}

			return mapper.writeValueAsString(status);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

}
