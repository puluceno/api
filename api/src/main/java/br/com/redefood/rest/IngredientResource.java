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
import javax.ws.rs.core.Response;

import br.com.redefood.annotations.OwnerOrManager;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Ingredient;
import br.com.redefood.model.IngredientType;
import br.com.redefood.model.Meal;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.RedeFoodAnswerGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}")
@Stateless
public class IngredientResource extends HibernateMapper {

	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@SuppressWarnings("unchecked")
	@GET
	@Path("/ingredient-type")
	@Produces("application/json;charset=UTF8")
	public String findIngredientTypesBySubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			List<IngredientType> ingredients = em
					.createNamedQuery(IngredientType.FIND_INGREDIENT_TYPE_BY_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			return mapper.writeValueAsString(ingredients);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@OwnerOrManager
	@POST
	@Path("/ingredient-type")
	@Consumes("application/json")
	public Response createIngredientTypeToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, IngredientType ingredientType) {

		log.log(Level.INFO, "Creating IngredientType " + ingredientType.getName() + " to subsidiary " + idSubsidiary);

		try {
			validateNewIngredientType(ingredientType);

			ingredientType.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));

			em.persist(ingredientType);
			em.flush();

			log.log(Level.INFO, "IngredientType " + ingredientType.getName() + " created to subsidiary " + idSubsidiary);

			return RedeFoodAnswerGenerator.generateSuccessPOST(ingredientType.getId(), 201);

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	private void validateNewIngredientType(IngredientType ingredientType) throws Exception {
		if (ingredientType.getName().length() > 60)
			throw new Exception("maximun name length");
		if (ingredientType.getDescription().length() > 300)
			throw new Exception("maximun description length");
	}

	@OwnerOrManager
	@PUT
	@Path("/ingredient-type/{idIngredientType:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editIngredientTypeToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idIngredientType") Short idIngredientType,
			IngredientType ingredientType) {

		log.log(Level.INFO, "Editing IngredientType " + ingredientType.getName() + " to subsidiary " + idSubsidiary);

		try {
			IngredientType it = em.find(IngredientType.class, idIngredientType);

			it.setDescription(ingredientType.getDescription());
			it.setName(ingredientType.getName());

			em.merge(it);
			em.flush();

			log.log(Level.INFO, "IngredientType " + idIngredientType + " updated to subsidiary " + idSubsidiary);

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/ingredient-type/{idIngredientType:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response removeIngredientTypeFromSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idIngredientType") Short idIngredientType) {

		log.log(Level.INFO, "Removing IngredientType " + idIngredientType + " to subsidiary " + idSubsidiary);

		try {

			IngredientType it = em.find(IngredientType.class, idIngredientType);
			it.setActive(false);

			em.merge(it);
			em.flush();

			log.log(Level.INFO, "IngredientType " + idIngredientType + " removed from subsidiary " + idSubsidiary);

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}

	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/ingredient")
	@Produces("application/json;charset=UTF8")
	public String findAllIngredientByRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			List<IngredientType> ingredients = em.createNamedQuery(IngredientType.FIND_INGREDIENT_TYPES_BY_RESTAURANT)
					.setParameter("idSubsidiary", idSubsidiary).getResultList();

			return mapper.writeValueAsString(ingredients);
		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@OwnerOrManager
	@POST
	@Path("/ingredient-type/{idIngredientType:[0-9][0-9]*}/ingredient")
	@Consumes("application/json")
	public Response createIngredientToSubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idIngredientType") Short idIngredientType,
			Ingredient ingredient) {

		log.log(Level.INFO, "Creating Ingredient " + ingredient.getName() + " to subsidiary " + idSubsidiary);

		try {
			validateNewIngredient(ingredient);

			ingredient.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));
			ingredient.setIngredientType(em.find(IngredientType.class, idIngredientType));

			em.persist(ingredient);
			em.flush();

			log.log(Level.INFO, "Ingredient " + ingredient.getName() + " created to subsidiary " + idSubsidiary);

			return RedeFoodAnswerGenerator.generateSuccessPOST(ingredient.getId(), 201);

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@GET
	@Path("/ingredient-type/{idIngredientType:[0-9][0-9]*}/ingredient")
	public Response copyMealsToIngredients(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idIngredientType") Short idIngredientType) {

		try {

			log.log(Level.INFO, "Copying active meals to ingredient type " + idIngredientType + " to subsidiary "
					+ idSubsidiary);

			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

			for (Meal meal : subsidiary.getMeals())
				if (meal.getActive() && meal.getMealType().isActive()) {
					Ingredient i = new Ingredient();
					i.setName(meal.getName());
					i.setDescription(meal.getDescription());
					i.setIngredientType(em.find(IngredientType.class, idIngredientType));
					i.setSubsidiary(subsidiary);

					em.persist(i);
					em.flush();
				}
			return Response.status(200).entity("OK").build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	private void validateNewIngredient(Ingredient ingredient) throws Exception {
		if (ingredient.getName().length() > 100)
			throw new Exception("maximun name length");
		if (ingredient.getDescription().length() > 200)
			throw new Exception("maximun description length");
	}

	@OwnerOrManager
	@PUT
	@Path("/ingredient-type/{idIngredientType:[0-9][0-9]*}/ingredient/{idIngredient:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response editIngredientToRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idIngredient") Integer idIngredient,
			@PathParam("idIngredientType") Short idIngredientType, Ingredient subIngredient) {

		log.log(Level.INFO, "Updating Ingredient " + subIngredient.getName() + " to subsidiary " + idSubsidiary);

		try {

			Ingredient ingredient = em.find(Ingredient.class, idIngredient);
			if (ingredient == null)
				throw new Exception("ingredient not found");

			subIngredient.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));
			subIngredient.setIngredientType(em.find(IngredientType.class, idIngredientType));

			em.merge(subIngredient);
			em.flush();

			log.log(Level.INFO, "Ingredient " + ingredient.getName() + " updated to subsidiary " + idSubsidiary);

			return Response.status(201).build();

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@OwnerOrManager
	@DELETE
	@Path("/ingredient-type/{idIngredientType:[0-9][0-9]*}/ingredient/{idIngredient:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response removeIngredientFromRestaurant(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idIngredient") Integer idIngredient,
			@PathParam("idIngredientType") Short idIngredientType) {

		log.log(Level.INFO, "Removing Ingredient " + idIngredient + " to subsidiary " + idSubsidiary);

		try {

			Ingredient i = em.find(Ingredient.class, idIngredient);
			if (i == null)
				throw new Exception("wrong ingredient id");
			i.setActive(false);

			em.merge(i);
			em.flush();

			log.log(Level.INFO, "Ingredient " + idIngredient + " removed from subsidiary " + idSubsidiary);

			return Response.status(200).build();

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}
}
