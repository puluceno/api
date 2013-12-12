package br.com.redefood.rest;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.redefood.annotations.OwnerOrManager;
import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Login;
import br.com.redefood.model.Profile;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.service.FileUploadService;
import br.com.redefood.util.CPFValidator;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class produces a RESTful service to read the contents of the Cities
 * table.
 */
@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/employee")
@Stateless
public class EmployeeResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	/**
	 * Retrieves informations of all Employees present into the database. Offset
	 * and Limit are required parameters.
	 * 
	 * @param name
	 * @param offset
	 * @param limit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@OwnerOrManager
	@GET
	@Produces("application/json;charset=UTF8")
	public String listAllLessMyself(@PathParam("idSubsidiary") Short idSubsidiary, @HeaderParam("token") String token,
			@HeaderParam("locale") String locale, @DefaultValue("1") @QueryParam("offset") Integer offset,
			@DefaultValue("100") @QueryParam("limit") Integer limit) {

		try {

			Login loggedUser = em.find(Login.class, token);
			Employee emp = em.find(Employee.class, (short) loggedUser.getIdUser());

			List<Employee> resultList = em.createNamedQuery(Employee.FIND_ALL_LESS_MYSELF)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("idEmployee", emp.getId())
					.setMaxResults(limit).setFirstResult(offset - 1).getResultList();
			for (Employee employee : resultList) {
				if (employee.getAddress() != null) {
					Hibernate.initialize(employee.getAddress());
					Hibernate.initialize(employee.getAddress().getCity());
					Hibernate.initialize(employee.getAddress().getNeighborhood());
				}
				Hibernate.initialize(employee.getProfile());
			}

			return mapper.writeValueAsString(resultList);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}

	}

	/**
	 * Retrieves information of a single Employee.
	 * 
	 * @param id
	 * @param token
	 * @return
	 */
	@OwnerOrManager
	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String lookupUserById(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("id") Short id) {

		try {
			Employee employee = em.find(Employee.class, id);
			if (employee.getAddress() != null) {
				Hibernate.initialize(employee.getAddress());
				Hibernate.initialize(employee.getAddress().getCity());
				Hibernate.initialize(employee.getAddress().getNeighborhood());
			}
			Hibernate.initialize(employee.getProfile());
			return mapper.writeValueAsString(employee);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	/**
	 * Method responsible for listing all employees that works on provided
	 * subsidiary, filtered by profile received by query parameter.
	 * 
	 * @param locale
	 *            locale
	 * @param idSubsidiary
	 *            idSubsidiary
	 * @param idProfile
	 *            idProfile
	 * @return List of found employees who meets the given parameters.
	 */
	@Securable
	@GET
	@Path("/profile")
	@Produces("application/json;charset=UTF8")
	public String lookupEmployeeByProfile(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("profile") Short idProfile) {
		try {
			return mapper.writeValueAsString(em.createNamedQuery(Employee.FIND_BY_SUBSIDIARY_AND_PROFILE)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("idProfile", idProfile).getResultList());
		} catch (Exception e) {
			return eh.employeeExceptions(e, locale, "").getEntity().toString();
		}
	}

	/**
	 * Method used to persist a new Employee and then send him an e-mail to
	 * verify the authenticity of his email address.
	 * 
	 * @param employee
	 * @return
	 */
	@OwnerOrManager
	@POST
	@Consumes("application/json")
	public Response newEmployee(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, Employee employee) {

		if (isManager(token)
				&& (employee.getProfile().getId() == Profile.OWNER || employee.getProfile().getId() == Profile.MANAGER))
			return RedeFoodAnswerGenerator.unauthorizedProfile();

		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
		if (subsidiary == null) {
			String answer = LocaleResource.getString(locale, "exception.employee.subsidiary", idSubsidiary);
			return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
		}

		try {
			Employee toPersist = validateCreate(employee);
			subsidiary.getEmployees().add(toPersist);
			em.persist(subsidiary);
			em.flush();

			String answer = LocaleResource.getString(locale, "employee.created", employee.getFirstName(), idSubsidiary);
			log.log(Level.INFO, answer);
			return RedeFoodAnswerGenerator.generateSuccessPOSTwithImageEmployee(employee.getId(), employee.getPhoto(),
					201);

		} catch (Exception e) {
			return eh.employeeExceptions(e, locale, employee.getFirstName(), employee.getCpf());
		}

	}

	@Securable
	@POST
	@Path("/{idEmployee:[0-9][0-9]*}/photo")
	@Consumes("multipart/form-data")
	public Response addEmployeePhoto(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idEmployee") Short idEmployee,
			MultipartFormDataInput photo) {

		try {
			Employee employee = em.find(Employee.class, idEmployee);

			if (employee == null) {
				String answer = LocaleResource.getProperty(locale).getProperty("exception.user.null");
				log.log(Level.INFO, answer);
				return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
			}

			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

			String uploadFile = FileUploadService.uploadFile("restaurant/"
					+ subsidiary.getRestaurant().getIdRestaurant() + "/"
					+ subsidiary.getClass().getSimpleName().toLowerCase() + "/" + idSubsidiary + "/"
					+ employee.getClass().getSimpleName().toLowerCase(), employee.getId().toString(), photo);
			if (uploadFile.contains("error"))
				throw new Exception("file error");

			// clear the old photo, to make the system overwrite it
			FileUploadService.deleteOldFile(employee.getPhoto());

			employee.setPhoto(uploadFile);

			em.merge(employee);
			em.flush();

			String answer = LocaleResource.getString(locale, "employee.updated", employee.getFirstName(),
					employee.getCpf(), idSubsidiary);
			log.log(Level.INFO, answer);
			return RedeFoodAnswerGenerator.generateSuccessAnswerWithoutSuccess(200, uploadFile);

		} catch (Exception e) {
			return eh.employeeExceptions(e, locale);
		}

	}

	/**
	 * Method responsible to merge the edited information relative to a user
	 * 
	 * @param id
	 * @param token
	 * @param employee
	 * @return
	 */
	@OwnerOrManager
	@PUT
	@Consumes("application/json")
	@Path("/{idEmployee:[0-9][0-9]*}")
	public Response editEmployee(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idEmployee") Short idEmployee, Employee employee) {

		if (isManager(token)
				&& (employee.getProfile().getId() == Profile.OWNER || employee.getProfile().getId() == Profile.MANAGER))
			return RedeFoodAnswerGenerator.unauthorizedProfile();

		try {
			Employee toMerge = validateEdit(idEmployee, employee);
			em.merge(toMerge);
			em.flush();

			String answer = LocaleResource.getString(locale, "employee.updated", employee.getFirstName(),
					employee.getCpf(), idSubsidiary);
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.employeeExceptions(e, locale, employee.getFirstName(), employee.getCpf(),
					String.valueOf(employee.getId()));
		}
	}

	/**
	 * Users are not removed from the database, they are only deactivated.
	 * 
	 * @param idEmployee
	 * @param token
	 * @return
	 */
	@OwnerOrManager
	@DELETE
	@Consumes("application/json")
	@Path("/{idEmployee:[0-9][0-9]*}")
	public Response deleteEmployee(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idEmployee") Short idEmployee) {

		// Do not allow managers to change owner
		if (isManager(token) && em.find(Employee.class, idEmployee).getProfile().getId() == Profile.OWNER)
			return RedeFoodAnswerGenerator.unauthorizedProfile();

		Employee toDelete = em.find(Employee.class, idEmployee);

		try {
			if (toDelete == null)
				throw new Exception("bad id");

			toDelete.setActive(false);

			em.merge(toDelete);
			em.flush();

			String answer = LocaleResource.getString(locale, "employee.deactivated", toDelete.getFirstName(),
					idSubsidiary);
			log.log(Level.INFO, answer);
			return RedeFoodAnswerGenerator.generateSuccessAnswer(200, answer);

		} catch (Exception e) {
			return eh.employeeExceptions(e, locale, String.valueOf(toDelete == null ? null : toDelete.getFirstName()),
					String.valueOf(toDelete == null ? null : toDelete.getCpf()),
					String.valueOf(toDelete == null ? null : toDelete.getId()));
		}

	}

	/**
	 * Validates a new created user, setting a temporary password, active to
	 * false and @param(numberOfLogins) to zero
	 * 
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	public Employee validateCreate(Employee employee) throws Exception {
		if (!CPFValidator.isCPF(employee.getCpf()))
			throw new Exception("invalid cpf");
		employee.setActive(true);

		if (employee.getAddress() == null || employee.getAddress().getZipcode() == null
				|| employee.getAddress().getZipcode().isEmpty()) {
			employee.setAddress(null);
		}

		if (employee.getPassword() == null) {
			SecureRandom random = new SecureRandom();
			String noHash = new BigInteger(30, random).toString(32);
			employee.setPassword(noHash);
		}

		return employee;
	}

	/**
	 * Validates users attributes when they are edited. Thrown exception is
	 * caught in the above method.
	 * 
	 * @param idEmployee
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	private Employee validateEdit(Short idEmployee, Employee employee) throws Exception {

		if (!CPFValidator.isCPF(employee.getCpf()))
			throw new Exception("invalid cpf");

		Employee toMerge = em.find(Employee.class, idEmployee);
		if (toMerge == null)
			throw new Exception("bad id");
		toMerge.setFirstName(employee.getFirstName());
		toMerge.setLastName(employee.getLastName());
		toMerge.setEmail(employee.getEmail());
		toMerge.setPhone(employee.getPhone());
		toMerge.setCellphone(employee.getCellphone());
		toMerge.setRg(employee.getRg());
		toMerge.setSex(employee.isSex());
		if (employee.getPassword() != null && !employee.getPassword().equals("")) {
			toMerge.setPassword(employee.getPassword());
		}
		toMerge.setPhoto(employee.getPhoto());
		if (employee.getAddress() != null) {
			toMerge.setAddress(employee.getAddress());
		}
		if (employee.getProfile() != null) {
			toMerge.setProfile(employee.getProfile());
		}

		if (employee.getAddress() == null || employee.getAddress().getZipcode().isEmpty()) {
			toMerge.setAddress(null);
		}

		toMerge.setActive(employee.getActive());

		return toMerge;
	}

	public Boolean isEmployee(String token) {
		if (getProfile(token).equals(Profile.EMPLOYEE))
			return true;
		return false;
	}

	public Boolean isManager(String token) {
		if (getProfile(token).equals(Profile.MANAGER))
			return true;
		return false;
	}

	public Boolean isOwner(String token) {
		if (getProfile(token).equals(Profile.OWNER) || getProfile(token).equals(Profile.DEMO))
			return true;
		return false;
	}

	private Short getProfile(String token) {
		Login loggedUser = em.find(Login.class, token);
		Employee employee = em.find(Employee.class, (short) loggedUser.getIdUser());
		return employee.getProfile().getId();
	}

}