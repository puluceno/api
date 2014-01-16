package br.com.redefood.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
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
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.redefood.annotations.Owner;
import br.com.redefood.annotations.OwnerOrManager;
import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Address;
import br.com.redefood.model.Board;
import br.com.redefood.model.City;
import br.com.redefood.model.Configuration;
import br.com.redefood.model.DeliveryArea;
import br.com.redefood.model.Neighborhood;
import br.com.redefood.model.OpenTime;
import br.com.redefood.model.PaymentMethod;
import br.com.redefood.model.Printer;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryMessages;
import br.com.redefood.model.SubsidiaryModule;
import br.com.redefood.model.User;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * This class produces a RESTful service to read the contents of the Cities
 * table.
 */
@Path("/subsidiary")
@Stateless
public class SubsidiaryResource extends HibernateMapper {
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;
	@Inject
	private SquareResource squareResource;

	private static ObjectMapper mapper = HibernateMapper.getMapper();

	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String lookupSubsidiaryById(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("field") String field) {

		List<String> fields = null;
		if (field != null) {
			fields = Arrays.asList(field.toLowerCase().split(","));
		}

		Subsidiary subsidiary = null;

		try {
			subsidiary = em.find(Subsidiary.class, idSubsidiary);
			if (subsidiary == null)
				throw new Exception("subsidiary not found");
			if (fields == null || fields.isEmpty()) {

				if (subsidiary.getAddress() != null) {
					Hibernate.initialize(subsidiary.getAddress());
					Hibernate.initialize(subsidiary.getAddress().getCity());
					Hibernate.initialize(subsidiary.getAddress().getNeighborhood());
				}

				if (subsidiary.getPaymentMethod() != null) {
					Hibernate.initialize(subsidiary.getPaymentMethod());
				}

				if (subsidiary.getConfiguration() != null) {
					Hibernate.initialize(subsidiary.getConfiguration());
				}

				if (subsidiary.getOrdersList() != null) {
					Hibernate.initialize(subsidiary.getOrderTypes());
				}

				if (subsidiary.getSubsidiaryModules() != null) {
					for (SubsidiaryModule subsidiaryModule : subsidiary.getSubsidiaryModules()) {
						Hibernate.initialize(subsidiaryModule.getModule());
					}
				}
				if (subsidiary.getOpenTime() != null) {
					for (OpenTime openTime : subsidiary.getOpenTime()) {
						Hibernate.initialize(openTime.getDayOfWeek());
					}
				}
				if (subsidiary.getDeliveryAreas() != null) {
					for (DeliveryArea deliveryArea : subsidiary.getDeliveryAreas()) {
						Hibernate.initialize(deliveryArea.getNeighborhood());
					}
				}
				if (subsidiary.getMessages() != null) {
					Hibernate.initialize(subsidiary.getMessages());
				}

				if (subsidiary.getCitiesAttended() != null) {
					Hibernate.initialize(subsidiary.getCitiesAttended());
				}

			} else {
				if (fields.contains("address")) {
					Hibernate.initialize(subsidiary.getAddress());
					Hibernate.initialize(subsidiary.getAddress().getCity());
					Hibernate.initialize(subsidiary.getAddress().getNeighborhood());
				}
				if (fields.contains("paymentmethod")) {
					Hibernate.initialize(subsidiary.getPaymentMethod());
				}

				if (fields.contains("configuration")) {
					Hibernate.initialize(subsidiary.getConfiguration());
				}

				if (fields.contains("ordertypes")) {
					Hibernate.initialize(subsidiary.getOrderTypes());
				}

				if (fields.contains("subsidiarymodule")) {
					for (SubsidiaryModule subsidiaryModule : subsidiary.getSubsidiaryModules()) {
						Hibernate.initialize(subsidiaryModule.getModule());
					}
				}
				if (fields.contains("opentime")) {
					for (OpenTime openTime2 : subsidiary.getOpenTime()) {
						Hibernate.initialize(openTime2.getDayOfWeek());
					}
				}
				if (fields.contains("deliveryareas")) {
					for (DeliveryArea deliveryArea : subsidiary.getDeliveryAreas()) {
						Hibernate.initialize(deliveryArea.getNeighborhood());
					}
				}
				if (fields.contains("messages")) {
					Hibernate.initialize(subsidiary.getMessages());
				}
			}

			subsidiary.setSubsidiaryOpen(isSubsidiaryOpen(locale, idSubsidiary));
			subsidiary.setAvgRating(squareResource.calculateSubsidiaryAvgRating(subsidiary.getId()));

			return mapper.writeValueAsString(subsidiary);

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale, idSubsidiary.toString()).getEntity().toString();
		}
	}

	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/info")
	@Produces("application/json;charset=UTF8")
	public String lookupSubsidiaryInfoById(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			Subsidiary find = em.find(Subsidiary.class, idSubsidiary);
			Hibernate.initialize(find.getAddress());
			Hibernate.initialize(find.getAddress().getCity());
			Hibernate.initialize(find.getAddress().getNeighborhood());

			return mapper.writeValueAsString(find);

		} catch (Exception e) {
			return eh.orderExceptionHandlerString(e, locale, null);
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/delivery-area")
	@Produces("application/json;charset=UTF8")
	public String findSubsidiaryDeliveryAreas(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			List<DeliveryArea> resultList = em.createNamedQuery(DeliveryArea.FIND_DELIVERY_AREA_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			for (DeliveryArea deliveryArea : resultList) {
				Hibernate.initialize(deliveryArea.getNeighborhood());
			}

			return mapper.writeValueAsString(resultList);
		} catch (Exception e) {
			return eh.orderExceptionHandlerString(e, locale, null);
		}
	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/available-neighborhoods")
	@Produces("application/json;charset=UTF8")
	public String findAvailableSubsidiaryDeliveryAreas(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			List<Neighborhood> resultList = em.createNamedQuery(Neighborhood.FIND_AVAILABLE_NEIGHBORHOOD_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			return mapper.writeValueAsString(resultList);
		} catch (Exception e) {
			return eh.orderExceptionHandlerString(e, locale, null);
		}
	}

	@OwnerOrManager
	@POST
	@Path("/{idSubsidiary:[0-9][0-9]*}/delivery-area")
	@Consumes("application/json")
	public Response addDeliveryArea(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, DeliveryArea deliveryArea) {

		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

		try {
			deliveryArea.setIdSubsidiary(subsidiary);
			em.persist(deliveryArea);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.delivery.created", deliveryArea
					.getNeighborhood().getId(), idSubsidiary);
			log.log(Level.INFO, answer);

			String asString = mapper.writeValueAsString(deliveryArea);
			return Response.status(201).entity(asString).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(new Exception("add delivery"), locale, subsidiary.getName());
		}
	}

	@OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/delivery-area/{idDeliveryArea:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editDeliveryArea(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idDeliveryArea") Integer idDeliveryArea,
			DeliveryArea deliveryAreaTax) {

		try {
			if (deliveryAreaTax.getTax() < 0)
				throw new Exception("invalid tax");

			DeliveryArea deliveryArea = em.find(DeliveryArea.class, idDeliveryArea);
			if (deliveryArea == null)
				throw new Exception("delivery area does not exist");
			deliveryArea.setTax(deliveryAreaTax.getTax());
			em.merge(deliveryArea);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.delivery.updated", deliveryArea
					.getNeighborhood().getName(), idSubsidiary);
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.deliveryExceptionHandler(e, locale, idDeliveryArea.toString(), idSubsidiary.toString());
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/{idSubsidiary:[0-9][0-9]*}/delivery-area/{idDeliveryArea:[0-9][0-9]*}")
	public Response deleteDeliveryArea(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idDeliveryArea") Integer idDeliveryArea) {

		DeliveryArea toDelete = null;

		try {
			toDelete = em.find(DeliveryArea.class, idDeliveryArea);

		} catch (Exception e) {
			String answer = LocaleResource.getString(locale, "exception.subsidiary.delivery.found", idDeliveryArea,
					idSubsidiary);
			log.log(Level.SEVERE, answer);
			return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
		}

		try {
			em.remove(toDelete);
			em.flush();
			String answer = LocaleResource.getString(locale, "subsidiary.delivery.removed", toDelete.getNeighborhood()
					.getName(), idSubsidiary);
			log.log(Level.INFO, answer);
			return RedeFoodAnswerGenerator.generateSuccessAnswer(200, answer);

		} catch (Exception e) {
			String answer = LocaleResource.getString(locale, "exception.subsidiary.delivery.remove", idDeliveryArea,
					idSubsidiary);
			log.log(Level.SEVERE, answer);
			return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
		}
	}

	/**
	 * 
	 * @param idSubsidiary
	 * @param token
	 * @param subsidiary
	 * @return
	 */
	@Owner
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editSubsidiaryAndRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, Subsidiary subsidiary) {

		try {
			Subsidiary toMerge = validateEdit(subsidiary, idSubsidiary);
			for (OpenTime openTime : toMerge.getOpenTime()) {
				openTime.setSubsidiary(toMerge);
			}
			em.merge(toMerge);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.updated", toMerge.getName());
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale);
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

		if (subsidiary.getName().length() < 4)
			throw new Exception("name length");

		if (subsidiary.getAccountList() != null) {
			toMerge.setAccountList(subsidiary.getAccountList());
		}
		if (subsidiary.getAddress() != null) {
			validateAddress(subsidiary.getAddress());
			toMerge.setAddress(subsidiary.getAddress());
		}

		if (subsidiary.getEmployees() != null) {
			toMerge.setEmployees(subsidiary.getEmployees());
		}

		toMerge.setDescription(subsidiary.getDescription());
		toMerge.setHeadOffice(subsidiary.getHeadOffice());
		toMerge.setImage(subsidiary.getImage());
		toMerge.setImageBanner(subsidiary.getImageBanner());
		toMerge.setImageLogo(subsidiary.getImageLogo());
		toMerge.setMinOrder(subsidiary.getMinOrder());
		toMerge.setName(subsidiary.getName());
		toMerge.setTrackOrder(subsidiary.isTrackOrder());
		toMerge.setGa(subsidiary.getGa());
		toMerge.setMetaDescription(subsidiary.getMetaDescription());
		toMerge.setMetaKeyWords(subsidiary.getMetaKeyWords());
		toMerge.setPaused(subsidiary.getPaused());

		if (subsidiary.getPaymentMethod() != null) {
			toMerge.setPaymentMethod(subsidiary.getPaymentMethod());
		}
		if (subsidiary.getOpenTime() != null) {
			toMerge.setOpenTime(subsidiary.getOpenTime());
		}
		if (subsidiary.getSubsidiaryhasPromotionList() != null) {
			toMerge.setSubsidiaryhasPromotionList(subsidiary.getSubsidiaryhasPromotionList());
		}
		if (subsidiary.getContact() != null) {
			toMerge.setContact(subsidiary.getContact());
		}

		if (subsidiary.getAvgDeliveryTime() != null) {
			toMerge.setAvgDeliveryTime(subsidiary.getAvgDeliveryTime());
		}

		return toMerge;
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
	@Path("/{idSubsidiary:[0-9][0-9]*}/open-time")
	@Produces("application/json;charset=UTF8")
	public String findSubsidiaryBusinessHour(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			List<OpenTime> resultList = em.createNamedQuery(OpenTime.FIND_OPEN_TIME_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			for (OpenTime openTime : resultList) {
				Hibernate.initialize(openTime.getDayOfWeek());
			}

			return mapper.writeValueAsString(resultList);

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale).toString();
		}
	}

	@Owner
	@POST
	@Path("/{idSubsidiary:[0-9][0-9]*}/open-time")
	@Consumes("application/json")
	public Response addOpenTime(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			OpenTime openTime) {

		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

		try {
			validateOpenTime(openTime);
			openTime.setSubsidiary(subsidiary);
			em.persist(openTime);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.opentime.created", openTime.toString(),
					idSubsidiary);
			log.log(Level.INFO, answer);
			return RedeFoodAnswerGenerator.generateSuccessPOST(openTime.getId(), 201);

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale, "", openTime.getOpen(), openTime.getClose());
		}
	}

	private void validateOpenTime(OpenTime openTime) throws Exception {
		int openH = Integer.parseInt(openTime.getOpen().substring(0, 2));
		if (openH < 0 || openH > 23)
			throw new Exception("open hour");
		int openM = Integer.parseInt(openTime.getOpen().substring(3, 5));
		if (openM < 0 || openM > 59)
			throw new Exception("open minute");
		int closeH = Integer.parseInt(openTime.getClose().substring(0, 2));
		if (closeH < 0 || closeH > 23)
			throw new Exception("close hour");
		int closeM = Integer.parseInt(openTime.getClose().substring(3, 5));
		if (closeM < 0 || closeM > 59)
			throw new Exception("close minute");
	}

	@OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/open-time/{idOpenTime:[0-9][0-9]*}")
	public Response editOpenTime(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idOpenTime") Integer idOpenTime, OpenTime openTimeHours) {

		OpenTime openTime = em.find(OpenTime.class, idOpenTime);

		try {
			validateOpenTime(openTimeHours);
			openTime.setClose(openTimeHours.getClose());
			openTime.setOpen(openTimeHours.getOpen());
			openTime.setLocalTime(openTimeHours.getLocalOpenTime() != null ? openTimeHours.getLocalOpenTime() : false);
			em.merge(openTime);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.opentime.updated", openTime.toString(),
					idSubsidiary);
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale, openTime.getOpen(), openTime.getClose());
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/{idSubsidiary:[0-9][0-9]*}/open-time/{idOpenTime:[0-9][0-9]*}")
	public Response deleteOpenTime(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idOpenTime") Integer idOpenTime) {

		OpenTime toDelete = null;

		try {
			toDelete = em.find(OpenTime.class, idOpenTime);

		} catch (Exception e) {
			String answer = LocaleResource.getString(locale, "exception.subsidiary.opentime.find", idOpenTime,
					idSubsidiary);
			log.log(Level.SEVERE, answer);
			return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
		}

		try {
			em.remove(toDelete);
			em.flush();
			String answer = LocaleResource.getString(locale, "subsidiary.opentime.removed", toDelete.toString(),
					idSubsidiary);
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale);
		}
	}

	@Securable
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/payment-method")
	@Produces("application/json;charset=UTF8")
	public String findPaymentMethod(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			Hibernate.initialize(subsidiary.getPaymentMethod());
			return mapper.writeValueAsString(subsidiary.getPaymentMethod());

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale).toString();
		}

	}

	@SuppressWarnings("unchecked")
	// @Securable
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/available-payment-method")
	@Produces("application/json;charset=UTF8")
	public String availablePaymentMethod(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			List<PaymentMethod> paymentMethods = em.createNamedQuery(PaymentMethod.FIND_ALL).getResultList();
			List<PaymentMethod> aux = new ArrayList<PaymentMethod>();
			Subsidiary find = em.find(Subsidiary.class, idSubsidiary);
			for (PaymentMethod paymentMethod : paymentMethods)
				if (!find.getPaymentMethod().contains(paymentMethod)) {
					aux.add(paymentMethod);
				}

			return mapper.writeValueAsString(aux);
		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale).toString();
		}
	}

	@OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/payment-method/{idPaymentMethod:[0-9][0-9]*}")
	public Response addPaymentMethod(@HeaderParam("locale") String locale, @HeaderParam("token") String token,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idPaymentMethod") Short idPaymentMethod) {

		log.log(Level.INFO, "Adding payment method to subsidiary " + idSubsidiary);
		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
		subsidiary.getPaymentMethod().add(em.find(PaymentMethod.class, idPaymentMethod));

		try {
			em.merge(subsidiary);
			em.flush();

			String answer = LocaleResource.getProperty(locale).getProperty("subsidiary.payment.created");
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale);
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/{idSubsidiary:[0-9][0-9]*}/payment-method/{idPaymentMethod:[0-9][0-9]*}")
	public Response deletePaymentMethod(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idPaymentMethod") Short idPaymentMethod) {

		log.log(Level.INFO, "Removing payment method to subsidiary " + idSubsidiary);
		Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
		subsidiary.getPaymentMethod().remove(em.find(PaymentMethod.class, idPaymentMethod));

		try {
			em.merge(subsidiary);
			em.flush();

			String answer = LocaleResource.getProperty(locale).getProperty("subsidiary.payment.removed");
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale);
		}
	}

	@Securable
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/configuration")
	@Produces("application/json;charset=UTF8")
	public String findSubsidiaryConfiguration(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

			if (subsidiary.getConfiguration() != null) {
				Hibernate.initialize(subsidiary.getConfiguration());
				return mapper.writeValueAsString(subsidiary.getConfiguration());
			} else
				return RedeFoodAnswerGenerator.generateErrorAnswerString(404, "No configuration found.");

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale).toString();
		}
	}

	@OwnerOrManager
	@Securable
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/configuration")
	@Consumes("application/json")
	public Response createEditSubsidiaryConfiguration(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, Configuration config) {

		try {
			if (config.getPrinters() != null) {
				for (Printer printer : config.getPrinters()) {
					printer.setConfiguration(config);
				}
			}

			if (!config.isReceiveOrdersByMail()) {
				config.setReceiveOrdersMailAddress(null);
			}

			em.merge(config);
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.configuration.updated", idSubsidiary);

			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale);
		}

	}

	@OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/configuration/printer/{idPrinter:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editSubsidiaryPrinter(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idPrinter") Short idPrinter, Printer printer) {

		log.log(Level.INFO, "Updating configurations for printer " + idPrinter + " that belongs to subsidiary "
				+ idSubsidiary);

		try {
			validatePrinter(printer);
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			printer.setConfiguration(subsidiary.getConfiguration());
			printer.setId(idPrinter);

			em.merge(printer);
			em.flush();

			String answer = LocaleResource.getProperty(locale).getProperty("subsidiary.configuration.printer.updated");
			log.log(Level.INFO, answer);
			return Response.status(200).build();

		} catch (Exception e) {
			return eh.printExceptionHandlerResponse(e, locale);
		}
	}

	@OwnerOrManager
	@POST
	@Path("/{idSubsidiary:[0-9][0-9]*}/configuration/printer")
	@Consumes("application/json")
	public Response addPrinter(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			Printer printer) {

		log.log(Level.INFO, "Adding printer for subsidiary " + idSubsidiary);

		try {
			validatePrinter(printer);

			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			printer.setConfiguration(subsidiary.getConfiguration());

			em.persist(printer);
			em.flush();

			String answer = LocaleResource.getProperty(locale).getProperty("subsidiary.configuration.printer.added");
			log.log(Level.INFO, answer);

			String jsonReturn = mapper.writeValueAsString(printer);
			return Response.status(201).entity(jsonReturn).build();

		} catch (Exception e) {
			return eh.printExceptionHandlerResponse(e, locale);
		}
	}

	private void validatePrinter(Printer printer) throws Exception {
		if (printer.getName().length() < 1)
			throw new Exception("no printer name");
		if (printer.getIp().length() < 7)
			throw new Exception("no printer ip");
	}

	@OwnerOrManager
	@DELETE
	@Path("/{idSubsidiary:[0-9][0-9]*}/configuration/printer/{idPrinter:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response removePrinter(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idPrinter") Short idPrinter) {

		log.log(Level.INFO, "Removing printer " + idPrinter + " for subsidiary " + idSubsidiary);

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			Printer printer = em.find(Printer.class, idPrinter);
			if (!subsidiary.getConfiguration().getPrinters().contains(printer))
				throw new Exception("printer belong");

			em.remove(printer);
			em.flush();

			String answer = LocaleResource.getProperty(locale).getProperty("subsidiary.configuration.printer.removed");
			log.log(Level.INFO, answer);
			return Response.status(201).build();

		} catch (Exception e) {
			return eh.printExceptionHandlerResponse(e, locale);
		}
	}

	@SuppressWarnings("unchecked")
	@OwnerOrManager
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/clients")
	@Produces("application/json;charset=UTF8")
	public String findUsersFromSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {

			List<User> users = em.createNamedQuery(User.FIND_USER_BY_ORDER_AT_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			return mapper.writeValueAsString(users);

		} catch (Exception e) {
			return eh.subsidiaryExceptionHandler(e, locale).toString();
		}
	}

	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/messages")
	public String findSubsidiaryMessages(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {
		try {

			return mapper.writeValueAsString(em.createNamedQuery(SubsidiaryMessages.FIND_MESSAGES_BY_SUBSIDIARY_ID)
					.setParameter("idSubsidiary", idSubsidiary).getResultList());

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/messages")
	public Response editSubsidiaryMessages(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, List<SubsidiaryMessages> messages) {

		try {
			for (SubsidiaryMessages subsidiaryMessages : messages) {
				Integer id = subsidiaryMessages.getId();
				SubsidiaryMessages find = em.find(SubsidiaryMessages.class, id);
				find.setMessage(subsidiaryMessages.getMessage());
				em.merge(find);
			}
			em.flush();

			String answer = LocaleResource.getString(locale, "subsidiary.messages.updated", idSubsidiary);
			log.log(Level.INFO, answer);
			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @Securable
	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/boards")
	@Produces("application/json;charset=UTF8")
	public String findBoards(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary) {

		try {

			return mapper.writeValueAsString(em.createNamedQuery(Board.FIND_ALL_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList());

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	// @OwnerOrManager
	@POST
	@Path("/{idSubsidiary:[0-9][0-9]*}/boards")
	public Response addBoardToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			log.log(Level.INFO, "Adding board to subsidiary " + idSubsidiary);

			Short number = (Short) em.createNamedQuery(Board.FIND_MAX_NUMBER_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getSingleResult();
			if (number == null) {
				number = new Short("1");
			} else {
				++number;
			}

			Board b = new Board(number, em.find(Subsidiary.class, idSubsidiary));
			em.persist(b);
			em.flush();

			return Response.status(201).entity(mapper.writeValueAsString(b)).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @OwnerOrManager
	@DELETE
	@Path("/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}")
	public Response removeBoardFromSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBoard") Integer idBoard) {

		try {
			log.log(Level.INFO, "Removing board " + idBoard + " from subsidiary " + idSubsidiary);

			em.remove(em.find(Board.class, idBoard));
			em.flush();

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editBoardFromSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBoard") Integer idBoard, Board board) {

		try {
			log.log(Level.INFO, "Updating board " + idBoard + " from subsidiary " + idSubsidiary);

			Board b = em.find(Board.class, idBoard);
			b.setNumber(board.getNumber());

			em.merge(b);
			em.flush();

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@Owner
	@POST
	@Path("/{idSubsidiary:[0-9][0-9]*}/filled-wizard")
	public Response finishWizard(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary) {
		try {
			log.log(Level.INFO, "Finishing Wizard to subsidiary " + idSubsidiary);

			Subsidiary sub = em.find(Subsidiary.class, idSubsidiary);
			sub.setFilledWizard(true);

			em.merge(sub);
			em.flush();

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@OwnerOrManager
	@PUT
	@Path("/{idSubsidiary:[0-9][0-9]*}/cities-attended")
	public Response editCitiesAttended(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, List<City> citiesAttended) {

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

			subsidiary.setCitiesAttended(citiesAttended);

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@GET
	@Path("/{idSubsidiary:[0-9][0-9]*}/isopen")
	public Boolean isSubsidiaryOpen(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			OpenTime openTime = (OpenTime) em.createNamedQuery(OpenTime.FIND_OPEN_TIME_BY_SUBSIDIARY_AND_DAY)
					.setParameter("idSubsidiary", idSubsidiary)
					.setParameter("dayOfWeek", (short) Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
					.setParameter("localOpenTime", false).getSingleResult();

			DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm:ss");

			if (!openTime.getLocalOpenTime()) {
				DateTime openHour = formatter.parseDateTime(openTime.getOpen());
				MutableDateTime open = new MutableDateTime();
				open.setTime(openHour.getHourOfDay(), openHour.getMinuteOfHour(), openHour.getSecondOfMinute(), 0);

				DateTime closeHour = formatter.parseDateTime(openTime.getClose());
				MutableDateTime close = new MutableDateTime();
				close.setTime(closeHour.getHourOfDay(), closeHour.getMinuteOfHour(), closeHour.getSecondOfMinute(), 999);

				if (close.getHourOfDay() == open.getHourOfDay() || close.getHourOfDay() < open.getHourOfDay()) {
					close.addDays(1);
				}
				return open.isBeforeNow() && close.isAfterNow();

			}

			return false;
		} catch (Exception e) {
			return false;
		}
	}
}