package br.com.redefood.interceptor;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import br.com.redefood.exceptions.ProfileException;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Login;
import br.com.redefood.util.RedeFoodAnswerGenerator;

@Provider
@ServerInterceptor
@Precedence("ENCODER")
public class DemoSecurityInterceptor implements PreProcessInterceptor,
		AcceptedByMethod {

	@Inject
	private EntityManager em;
	@Inject
	private Logger log;

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method)
			throws Failure, WebApplicationException {

		try {
			String token = request.getHttpHeaders().getRequestHeaders()
					.getFirst("token");

			if (token != null) {
				Login userToken = em.find(Login.class, token);
				Employee demo = em.find(Employee.class, userToken.getIdUser());
				if (demo == null)
					throw new NoResultException();
				if (demo.getProfile().getId().intValue() == 99)
					throw new ProfileException();
			}

		} catch (NoResultException e) {
			String answer = "Could not find user token at RedeFood. Denying access.";
			log.log(Level.INFO, answer);
			return (ServerResponse) RedeFoodAnswerGenerator
					.generateErrorAnswer(401, answer);

		} catch (ProfileException e) {
			String answer = "Unauthorized Profile.";
			log.log(Level.INFO, answer);
			return (ServerResponse) RedeFoodAnswerGenerator
					.generateErrorAnswer(403, answer);
		} catch (Exception e) {
			String answer = "Failed to persist user token.";
			log.log(Level.SEVERE, answer);
			return (ServerResponse) RedeFoodAnswerGenerator
					.generateErrorAnswer(500, answer);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean accept(Class declaring, Method method) {
		return (method.isAnnotationPresent(PUT.class)
				|| method.isAnnotationPresent(POST.class) || method
					.isAnnotationPresent(DELETE.class));
	}
}