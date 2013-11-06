package br.com.redefood.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import br.com.redefood.model.Parameter;

@Stateless
@Path("")
public class ParameterResource {
	@Inject
	private EntityManager em;

	@GET
	@Path("/printers")
	@Produces("application/json;charset=UTF8")
	public String findPrinterOptions() {
		return em.find(Parameter.class, "printers").getValue();
	}

}
