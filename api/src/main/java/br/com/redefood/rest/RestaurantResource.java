package br.com.redefood.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import br.com.redefood.annotations.Owner;
import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.AdminRFNewRestaurantNotificator;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.restaurant.NewRestaurantNotificator;
import br.com.redefood.model.Address;
import br.com.redefood.model.BeverageType;
import br.com.redefood.model.City;
import br.com.redefood.model.Configuration;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Ingredient;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealIngredientTypes;
import br.com.redefood.model.MealType;
import br.com.redefood.model.Module;
import br.com.redefood.model.Neighborhood;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Profile;
import br.com.redefood.model.Restaurant;
import br.com.redefood.model.RestaurantType;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryMessages;
import br.com.redefood.model.SubsidiaryModule;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.model.complex.RestaurantComplexType;
import br.com.redefood.model.enumtype.SubsidiaryMessageLocation;
import br.com.redefood.service.FileUploadService;
import br.com.redefood.util.CNPJValidator;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodMailUtil;
import br.com.redefood.util.RedeFoodRegex;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class produces a RESTful service to read the contents of the Cities
 * table.
 */

@Path("/restaurant")
@Stateless
public class RestaurantResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private EmployeeResource er;
	@Inject
	private RedeFoodExceptionHandler eh;

	/**
	 * 
	 * @return Returns a list with all Restaurants Subsidiaries available.
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Produces("application/json;charset=UTF8")
	public String listByName(@HeaderParam("locale") String locale, @QueryParam("subdomain") String subdomain) {

		try {
			List<Restaurant> restaurants = null;
			Restaurant restaurant = null;

			if (subdomain == null) {
				restaurants = em.createNamedQuery(Restaurant.FIND_ALL).getResultList();
				for (Restaurant rest : restaurants) {
					Hibernate.initialize(rest.getSubsidiaries());
					for (Subsidiary subsidiary : rest.getSubsidiaries()) {

						Hibernate.initialize(subsidiary.getMealTypes());

						for (SubsidiaryModule subsidiaryModule : subsidiary.getSubsidiaryModules()) {
							Hibernate.initialize(subsidiaryModule.getModule());
						}
					}
					Hibernate.initialize(rest.getRestaurantTypes());
				}

			} else {
				restaurant = (Restaurant) em.createNamedQuery(Restaurant.FIND_BY_SUBDOMAIN)
						.setParameter("subdomain", subdomain).getSingleResult();

				if (restaurant != null) {
					Hibernate.initialize(restaurant.getSubsidiaries());
					for (Subsidiary subsidiary : restaurant.getSubsidiaries()) {

						for (SubsidiaryModule subsidiaryModule : subsidiary.getSubsidiaryModules()) {
							Hibernate.initialize(subsidiaryModule.getModule());
						}
						Hibernate.initialize(subsidiary.getMealTypes());
					}
					Hibernate.initialize(restaurant.getRestaurantTypes());
				}
			}

			if (restaurant == null)
				return mapper.writeValueAsString(restaurants);
			else
				return mapper.writeValueAsString(restaurant);

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale).getEntity().toString();
		}
	}

	/**
	 * 
	 * @param id
	 *            Requested Restaurant ID
	 * @return Returns a list with all subsidiaries to specified Restaurant ID
	 */
	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String lookupRestaurantById(@HeaderParam("locale") String locale, @PathParam("id") Short id) {
		try {
			Restaurant restaurant = em.find(Restaurant.class, id);

			// The code below removes the duplicated entries populated from
			// hibernate
			for (Subsidiary subsidiary : restaurant.getSubsidiaries()) {
				Set<RestaurantType> setItems = new HashSet<RestaurantType>(subsidiary.getRestaurant()
						.getRestaurantTypes());
				subsidiary.getRestaurant().getRestaurantTypes().clear();
				subsidiary.getRestaurant().getRestaurantTypes().addAll(setItems);
			}

			return mapper.writeValueAsString(restaurant);

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale).toString();
		}
	}

	@GET
	@Path("/{id:[0-9][0-9]*}/restaurant-type")
	@Produces("application/json;charset=UTF8")
	public String lookupRestaurantTypeById(@HeaderParam("locale") String locale, @PathParam("id") Short id) {
		try {

			return mapper.writeValueAsString(em.createNamedQuery(RestaurantType.FIND_BY_RESTAURANT)
					.setParameter("idRestaurant", id).getResultList());

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale).toString();
		}
	}

	/**
	 * Adds a new Restaurant and a new Subsidiary. Also adds obligatory fields
	 * to those entities, like RestaurantType and Address.
	 * 
	 * @param token
	 *            User identification
	 * @param subsidiary
	 *            Restaurant and Subsidiary being persisted into the database.
	 * @return Response object to the requester
	 */
	@POST
	@Consumes("application/json")
	public Response addRestaurant(@HeaderParam("locale") String locale, RestaurantComplexType restaurantCT) {

		try {
			if (restaurantCT.getEmployee() == null)
				throw new Exception("owner null");
			if (restaurantCT.getRestaurant() == null)
				throw new Exception("restaurant null");
			if (restaurantCT.getSubsidiary() == null)
				throw new Exception("subsidiary null");

			// Persist Restaurant
			Restaurant restaurant = validateRestaurant(restaurantCT.getRestaurant());

			log.log(Level.INFO, "Registering Restaurant " + restaurantCT.getRestaurant().getName()
					+ " Registering Subsidiary with CNPJ " + restaurantCT.getSubsidiary().getCnpj()
					+ " and owner with cpf " + restaurantCT.getEmployee().getCpf());

			Employee employee = restaurantCT.getEmployee();
			// Performs cpf validation and creates a temporary password
			er.validateCreate(employee);
			employee.setProfile(em.find(Profile.class, Profile.OWNER));

			Subsidiary subsidiary = validateNewSubsidiary(restaurantCT.getSubsidiary(), restaurant, employee,
					restaurantCT.getModule());

			List<Subsidiary> subsidiaries = new ArrayList<Subsidiary>();
			subsidiaries.add(subsidiary);
			restaurant.setSubsidiaries(subsidiaries);
			// Persist Restaurant, Subsidiary and then employee as owner
			em.persist(restaurant);
			em.flush();

			sendNewRestaurantNotification(restaurantCT);

			String answer = LocaleResource.getString(locale, "restaurant.created", restaurant.getName());
			log.log(Level.INFO, answer);
			return Response.status(201).build();

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale, restaurantCT.getRestaurant());
		}
	}

	private Restaurant validateRestaurant(Restaurant restaurant) throws Exception {
		if (restaurant.getName() == null || restaurant.getName().length() < 3)
			throw new Exception("invalid name");
		if (restaurant.getSubdomain() == null || restaurant.getSubdomain().length() < 3
				|| !RedeFoodRegex.verifySubDomain(restaurant.getSubdomain()))
			throw new Exception("invalid subdomain");

		restaurant.setInsertDate(new Date());
		return restaurant;
	}

	private void sendNewRestaurantNotification(RestaurantComplexType rct) throws Exception {

		Notificator notificator = new NewRestaurantNotificator();

		log.log(Level.INFO, "Sending e-mail to " + rct.getEmployee().getFirstName() + ", owner of "
				+ rct.getRestaurant().getName() + " with cnpj " + rct.getSubsidiary().getCnpj());

		notificator.send(prepareEmailData(rct));

		Notificator adminNotificator = new AdminRFNewRestaurantNotificator();
		adminNotificator.send(prepareEmailData(rct));
	}

	private EmailDataDTO<String, String> prepareEmailData(RestaurantComplexType rct) {
		EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();
		RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);

		emailData.put("userName", rct.getEmployee().getFirstName().toUpperCase());
		emailData.put("restaurantName", rct.getRestaurant().getName().toUpperCase());
		emailData.put("addressee", rct.getEmployee().getEmail());
		emailData.put("cnpj", CNPJValidator.unMaskCNPJ(rct.getSubsidiary().getCnpj()));
		emailData.put("subsidiaryCity", rct.getSubsidiary().getAddress().getCity().getName());
		emailData.put("subsidiaryNeighborhood", rct.getSubsidiary().getAddress().getNeighborhood().getName());
		emailData.put("idSubsidiary", String.valueOf(rct.getSubsidiary().getId()));
		return emailData;
	}

	/**
	 * Method responsible for performing some variables initializing, like City,
	 * Neighborhood and RestaurantType.
	 * 
	 * @param subsidiary
	 *            Subsidiary to be validated and have some variables initialized
	 *            like City, Neighborhood and RestaurntType
	 * @return Subsidiary with variables initialized
	 * @throws Exception
	 */
	private Subsidiary validateNewSubsidiary(Subsidiary subsidiary, Restaurant restaurant, Employee owner,
			Module insertModule) throws Exception {

		if (!CNPJValidator.isCNPJ(subsidiary.getCnpj()))
			throw new Exception("invalid cnpj");

		validateAddress(subsidiary.getAddress());

		if (insertModule == null)
			throw new Exception("module null");

		subsidiary = validateAddSubsidiary(subsidiary);

		verifyNeighborhoodsToSubsidiaryCity(subsidiary.getAddress().getCity());

		subsidiary.setName(restaurant.getName());
		subsidiary.setHeadOffice(true);
		subsidiary.setActive(false);
		subsidiary.setInsertDate(new Date());
		subsidiary.setConfiguration(new Configuration(true, true, true, true, true, true));

		List<SubsidiaryMessages> subsidiaryMessages = new ArrayList<SubsidiaryMessages>();
		subsidiaryMessages.add(new SubsidiaryMessages("", SubsidiaryMessageLocation.MAIN, subsidiary));
		subsidiaryMessages.add(new SubsidiaryMessages("", SubsidiaryMessageLocation.CHECKOUT, subsidiary));
		subsidiaryMessages.add(new SubsidiaryMessages("", SubsidiaryMessageLocation.MENU, subsidiary));
		subsidiaryMessages.add(new SubsidiaryMessages("", SubsidiaryMessageLocation.MONITOR, subsidiary));
		subsidiary.setMessages(subsidiaryMessages);

		subsidiary.setRestaurant(restaurant);
		subsidiary.setEmployees(new ArrayList<Employee>());
		subsidiary.getEmployees().add(owner);

		Module module = em.find(Module.class, insertModule.getId());

		SubsidiaryModule subModule = new SubsidiaryModule(new Date(), null, subsidiary, module, true);
		subModule.setCharged(false);
		subModule.setDeactivate(false);

		List<SubsidiaryModule> subsidiaryModules = new ArrayList<SubsidiaryModule>();
		subsidiaryModules.add(subModule);
		subsidiary.setSubsidiaryModules(subsidiaryModules);

		if (module.getId() == Module.MODULE_LOCAL) {
			OrderType orderType1 = em.find(OrderType.class, OrderType.LOCAL);
			OrderType orderType2 = em.find(OrderType.class, OrderType.LOCAL_TO_GO);
			List<OrderType> ot = new ArrayList<OrderType>();
			ot.add(orderType1);
			ot.add(orderType2);
			subsidiary.setOrderTypes(ot);
		}
		if (module.getId() == Module.MODULE_SQUARE || module.getId() == Module.MODULE_SITE) {
			OrderType orderType1 = em.find(OrderType.class, OrderType.DELIVERY_ONLINE);
			// Em breve será fornecido o tipo Agendamento Online, ainda
			// não disponível.
			// OrderType orderType2 = em.find(OrderType.class,
			// OrderType.SCHEDULE_ONLINE);
			List<OrderType> ot = new ArrayList<OrderType>();
			ot.add(orderType1);
			// ot.add(orderType2);
			subsidiary.setOrderTypes(ot);
		}

		return subsidiary;
	}

	/**
	 * Method responsible for verifying if a city is present into RedeFood
	 * database. If it does not, this method insert the city and all found
	 * neighborhoods to the database.
	 * 
	 * @param city
	 */
	private void verifyNeighborhoodsToSubsidiaryCity(City city) {
		if (city.getNeighborhoods().isEmpty()) {

		}

	}

	/**
	 * Adds a new Subsidiary to an existing Restaurant.
	 * 
	 * @param id
	 *            Restaurant ID where the new subsidiary will belong.
	 * @param token
	 *            User identification
	 * @param subsidiary
	 *            New subsidiary to be persisted to the database
	 * @return Response object to the requester
	 */
	@POST
	@Path("/{idRestaurant:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response addSubsidiary(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idRestaurant") Short idRestaurant, Subsidiary subsidiary) {

		log.log(Level.INFO, "Registering " + subsidiary.getName());

		Subsidiary toPersist;
		Restaurant restaurant = null;
		try {
			toPersist = validateAddSubsidiary(subsidiary);

			restaurant = em.find(Restaurant.class, idRestaurant);
			toPersist.setRestaurant(restaurant);

			em.persist(toPersist.getAddress());
			em.persist(toPersist);

			String answer = LocaleResource.getString(locale, "subsidiary.created", toPersist.getName());
			log.log(Level.INFO, answer);
			return Response.status(201).entity(answer).build();

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale, restaurant);
		}
	}

	@Securable
	@POST
	@Path("/{idRestaurant:[0-9][0-9]*}/logo")
	@Consumes("multipart/form-data")
	public Response addRestaurantLogo(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idRestaurant") Short idRestaurant, MultipartFormDataInput logo) {

		if (er.isOwner(token)) {
			try {
				Restaurant restaurant = em.find(Restaurant.class, idRestaurant);

				if (restaurant == null) {
					String answer = LocaleResource.getString(locale, "exception.restaurant.found", idRestaurant);
					log.log(Level.INFO, answer);
					return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
				}

				log.log(Level.INFO, "Uploading photo to restaurant " + restaurant.getName());

				if (restaurant.getLogo() != null && !restaurant.getLogo().contains("default")) {
					FileUploadService.deleteOldFile(restaurant.getLogo());
				}

				String uploadFile = FileUploadService.uploadFile(restaurant.getClass().getSimpleName().toLowerCase(),
						idRestaurant.toString(), logo);
				if (uploadFile.contains("error"))
					throw new Exception("file error");

				restaurant.setLogo(uploadFile);

				em.merge(restaurant);
				em.flush();

				String answer = LocaleResource.getString(locale, "restaurant.updated", restaurant.getName());
				log.log(Level.INFO, answer);
				return RedeFoodAnswerGenerator.generateSuccessAnswerWithoutSuccess(200, uploadFile);

			} catch (Exception e) {
				return eh.restaurantExceptionHandler(e, locale);
			}
		} else
			return RedeFoodAnswerGenerator.unauthorizedProfile();
	}

	@Securable
	@Path("/{idRestaurant:[0-9][0-9]*}/{idSubsidiary:[0-9][0-9]*}")
	@DELETE
	public Response removeSubsidiary(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idRestaurant") Short idRestaurant, @PathParam("idSubsidiary") Short idSubsidiary) {

		log.log(Level.INFO, "Removing Subsidiary " + idSubsidiary + " from Restaurant " + idRestaurant);
		try {
			Subsidiary subsidiary = (Subsidiary) em
					.createNamedQuery(Subsidiary.FIND_SUBSIDIARY_BY_ID_AND_RESTAURANT_ID)
					.setParameter("idRestaurant", idRestaurant).setParameter("idSubsidiary", idSubsidiary)
					.getSingleResult();

			em.remove(subsidiary);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.removed", idSubsidiary);
			log.log(Level.INFO, answer);
			return Response.status(200).entity(answer).build();

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale);
		}

	}

	/**
	 * Updates both Restaurant and Subsidiary, merging them into the database.
	 * 
	 * @param idRestaurant
	 *            Restaurant id passed through URL
	 * @param token
	 *            User identificator
	 * @param subsidiary
	 *            Restaurant Subsidiary to be updated
	 * @return Response object to the requester
	 */
	@Securable
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editSubsidiaryAndRestaurant(@PathParam("idSubsidiary") Short idSubsidiary,
			@HeaderParam("locale") String locale, Subsidiary subsidiary) {

		log.log(Level.INFO, "Updating Restaurant " + subsidiary.getRestaurant().getName() + " and Subsidiary "
				+ subsidiary.getName());

		try {
			Subsidiary toMerge = validateEdit(subsidiary, idSubsidiary);
			em.merge(toMerge);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.updated", toMerge.getName());
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.restaurantExceptionHandler(e, locale);
		}
	}

	/**
	 * Method that initialize restaurant types variables, so that the system
	 * could insert them into database.
	 * 
	 * @param subsidiary
	 *            Restaurant Subsidiary to be validated
	 * @return Subsidiary with initialized components
	 */
	private Subsidiary validateEdit(Subsidiary subsidiary, Short idSubsidiary) throws Exception {

		Subsidiary toMerge = em.find(Subsidiary.class, idSubsidiary);
		if (toMerge == null)
			throw new Exception("bad id");
		toMerge.setAccountList(subsidiary.getAccountList());
		if (subsidiary.getAddress() != null) {
			toMerge.setAddress(subsidiary.getAddress());
		}
		// CNPJ can't be changed. It is disabled at the interfaces and verified
		// when inserted.
		// toMerge.setCnpj(subsidiary.getCnpj());
		toMerge.setDescription(subsidiary.getDescription());
		if (subsidiary.getEmployees() != null) {
			toMerge.setEmployees(subsidiary.getEmployees());
		}
		toMerge.setHeadOffice(subsidiary.getHeadOffice());
		toMerge.setImage(subsidiary.getImage());
		toMerge.setImageBanner(subsidiary.getImageBanner());
		toMerge.setImageLogo(subsidiary.getImageLogo());
		toMerge.setMinOrder(subsidiary.getMinOrder());
		toMerge.setName(subsidiary.getName());
		if (subsidiary.getPaymentMethod() != null) {
			toMerge.setPaymentMethod(subsidiary.getPaymentMethod());
		}
		if (subsidiary.getOpenTime() != null) {
			toMerge.setOpenTime(subsidiary.getOpenTime());
		}
		if (subsidiary.getSubsidiaryhasPromotionList() != null) {
			toMerge.setSubsidiaryhasPromotionList(subsidiary.getSubsidiaryhasPromotionList());
		}

		/*
		 * List<RestaurantType> aux = new ArrayList<RestaurantType>();
		 * 
		 * for (RestaurantType rt :
		 * subsidiary.getRestaurant().getRestaurantTypeList()) {
		 * aux.add(em.find(RestaurantType.class, rt.getIdRestaurantType())); }
		 * subsidiary.getRestaurant().setRestaurantTypeList(aux);
		 */

		return toMerge;
	}

	/**
	 * Method responsible to validate a new subsidiary, initializing City and
	 * Neighborhood.
	 * 
	 * @param subsidiary
	 *            Subsidiary to have parameters initialized.
	 * @return Subisidiary with initialized parameters.
	 */
	private Subsidiary validateAddSubsidiary(Subsidiary subsidiary) throws Exception {

		if (!CNPJValidator.isCNPJ(subsidiary.getCnpj()))
			throw new Exception("invalid cnpj");

		validateAddress(subsidiary.getAddress());

		Address address = subsidiary.getAddress();
		address.setCity(em.find(City.class, subsidiary.getAddress().getCity().getId()));
		address.setNeighborhood(em.find(Neighborhood.class, address.getNeighborhood().getId()));
		subsidiary.setAddress(address);
		subsidiary.getCitiesAttended().add(address.getCity());

		return subsidiary;
	}

	private void validateAddress(Address address) throws Exception {
		if (address.getStreet() == null || address.getStreet().length() < 5)
			throw new Exception("invalid street");
		if (address.getCity() == null)
			throw new Exception("invalid city");
		if (address.getNeighborhood() == null)
			throw new Exception("invalid neighborhood");
		if (address.getZipcode() == null || address.getZipcode().length() != 9)
			throw new Exception("invalid zipcode");
		if (address.getNumber() == null || address.getNumber().length() == 0)
			throw new Exception("invalid number");
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("{idRestaurant:[0-9][0-9]*}/subsidiary/{idSubsidiary:[0-9][0-9]*}/menu")
	@Produces("application/json;charset=UTF8")
	public String restaurantMenu(@HeaderParam("locale") String locale, @PathParam("idRestaurant") Short idRestaurant,
			@PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("localOnly") Boolean localOnly) {

		try {

			List<BeverageType> beverages = null;
			List<MealType> meals = null;

			if (localOnly != null) {
				beverages = em.createNamedQuery(BeverageType.FIND_LOCAL_OR_DELIVERY__BY_RESTAURANT)
						.setParameter("idSubsidiary", idSubsidiary).setParameter("localOnly", localOnly)
						.getResultList();

				meals = em.createNamedQuery(MealType.FIND_MEALS_LOCAL_OR_DELIVERY_BY_MEALTYPE_AND_SUBSIDIARY)
						.setParameter("idSubsidiary", idSubsidiary).setParameter("localOnly", localOnly)
						.getResultList();

			} else {
				beverages = em.createNamedQuery(BeverageType.FIND_ALL_BY_RESTAURANT)
						.setParameter("idSubsidiary", idSubsidiary).getResultList();

				meals = em.createNamedQuery(MealType.FIND_MEALS_BY_MEALTYPE_AND_SUBSIDIARY)
						.setParameter("idSubsidiary", idSubsidiary).getResultList();
			}

			for (MealType mealType : meals) {
				for (Meal meal : mealType.getMeals()) {
					for (MealIngredientTypes mit : meal.getMealIngredientTypes()) {
						Hibernate.initialize(mit.getMealIngredientTypeshasIngredient());
						for (Ingredient ingredient : mit.getIngredientType().getIngredients()) {
							Hibernate.initialize(ingredient);
						}
					}
				}
			}
			String mealString = mapper.writeValueAsString(meals);
			String beverageString = mapper.writeValueAsString(beverages);
			return RedeFoodAnswerGenerator.generateRestaurantMenu(mealString, beverageString);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@SuppressWarnings("unchecked")
	@Owner
	@GET
	@Path("/restaurant-type")
	@Produces("application/json;charset=UTF8")
	public String findAllRestaurantTypes(@HeaderParam("locale") String locale) {

		try {
			List<RestaurantType> rtypes = em.createNamedQuery(RestaurantType.FIND_ALL_RESTAURANT_TYPE).getResultList();

			return mapper.writeValueAsString(rtypes);
		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@Owner
	@PUT
	@Path("/{idRestaurant:[0-9][0-9]*}/restaurant-type/{idRestaurantType:[0-9][0-9]*}")
	public Response addRestaurantTypeToRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idRestaurant") Short idRestaurant, @PathParam("idRestaurantType") Short idRestaurantType) {

		log.log(Level.INFO, "Adding restaurant type " + idRestaurantType + " to restaurant " + idRestaurant);

		try {
			Restaurant restaurant = em.find(Restaurant.class, idRestaurant);
			restaurant.getRestaurantTypes().add(em.find(RestaurantType.class, idRestaurantType));

			em.merge(restaurant);
			em.flush();

			String answer = LocaleResource.getString(locale, "restaurant.restauranttype.added", idRestaurantType,
					idRestaurant);
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@Owner
	@DELETE
	@Path("/{idRestaurant:[0-9][0-9]*}/restaurant-type/{idRestaurantType:[0-9][0-9]*}")
	public Response removeRestaurantTypeToRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idRestaurant") Short idRestaurant, @PathParam("idRestaurantType") Short idRestaurantType) {

		log.log(Level.INFO, "Removing restaurant type " + idRestaurantType + " to restaurant " + idRestaurant);

		try {
			Restaurant restaurant = em.find(Restaurant.class, idRestaurant);
			restaurant.getRestaurantTypes().remove(em.find(RestaurantType.class, idRestaurantType));

			em.merge(restaurant);
			em.flush();

			String answer = LocaleResource.getString(locale, "restaurant.restauranttype.removed", idRestaurantType,
					idRestaurant);
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

}