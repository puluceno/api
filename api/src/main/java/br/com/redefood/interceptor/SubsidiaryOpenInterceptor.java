package br.com.redefood.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.RedirectPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.rest.SubsidiaryResource;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@ServerInterceptor
@RedirectPrecedence
public class SubsidiaryOpenInterceptor implements PostProcessInterceptor, AcceptedByMethod {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();

	@Inject
	private RedeFoodExceptionHandler eh;
	@Inject
	private SubsidiaryResource sr;
	@Context
	private HttpServletRequest req;
	@Context
	private UriInfo uri;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean accept(Class clazz, Method method) {
		final Annotation[][] paramAnnotations = method.getParameterAnnotations();
		for (Annotation[] paramAnnotation : paramAnnotations) {
			for (Annotation a : paramAnnotation) {
				if (a instanceof PathParam && ((PathParam) a).value().equals("idSubsidiary"))
					return true;
				if (a instanceof QueryParam && ((QueryParam) a).value().equals("idSubsidiary"))
					return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postProcess(ServerResponse response) {
		try {
			if (response.getGenericType().toString().equals(String.class.toString())) {
				String entity = (String) response.getEntity();
				HashMap<String, Object> value = mapper.readValue(entity, HashMap.class);

				final Annotation[][] paramAnnotations = response.getResourceMethod().getParameterAnnotations();
				for (Annotation[] paramAnnotation : paramAnnotations) {
					for (Annotation a : paramAnnotation) {
						if (a instanceof PathParam && ((PathParam) a).value().equals("idSubsidiary")
								|| a instanceof QueryParam && ((QueryParam) a).value().equals("idSubsidiary")) {
							Short idSubsidiary = new Short(uri.getPathParameters().getFirst("idSubsidiary"));
							value.put("subsidiaryOpen", sr.isSubsidiaryOpen("", idSubsidiary));
							response.setEntity(mapper.writeValueAsString(value));
						}
					}
				}
			}
		} catch (Exception e) {
			eh.genericExceptionHandlerResponse(e, "");
		}
	}
}