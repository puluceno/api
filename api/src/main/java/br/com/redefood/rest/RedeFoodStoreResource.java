package br.com.redefood.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Template;
import br.com.redefood.model.Theme;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("/store")
public class RedeFoodStoreResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@SuppressWarnings("unchecked")
	@GET
	@Path("/templates")
	@Produces("application/json;charset=UTF8")
	public String listTemplates(@HeaderParam("locale") String locale) {
		try {

			List<Template> templates = em.createNamedQuery(Template.FIND_ALL_TEMPLATES).getResultList();

			return mapper.writeValueAsString(templates);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/templates/{idTemplate:[0-9][0-9]*}/themes")
	@Produces("application/json;charset=UTF8")
	public String findThemesByTemplate(@HeaderParam("locale") String locale, @PathParam("idTemplate") Short idTemplate) {
		try {
			List<Theme> themes = em.createNamedQuery(Theme.FIND_THEMES_BY_TEMPLATE)
					.setParameter("idTemplate", idTemplate).getResultList();

			return mapper.writeValueAsString(themes);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	// @RedeFoodAdmin commented due to test purpose
	@POST
	@Path("/templates")
	public Response createTemplate(@HeaderParam("locale") String locale, Template template) {
		try {
			em.persist(template);
			String answer = LocaleResource.getProperty(locale).getProperty("redefood.template.created");
			log.log(Level.FINE, answer);
			return Response.status(201).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @RedeFoodAdmin commented due to test purpose
	@PUT
	@Path("/templates/{idTemplate:[0-9][0-9]*}")
	public Response editTemplate(@HeaderParam("locale") String locale, @PathParam("idTemplate") Short idTemplate,
			Template template) {
		try {
			Template find = em.find(Template.class, idTemplate);
			find.setDescription(template.getDescription());
			find.setDemo(template.getDemo());
			find.setMobile(template.getMobile());
			find.setName(template.getName());

			String answer = LocaleResource.getProperty(locale).getProperty("redefood.template.updated");
			log.log(Level.FINE, answer);
			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @RedeFoodAdmin commented due to test purpose
	@POST
	@Path("/templates/{idTemplate:[0-9][0-9]*}/themes")
	public Response createTheme(@HeaderParam("locale") String locale, @PathParam("idTemplate") Short idTemplate,
			Theme theme) {
		try {
			Template template = em.find(Template.class, idTemplate);
			if (!template.getThemes().contains(theme)) {
				theme.setTemplate(template);
				template.getThemes().add(theme);
			} else
				return Response.status(403).build();

			String answer = LocaleResource.getProperty(locale).getProperty("redefood.theme.created");
			log.log(Level.FINE, answer);
			return Response.status(201).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @RedeFoodAdmin commented due to test purpose
	@PUT
	@Path("/themes/{idTheme:[0-9][0-9]*}")
	public Response editTheme(@HeaderParam("locale") String locale, @PathParam("idTheme") Short idTheme, Theme theme) {
		try {
			Theme find = em.find(Theme.class, idTheme);
			find.setDescription(theme.getDescription());
			find.setName(theme.getName());

			String answer = LocaleResource.getProperty(locale).getProperty("redefood.theme.updated");
			log.log(Level.FINE, answer);
			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

}