package br.com.redefood.rest;

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

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.redefood.annotations.OwnerOrManager;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Beverage;
import br.com.redefood.model.BeverageType;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.service.FileUploadService;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}")
public class BeverageResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@SuppressWarnings("unchecked")
	@GET
	@Path("/beverage-type")
	@Produces("application/json;charset=UTF8")
	public String findBeverageTypesBySubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {

			List<BeverageType> beverages = em.createNamedQuery(BeverageType.FIND_JUST_BEVERAGETYPE_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			return mapper.writeValueAsString(beverages);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@OwnerOrManager
	@POST
	@Path("/beverage-type")
	@Consumes("application/json")
	public Response createBeverageTypeToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, BeverageType beverageType) {

		log.log(Level.INFO, "Creating BeverageType " + beverageType.getName() + " to subsidiary " + idSubsidiary);

		try {
			validateNewBeverageType(beverageType);

			Integer maxOrder = (Integer) em.createNamedQuery(BeverageType.FIND_MAX_EXHIBITIONORDER)
					.setParameter("idSubsidiary", idSubsidiary).getSingleResult();
			if (maxOrder == null) {
				maxOrder = new Integer(0);
			}
			beverageType.setExhibitionOrder((1 + maxOrder));

			beverageType.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));

			em.persist(beverageType);
			em.flush();

			log.log(Level.INFO, "BeverageType " + beverageType.getName() + " created to subsidiary " + idSubsidiary);

			return RedeFoodAnswerGenerator.generateSuccessPOSTExhibitionOrder(beverageType.getId(),
					beverageType.getExhibitionOrder(), 201);

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	private void validateNewBeverageType(BeverageType beverageType) throws Exception {
		if (beverageType.getName().length() > 100)
			throw new Exception("maximun name length");
		if (beverageType.getDescription().length() > 300)
			throw new Exception("maximun description length");
	}

	@OwnerOrManager
	@PUT
	@Path("/beverage-type/{idBeverageType:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editBeverageTypeToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBeverageType") Short idBeverageType,
			BeverageType beverageType) {

		log.log(Level.INFO, "Editing BeverageType " + beverageType.getName() + " to subsidiary " + idSubsidiary);

		try {
			BeverageType bt = em.find(BeverageType.class, idBeverageType);

			bt.setDescription(beverageType.getDescription());
			bt.setExhibitionOrder(beverageType.getExhibitionOrder());
			bt.setName(beverageType.getName());

			em.merge(bt);
			em.flush();

			log.log(Level.INFO, "BeverageType " + idBeverageType + " updated to subsidiary " + idSubsidiary);

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/beverage-type/{idBeverageType:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response removeBeverageTypeFromSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBeverageType") Short idBeverageType) {

		log.log(Level.INFO, "Removing BeverageType " + idBeverageType + " to subsidiary " + idSubsidiary);

		try {

			BeverageType bt = em.find(BeverageType.class, idBeverageType);
			if (bt == null)
				throw new Exception("wrong beverage id");

			bt.setActive(false);
			em.merge(bt);
			em.flush();

			log.log(Level.INFO, "BeverageType " + idBeverageType + " removed from subsidiary " + idSubsidiary);

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}

	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/beverage")
	@Produces("application/json;charset=UTF8")
	public String findAllBeverageByRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("localOnly") Boolean localOnly) {

		try {
			List<BeverageType> beverages = null;

			if (localOnly != null) {
				beverages = em.createNamedQuery(BeverageType.FIND_LOCAL_OR_DELIVERY__BY_RESTAURANT)
						.setParameter("idSubsidiary", idSubsidiary).setParameter("localOnly", localOnly)
						.getResultList();
			} else {
				beverages = em.createNamedQuery(BeverageType.FIND_ALL_BY_RESTAURANT)
						.setParameter("idSubsidiary", idSubsidiary).getResultList();
			}

			return mapper.writeValueAsString(beverages);
		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@OwnerOrManager
	@POST
	@Path("/beverage-type/{idBeverageType:[0-9][0-9]*}/beverage")
	@Consumes("application/json")
	public Response createBeverageToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBeverageType") Short idBeverageType,
			Beverage beverage) {

		log.log(Level.INFO, "Creating Beverage " + beverage.getName() + " to subsidiary " + idSubsidiary);

		try {
			validateNewBeverage(beverage);

			if (beverage.getExhibitionOrder() == null) {
				Integer maxOrder = (Integer) em.createNamedQuery(Beverage.FIND_MAX_EXHIBITIONORDER)
						.setParameter("idSubsidiary", idSubsidiary).setParameter("idBeverageType", idBeverageType)
						.getSingleResult();
				if (maxOrder == null) {
					maxOrder = new Integer(0);
				}
				beverage.setExhibitionOrder((1 + maxOrder));
			}

			beverage.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));
			beverage.setBeverageType(em.find(BeverageType.class, idBeverageType));

			em.persist(beverage);
			em.flush();

			log.log(Level.INFO, "Beverage " + beverage.getName() + " created to subsidiary " + idSubsidiary);

			return RedeFoodAnswerGenerator.generateSuccessPOSTwithImageExhibitionOrder(beverage.getId(),
					beverage.getImage(), beverage.getExhibitionOrder(), 201);

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	private void validateNewBeverage(Beverage beverage) throws Exception {
		if (beverage.getName().length() > 50)
			throw new Exception("maximun name length");
		if (beverage.getDescription().length() > 200)
			throw new Exception("maximun description length");
		if (beverage.getPrice() == null || beverage.getPrice() < 0.0)
			throw new Exception("invalid price");

	}

	@OwnerOrManager
	@PUT
	@Path("/beverage-type/{idBeverageType:[0-9][0-9]*}/beverage/{idBeverage:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editBeverageToRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBeverage") Short idBeverage,
			@PathParam("idBeverageType") Short idBeverageType, Beverage subBeverage) {

		log.log(Level.INFO, "Updating Beverage " + subBeverage.getName() + " to subsidiary " + idSubsidiary);

		try {

			Beverage beverage = em.find(Beverage.class, idBeverage);
			if (beverage == null)
				throw new Exception("beverage not found");

			subBeverage.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));
			subBeverage.setBeverageType(em.find(BeverageType.class, idBeverageType));

			em.merge(subBeverage);
			em.flush();

			log.log(Level.INFO, "Beverage " + beverage.getName() + " updated to subsidiary " + idSubsidiary);

			return Response.status(201).build();

		} catch (Exception e) {
			return eh.beverageExceptionHandler(e, locale, idBeverage.toString());
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/beverage-type/{idBeverageType:[0-9][0-9]*}/beverage/{idBeverage:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response removeBeverageFromRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBeverage") Short idBeverage,
			@PathParam("idBeverageType") Short idBeverageType) {

		log.log(Level.INFO, "Removing Beverage " + idBeverage + " to subsidiary " + idSubsidiary);

		try {

			Beverage b = em.find(Beverage.class, idBeverage);
			if (b == null)
				throw new Exception("wrong beverage id");
			b.setActive(false);

			em.merge(b);
			em.flush();

			log.log(Level.INFO, "Beverage " + idBeverage + " removed from subsidiary " + idSubsidiary);

			return Response.status(200).build();

		} catch (Exception e) {
			return eh.beverageExceptionHandler(e, locale, idBeverage.toString());
		}
	}

	@OwnerOrManager
	@POST
	@Path("/beverage/{idBeverage:[0-9][0-9]*}/photo")
	@Consumes("multipart/form-data")
	public Response addBeveragePhoto(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBeverage") Short idBeverage,
			MultipartFormDataInput photo) {

		try {
			Beverage beverage = em.find(Beverage.class, idBeverage);

			if (beverage == null) {
				String answer = LocaleResource.getString(locale, "exception.restaurant.beverage.found", idBeverage);
				log.log(Level.INFO, answer);
				return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
			}

			log.log(Level.INFO, "Uploading photo to beverage " + beverage.getId());

			if (beverage.getImage() != null && !beverage.getImage().contains("default")) {
				FileUploadService.deleteOldFile(beverage.getImage());
			}

			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

			String uploadFile = FileUploadService.uploadFile("restaurant/"
					+ subsidiary.getRestaurant().getIdRestaurant().toString() + "/subsidiary/" + idSubsidiary + "/"
					+ beverage.getClass().getSimpleName().toLowerCase(), idBeverage.toString(), photo);
			if (uploadFile.contains("error"))
				throw new Exception("file error");

			beverage.setImage(uploadFile);

			em.merge(beverage);
			em.flush();

			String answer = LocaleResource.getString(locale, "restaurant.meal.updated", beverage.getName());
			log.log(Level.INFO, answer);
			return RedeFoodAnswerGenerator.generateSuccessAnswerWithoutSuccess(200, uploadFile);

		} catch (Exception e) {
			return eh.beverageExceptionHandler(e, locale);
		}
	}
}
