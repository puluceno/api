package br.com.redefood.rest;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
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
import br.com.redefood.model.Employee;
import br.com.redefood.model.Login;
import br.com.redefood.model.OpenTime;
import br.com.redefood.model.Restaurant;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryModule;
import br.com.redefood.model.complex.UserLogin;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/admin")
@Stateless
public class AdminLoginResource extends HibernateMapper {
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

		Employee employee = null;

		if (userLogin.getLogin().contains(".") && userLogin.getLogin().contains("-")) {
			try {

				employee = (Employee) em.createNamedQuery(Employee.FIND_BY_CPF)
						.setParameter("cpf", userLogin.getLogin()).getSingleResult();

			} catch (NoResultException e) {
				return eh.loginExceptionHandler(new Exception("employee found"), locale, userLogin.getLogin());
			}
		} else {
			String answer = LocaleResource.getString(locale, "exception.employee.found", userLogin.getLogin());
			return eh.loginExceptionHandler(new Exception("employee found"), locale, answer);
		}

		if (!employee.getPassword().equals(userLogin.getPassword()))
			return eh.loginExceptionHandler(new Exception("invalid password"), locale, "");

		Timestamp lastSeen = new Timestamp(new Date().getTime());
		String token = RedeFoodUtils.doHash(Integer.toString(userLogin.hashCode()) + lastSeen);
		Login loginToken = new Login(token, employee.getId(), lastSeen, userLogin.getIp());
		employee.setLastLogin(new Date());

		try {
			em.persist(loginToken);
			em.merge(employee);
			em.flush();

		} catch (Exception e) {
			return eh.loginExceptionHandler(new Exception("server exception"), locale, "");
		}

		log.log(Level.INFO, "Employee " + employee.getCpf() + " logged in at " + loginToken.getLastSeen() + " with IP "
				+ userLogin.getIp());

		return adminResponse(employee, loginToken.getToken(), locale);
	}

	private Response adminResponse(Employee emp, String token, String locale) {
		if (emp.getProfile().getId().intValue() == 100) {
			try {
				return Response
						.status(200)
						.entity("{\"" + RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR + "\":" + "\"" + token
								+ "\",\"employee\":" + mapper.writeValueAsString(emp) + "}").build();
			} catch (Exception e) {
				return eh.genericExceptionHandlerResponse(e, locale);
			}
		}

		// TODO: know bug: client is just accepting one restaurant, not a list,
		// if employee works in more than one restaurant, an exception will be
		// thrown
		Restaurant restaurant = (Restaurant) em.createNamedQuery(Restaurant.FIND_BY_EMPLOYEE)
				.setParameter("idEmployee", emp.getId()).getSingleResult();

		for (Subsidiary sub : restaurant.getSubsidiaries()) {
			Hibernate.initialize(sub.getAddress());
			Hibernate.initialize(sub.getAddress().getNeighborhood());
			Hibernate.initialize(sub.getAddress().getCity());
			Hibernate.initialize(sub.getConfiguration());
			for (OpenTime openTime : sub.getOpenTime()) {
				Hibernate.initialize(openTime.getDayOfWeek());
			}
			for (SubsidiaryModule subsidiaryModule : sub.getSubsidiaryModules()) {
				Hibernate.initialize(subsidiaryModule.getModule());
			}
		}

		String subsidiariesJson = "";
		String employeeJson = "";

		try {
			subsidiariesJson = mapper.writeValueAsString(restaurant);
			Hibernate.initialize(emp.getProfile());
			if (emp.getAddress() != null) {
				Hibernate.initialize(emp.getAddress());
				Hibernate.initialize(emp.getAddress().getNeighborhood());
				Hibernate.initialize(emp.getAddress().getCity());
			}
			employeeJson = mapper.writeValueAsString(emp);

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}

		return Response
				.status(200)
				.entity("{\"" + RedeFoodConstants.DEFAULT_TOKEN_IDENTIFICATOR + "\":" + "\"" + token
						+ "\", \"restaurant\":" + subsidiariesJson + ",\"employee\":" + employeeJson + "}").build();
	}
}