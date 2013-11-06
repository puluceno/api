package br.com.redefood.interceptor;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.SecurityPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.ExpiredTokenException;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Login;
import br.com.redefood.rest.LoginResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;

@Provider
@ServerInterceptor
@SecurityPrecedence
public class SecurableInterceptor implements PreProcessInterceptor,
		AcceptedByMethod {

	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private LoginResource loginResource;
	@Inject
	private RedeFoodExceptionHandler eh;
	@Context
	private HttpServletRequest servletRequest;

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method)
			throws Failure, WebApplicationException {

		Securable securable = method.getMethod().getAnnotation(Securable.class);
		String token = servletRequest.getHeader(securable.header());

		if (token == null) {
			String answer = "User token required but not provided. Denying access.";
			log.log(Level.WARNING, answer);
			return (ServerResponse) RedeFoodAnswerGenerator
					.generateErrorAnswer(401, answer);
		} else {

			try {

				Login userToken = em.find(Login.class, token);

				if (userToken == null || userToken.getToken() == null
						|| userToken.getToken() == "") {
					throw new Exception("Invalid user token");
				}

				if (!userToken.getToken().equals(token)) {
					throw new Exception("Invalid user token");
				}
				verifyExpiration(userToken);

			} catch (Exception e) {
				return (ServerResponse) eh.tokenExceptionHandler(e, null);
			}
		}
		return null;
	}

	private void verifyExpiration(Login userToken) throws Exception {
		if (userToken.isExpired()) {
			loginResource.removeLogin(userToken);
			throw new Exception("token expired");
		}
		Timestamp now = new Timestamp(new Date().getTime());
		if (now.getTime() - userToken.getLastSeen().getTime() > new Long(
				RedeFoodConstants.DEFAULT_TOKEN_EXPIRATION_TIME)) {
			userToken.setExpired(true);
			loginResource.mergeLogin(userToken);
			throw new ExpiredTokenException("token expired");
		}

		userToken.setLastSeen(now);
		loginResource.mergeLogin(userToken);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean accept(Class clazz, Method method) {
		return method.isAnnotationPresent(Securable.class);
	}
}