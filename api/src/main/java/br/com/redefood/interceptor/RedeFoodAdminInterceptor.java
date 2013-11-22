package br.com.redefood.interceptor;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.RedirectPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import br.com.redefood.annotations.RedeFoodAdmin;
import br.com.redefood.exceptions.ExpiredTokenException;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Login;
import br.com.redefood.model.Profile;
import br.com.redefood.rest.LoginResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;

@Provider
@ServerInterceptor
@RedirectPrecedence
public class RedeFoodAdminInterceptor implements PreProcessInterceptor, AcceptedByMethod {

	@Inject
	private EntityManager em;
	@Context
	private HttpServletRequest servletRequest;
	@Inject
	private LoginResource loginResource;
	@Inject
	private RedeFoodExceptionHandler eh;

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure,
	WebApplicationException {

		RedeFoodAdmin owner = method.getMethod().getAnnotation(RedeFoodAdmin.class);
		String token = servletRequest.getHeader(owner.header());

		try {
			Login userToken = em.find(Login.class, token);

			if (userToken == null || userToken.getToken() == null || userToken.getToken() == "")
				throw new Exception("Invalid user token");

			if (!userToken.getToken().equals(token))
				throw new Exception("Invalid user token");

			verifyExpiration(userToken);

			Short idProfile = (Short) em.createNamedQuery(Employee.FIND_ID_PROFILE_BY_LOGIN)
					.setParameter("idEmployee", (short) userToken.getIdUser()).getSingleResult();

			if (!idProfile.equals(Profile.ADMIN_REDEFOOD))
				throw new Exception("unauthorized");

		} catch (Exception e) {
			if (e.getMessage().contentEquals("unauthorized"))
				return (ServerResponse) RedeFoodAnswerGenerator.unauthorizedProfile();
			return (ServerResponse) eh.tokenExceptionHandler(e, null);

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
		return method.isAnnotationPresent(RedeFoodAdmin.class);
	}
}