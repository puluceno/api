package br.com.redefood.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Address;
import br.com.redefood.model.Login;
import br.com.redefood.model.User;
import br.com.redefood.model.complex.UserLogin;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/login")
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class LoginResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@POST
	@Consumes("application/json")
	@Produces("application/json;charset=UTF8")
	public Response doLogin(@HeaderParam("locale") String locale, UserLogin userLogin) {

		User user = null;
		try {

			if (userLogin.getLogin().contains("(") && userLogin.getLogin().contains(")")
					&& userLogin.getLogin().contains("-")) {

				try {
					user = (User) em.createNamedQuery(User.FIND_USER_BY_CELLPHONE)
							.setParameter("cellphone", userLogin.getLogin()).getSingleResult();

				} catch (NoResultException e) {
					throw new Exception("user cellphone not found");
				}

			} else {

				try {
					user = (User) em.createNamedQuery(User.FIND_USER_BY_EMAIL).setParameter("email", userLogin.getLogin())
							.getSingleResult();

				} catch (NoResultException e) {
					throw new Exception("user email not found");
				}
			}

			if (!user.getPassword().equals(userLogin.getPassword())) {

				String answer = LocaleResource.getProperty(locale).getProperty("exception.password");
				log.log(Level.INFO, answer);
				return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
			}

			Timestamp lastSeen = new Timestamp(new Date().getTime());
			String token = RedeFoodUtils.doHash(Integer.toString(userLogin.hashCode()) + lastSeen);
			Login loginToken = new Login(token, user.getId(), lastSeen, userLogin.getIp());
			user.setLastLogin(lastSeen);
			user.setNumberOfLogins(user.getNumberOfLogins() + 1);
			String jsonUser = "";

			em.persist(loginToken);
			em.merge(user);
			em.flush();

			for (Address address : user.getAddresses()) {
				Hibernate.initialize(address.getCity());
				Hibernate.initialize(address.getNeighborhood());
			}
			if (user.getOrders() != null && !user.getOrders().isEmpty()) {
				Hibernate.initialize(user.getOrders());
			}
			if (user.getNotifications() != null) {
				Hibernate.initialize(user.getNotifications());
			}

			jsonUser = mapper.writeValueAsString(user);

			log.log(Level.INFO, "User " + user.getEmail() + " logged in at " + loginToken.getLastSeen() + " with IP "
					+ userLogin.getIp());

			return Response
					.status(200)
					.entity("{\"" + RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR + "\":" + "\"" + token
							+ "\",\"user\":" + jsonUser + "}").build();

		} catch (Exception e) {
			return eh.loginExceptionHandler(e, locale);
		}

	}

	public void mergeLogin(Login userLogin) throws Exception {
		em.merge(userLogin);
		em.flush();
	}

	public void removeLogin(Login userLogin) throws Exception {
		em.remove(em.merge(userLogin));
		em.flush();
	}
}