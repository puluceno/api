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

import org.hibernate.Hibernate;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.redefood.annotations.OwnerOrManager;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealIngredientTypes;
import br.com.redefood.model.MealIngredientTypeshasIngredient;
import br.com.redefood.model.MealType;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.service.FileUploadService;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}")
@Stateless
public class MealResource extends HibernateMapper {
    private static final ObjectMapper mapper = HibernateMapper.getMapper();
    @Inject
    private EntityManager em;
    @Inject
    private Logger log;
    @Inject
    private RedeFoodExceptionHandler eh;
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/meal-type")
    @Produces("application/json;charset=UTF8")
    public String findRestaurantMealTypes(@HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary) {
	
	try {
	    List<MealType> mealTypes = em.createNamedQuery(MealType.FIND_ALL_BY_SUBSIDIARY)
		    .setParameter("idSubsidiary", idSubsidiary).getResultList();
	    
	    return mapper.writeValueAsString(mealTypes);
	    
	} catch (Exception e) {
	    return eh.genericExceptionHandlerString(e, locale);
	}
    }
    
    @OwnerOrManager
    @POST
    @Path("/meal-type")
    @Consumes("application/json")
    public Response createMealType(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, MealType newMealType) {
	
	try {
	    if (newMealType.getExhibitionOrder() == null) {
		Integer maxOrder = (Integer) em.createNamedQuery(MealType.FIND_MAX_EXHIBITIONORDER)
			.setParameter("idSubsidiary", idSubsidiary).getSingleResult();
		if (maxOrder == null) {
		    maxOrder = new Integer(0);
		}
		newMealType.setExhibitionOrder(1 + maxOrder);
	    }
	    
	    newMealType.setSubsidiary(em.find(Subsidiary.class, idSubsidiary));
	    em.persist(newMealType);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.mealtype.created", newMealType.getName());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessPOSTExhibitionOrder(newMealType.getId(),
		    newMealType.getExhibitionOrder(), 201);
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale);
	}
	
    }
    
    @OwnerOrManager
    @PUT
    @Path("/meal-type/{idMealType:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response editMealType(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
	    @PathParam("idMealType") Integer idMealType, MealType updatedMealType) {
	
	log.log(Level.INFO, "Updating MealType " + idMealType + " for Restaurant " + idSubsidiary);
	
	MealType mealType = em.find(MealType.class, idMealType);
	try {
	    mealType.setExhibitionOrder(updatedMealType.getExhibitionOrder());
	    mealType.setName(updatedMealType.getName());
	    mealType.setDescription(updatedMealType.getDescription());
	    em.merge(mealType);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.mealtype.updated", mealType.getName());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswer(200, answer);
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale);
	}
	
    }
    
    @OwnerOrManager
    @DELETE
    @Path("/meal-type/{idMealType:[0-9][0-9]*}")
    public Response removeMealTypeFromRestaurant(@HeaderParam("token") String token,
	    @HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
	    @PathParam("idMealType") Integer idMealType) {
	
	log.log(Level.INFO, "Removing MealType " + idMealType + " for Restaurant " + idSubsidiary);
	MealType subsidiaryMealType = null;
	
	try {
	    subsidiaryMealType = em.find(MealType.class, idMealType);
	    
	    if (subsidiaryMealType != null) {
		subsidiaryMealType.setActive(false);
		em.merge(subsidiaryMealType);
		em.flush();
	    } else {
		String answer = LocaleResource.getString(locale, "exception.restaurant.mealtype.found", idMealType,
			idSubsidiary);
		log.log(Level.WARNING, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	    }
	    
	    String answer = LocaleResource.getString(locale, "restaurant.mealtype.removed",
		    subsidiaryMealType.getName());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswer(200, answer);
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale);
	}
	
    }
    
    @OwnerOrManager
    @POST
    @Path("/meal-type/{idMealType:[0-9][0-9]*}/meal")
    @Consumes("application/json")
    public Response addNewMeal(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idMealType") Integer idMealType, Meal newMeal) {
	
	try {
	    if (newMeal.getName().length() < 3)
		throw new Exception("name length");
	    
	    Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
	    if (subsidiary == null)
		throw new Exception("subsidiary not found");
	    newMeal.setSubsidiary(subsidiary);
	    
	    MealType mealType = em.find(MealType.class, idMealType);
	    if (mealType == null)
		throw new Exception("mealType not found");
	    newMeal.setMealType(mealType);
	    
	    List<MealIngredientTypes> mealIngredientTypes = newMeal.getMealIngredientTypes();
	    newMeal.setMealIngredientTypes(null);
	    newMeal.setActive(true);
	    
	    if (newMeal.getExhibitionOrder() == null) {
		Integer maxOrder = (Integer) em.createNamedQuery(Meal.FIND_MAX_ORDER)
			.setParameter("idSubsidiary", idSubsidiary).setParameter("idMealType", mealType.getId())
			.getSingleResult();
		if (maxOrder == null) {
		    maxOrder = new Integer("0");
		}
		newMeal.setExhibitionOrder(1 + maxOrder);
	    }
	    
	    em.persist(newMeal);
	    
	    if (mealIngredientTypes != null) {
		for (MealIngredientTypes mit : mealIngredientTypes) {
		    
		    mit.setMeal(newMeal);
		    em.persist(mit);
		    
		    for (MealIngredientTypeshasIngredient mithi : mit.getMealIngredientTypeshasIngredient()) {
			
			mithi.setMealIngredientType(mit);
			
			if (mithi.getPrice() == null) {
			    mithi.setPrice(0.0);
			}
			
			em.persist(mithi);
		    }
		    
		}
	    }
	    em.flush();
	    
	    Meal callbackObj = new Meal();
	    callbackObj.setId(newMeal.getId());
	    callbackObj.setName(newMeal.getName());
	    callbackObj.setDescription(newMeal.getDescription());
	    callbackObj.setPrice(newMeal.getPrice());
	    callbackObj.setImage(newMeal.getImage());
	    callbackObj.setExhibitionOrder(newMeal.getExhibitionOrder());
	    callbackObj.setMealIngredientTypes(mealIngredientTypes);
	    
	    String answer = LocaleResource.getString(locale, "restaurant.meal.created", newMeal.getName());
	    log.log(Level.INFO, answer);
	    String callback = mapper.writeValueAsString(callbackObj);
	    Response build = Response.status(201).entity(callback).build();
	    return build;
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale, idSubsidiary, idMealType);
	}
    }
    
    @OwnerOrManager
    @PUT
    @Path("/meal-type/{idMealType:[0-9][0-9]*}/meal/{idMeal:[0-9][0-9]*}/stock")
    @Consumes("application/json")
    public Response editMealStock(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idMealType") Integer idMealType,
	    @PathParam("idMeal") Integer idMeal, @QueryParam("outOfStock") Boolean outOfStock){
	
	try{
	    
	    Meal meal = em.find(Meal.class,idMeal);
	    meal.setOutOfStock(outOfStock);
	    
	    log.log(Level.INFO, "Meal " + meal.getName() + " out of stock set to " + outOfStock + " to Subsidiary " + idSubsidiary);
	    
	    return Response.status(200).build();
	    
	}catch (Exception e){
	    return eh.mealExceptions(e, locale);
	}
    }
    
    
    @SuppressWarnings("unchecked")
    @OwnerOrManager
    @PUT
    @Path("/meal-type/{idMealType:[0-9][0-9]*}/meal/{idMeal:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response editMeal(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idMealType") Integer idMealType,
	    @PathParam("idMeal") Integer idMeal, Meal updatedMeal) {
	
	log.log(Level.INFO, "Editing Meal " + updatedMeal.getName() + " to Restaurant " + idSubsidiary);
	
	Meal oldMeal = null;
	try {
	    Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
	    if (subsidiary == null)
		throw new Exception("restaurant not found");
	    
	    oldMeal = em.find(Meal.class, idMeal);
	    if (oldMeal == null)
		throw new Exception("meal not found");
	    
	    MealType mt = em.find(MealType.class, oldMeal.getMealType().getId());
	    if (mt == null)
		throw new Exception("mealType not found");
	    if (updatedMeal.getName().length() < 3)
		throw new Exception("name length");
	    
	    updatedMeal.setMealType(mt);
	    updatedMeal.setSubsidiary(subsidiary);
	    
	    List<MealIngredientTypes> mealIngredientTypes = updatedMeal.getMealIngredientTypes();
	    updatedMeal.setMealIngredientTypes(null);
	    
	    em.merge(updatedMeal);
	    em.flush();
	    
	    // if (mealIngredientTypes.isEmpty()) {
	    
	    List<MealIngredientTypes> resultList = em.createNamedQuery(MealIngredientTypes.FIND_MEAL_INGREDIENT_TYPE_BY_MEAL_AND_RESTAURANT)
		    .setParameter("idMeal", idMeal).setParameter("idSubsidiary", idSubsidiary).getResultList();
	    for (MealIngredientTypes mit : resultList) {
		em.remove(mit);
	    }
	    
	    // } else {
	    if (mealIngredientTypes != null) {
		for (MealIngredientTypes mealIngredientType : mealIngredientTypes) {
		    mealIngredientType.setMeal(updatedMeal);
		    mealIngredientType.setId(null);
		    // if (mealIngredientType.getId() == null) {
		    em.persist(mealIngredientType);
		    // } else {
		    // em.merge(mealIngredientType);
		    // }
		    List<MealIngredientTypeshasIngredient> mealIngredientTypeshasIngredient = mealIngredientType
			    .getMealIngredientTypeshasIngredient();
		    for (MealIngredientTypeshasIngredient mealIngredientTypeshasIngredient2 : mealIngredientTypeshasIngredient) {
			mealIngredientTypeshasIngredient2.setMealIngredientType(mealIngredientType);
			if (mealIngredientTypeshasIngredient2.getPrice() == null) {
			    mealIngredientTypeshasIngredient2.setPrice(0.0);
			}
			em.merge(mealIngredientTypeshasIngredient2);
		    }
		    
		}
		// }
	    }
	    
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.meal.updated", updatedMeal.getName());
	    log.log(Level.INFO, answer);
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale, idSubsidiary, idMealType, oldMeal != null ? oldMeal.getMealType()
		    .getName() : "null", oldMeal != null ? oldMeal.getName() : "null");
	}
    }
    
    @OwnerOrManager
    @PUT
    @Path("/meal/{idMeal:[0-9][0-9]*}/order")
    @Consumes("application/json")
    public Response editMealOrder(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idMeal") Integer idMeal, Meal updatedMeal) {
	
	try {
	    
	    log.log(Level.INFO, "Editing exhibition order to meal " + idMeal);
	    
	    Meal meal = em.find(Meal.class, idMeal);
	    meal.setExhibitionOrder(updatedMeal.getExhibitionOrder());
	    
	    em.merge(meal);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.meal.updated", meal.getName());
	    log.log(Level.INFO, answer);
	    return Response.status(200).build();
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale);
	}
    }
    
    @OwnerOrManager
    @DELETE
    @Path("/meal-type/{idMealType:[0-9][0-9]*}/meal/{idMeal:[0-9][0-9]*}")
    public Response removeMeal(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idMealType") Integer idMealType,
	    @PathParam("idMeal") Integer idMeal) {
	
	log.log(Level.INFO, "Removing meal " + idMeal + " from subsidiary " + idSubsidiary);
	
	try {
	    Meal meal = em.find(Meal.class, idMeal);
	    if (meal == null)
		throw new Exception("meal not found");
	    
	    meal.setActive(false);
	    em.merge(meal);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.meal.removed", meal.getName());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswer(200, answer);
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale);
	}
    }
    
    @OwnerOrManager
    @POST
    @Path("/meal/{idMeal:[0-9][0-9]*}/photo")
    @Consumes("multipart/form-data")
    public Response addMealPhoto(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idMeal") Integer idMeal,
	    MultipartFormDataInput photo) {
	
	try {
	    Meal meal = em.find(Meal.class, idMeal);
	    
	    if (meal == null) {
		String answer = LocaleResource.getString(locale, "exception.restaurant.meal.found", idMeal);
		log.log(Level.INFO, answer);
		return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	    }
	    
	    log.log(Level.INFO, "Uploading photo to meal " + meal.getId());
	    
	    if (meal.getImage() != null && !meal.getImage().contains("default")) {
		FileUploadService.deleteOldFile(meal.getImage());
	    }
	    
	    Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
	    
	    String uploadFile = FileUploadService.uploadFile("restaurant/"
		    + subsidiary.getRestaurant().getIdRestaurant().toString() + "/subsidiary/" + idSubsidiary + "/"
		    + meal.getClass().getSimpleName().toLowerCase(), idMeal.toString(), photo);
	    if (uploadFile.contains("error"))
		throw new Exception("file error");
	    
	    meal.setImage(uploadFile);
	    
	    em.merge(meal);
	    em.flush();
	    
	    String answer = LocaleResource.getString(locale, "restaurant.meal.updated", meal.getId());
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateSuccessAnswerWithoutSuccess(200, uploadFile);
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale);
	}
    }
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/meal")
    @Produces("application/json;charset=UTF8")
    public String findRestaurantMealsByMealType(@HeaderParam("locale") String locale,
	    @PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("localOnly") Boolean localOnly) {
	
	try {
	    List<MealType> meals = null;
	    
	    if (localOnly != null) {
		meals = em.createNamedQuery(MealType.FIND_MEALS_LOCAL_OR_DELIVERY_BY_MEALTYPE_AND_SUBSIDIARY)
			.setParameter("idSubsidiary", idSubsidiary).setParameter("localOnly", localOnly)
			.getResultList();
	    } else {
		meals = em.createNamedQuery(MealType.FIND_MEALS_BY_MEALTYPE_AND_SUBSIDIARY)
			.setParameter("idSubsidiary", idSubsidiary).getResultList();
	    }
	    
	    for (MealType mealType : meals) {
		for (Meal meal : mealType.getMeals()) {
		    for (MealIngredientTypes mealIngType : meal.getMealIngredientTypes()) {
			Hibernate.initialize(mealIngType.getIngredientType());
			for (MealIngredientTypeshasIngredient mealIngredientTypeshasIngredient : mealIngType
				.getMealIngredientTypeshasIngredient()) {
			    Hibernate.initialize(mealIngredientTypeshasIngredient.getIngredient());
			}
		    }
		}
	    }
	    
	    return mapper.writeValueAsString(meals);
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale).toString();
	}
    }
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/meal/{idMeal:[0-9][0-9]*}")
    @Produces("application/json;charset=UTF8")
    public String findMealDetails(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
	    @PathParam("idMeal") Integer idMeal) {
	
	try {
	    
	    List<MealIngredientTypes> resultList = em.createNamedQuery(MealIngredientTypes.FIND_MEAL_INGREDIENT_TYPE_BY_MEAL_AND_RESTAURANT)
		    .setParameter("idMeal", idMeal).setParameter("idSubsidiary", idSubsidiary).getResultList();
	    
	    if (resultList.isEmpty()) {
		Meal meal = (Meal) em.createNamedQuery(Meal.FIND_BY_ID_AND_SUBSIDIARY).setParameter("idMeal", idMeal)
			.setParameter("idSubsidiary", idSubsidiary).getSingleResult();
		return mapper.writeValueAsString(meal);
	    }
	    
	    for (MealIngredientTypes mealIngredientTypes : resultList) {
		Hibernate.initialize(mealIngredientTypes.getIngredientType());
	    }
	    
	    String stringAnswer = mapper.writeValueAsString(resultList);
	    return "{\"mealIngredientTypes\":" + stringAnswer + "}";
	    
	} catch (Exception e) {
	    return eh.mealExceptions(e, locale).toString();
	}
    }
}