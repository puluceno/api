package br.com.redefood.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.SecurityPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

@Provider
@ServerInterceptor
@SecurityPrecedence
public class IPSecurityInterceptor implements PreProcessInterceptor {

	@Context
	HttpServletRequest req;

	@Override
	@Produces("application/json")
	public ServerResponse preProcess(HttpRequest request, ResourceMethod resource) throws Failure,
			WebApplicationException {

		String host = req.getRemoteHost();

		if (host == null || host == "" || host.isEmpty() || host.equals("")) {

//			String answer = "Could not read user IP Address.";
//			log.log(Level.WARNING, answer);
			// return (ServerResponse)
			// RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);

		} else {

			// log.log(Level.INFO, "Veryfing user IP address...");

			/*
			 * if (!host.equals(RedeFoodConstants.DEFAULT_CLIENT_HOST)) {
			 * 
			 * String answer = "User IP address not authorized.!";
			 * log.log(Level.WARNING, answer); return (ServerResponse)
			 * RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
			 * 
			 * }
			 */
			// log.log(Level.INFO, "User IP address match, allowing access.");
		}
		return null;
	}
}