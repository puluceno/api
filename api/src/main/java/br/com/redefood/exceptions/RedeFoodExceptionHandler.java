package br.com.redefood.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;

import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;

public class RedeFoodExceptionHandler {
    @Inject
    private Logger log;
    
    public Response restaurantExceptionHandler(Exception e, String locale, Object... parameter) {
	
	if (e.getMessage().contentEquals("restaurant null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.restaurant.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("subsidiary null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("owner null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.restaurant.owner.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid name")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.restaurant.name");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid subdomain")
		|| e.getMessage().contentEquals("No entity found for query")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.restaurant.subdomain");
	    log.log(Level.FINE, answer);
	    return RedeFoodAnswerGenerator.generateEmptyAnswer();
	}
	if (e.getMessage().contentEquals("invalid cnpj")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.cnpj");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("cnpj_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cnpj.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid cpf")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("cpf_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("module null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.restaurant.module.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid zipcode")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.zipcode");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid neighborhood")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.neighborhood");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid city")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.city");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid street")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.street");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid number")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.number");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("propertyPath=email")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.email.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("propertyPath=cellphone")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cellphone.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("domain_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.restaurant.subdomain.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("Invalid e-mail")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.email.invalid");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("file error")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.upload.found");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response orderExceptionHandlerResponse(Exception e, String locale, Object... parameter) {
	
	if (e.getMessage().contentEquals("subsidiary not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.found");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("user not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.user.found");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("paymentMethod null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.paymentMethod");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("orderType null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.orderType");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("ordertype not accepted")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.orderType.accepted");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("totalPrice null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.totalPrice");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("ingredient not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.ingredient");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("ingredient price")) {
	    String answer = LocaleResource.getString(locale, "exception.order.ingredient.price", e.getMessage()
		    .substring(e.getMessage().lastIndexOf(".")));
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("cannot deliver address")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.deliver.address");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("totalPrice wrong")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.totalprice.wrong");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("order price minimum")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.totalprice.minimum");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("duplicated beverage for restaurant")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.beverage.duplicated");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("beverage not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.beverage.found");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("print not available")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.available");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("failed order print")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
	}
	if (e.getMessage().contentEquals("subsidiary off")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.subsidiary.off");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("maximum change")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.change.maximum");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid cpf")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("order wo cellphone")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.user.cellphone");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("employee not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.found");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("employee does not work at subsidiary")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.subsidiary.found");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("order not found")) {
	    String answer = LocaleResource.getString(locale, "exception.order.found", parameter[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("reason empty")) {
	    String answer = LocaleResource.getString(locale, "exception.order.reason", parameter[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("reason length")) {
	    String answer = LocaleResource.getString(locale, "exception.order.reason.length", parameter[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid status")) {
	    String answer = LocaleResource.getString(locale, "exception.order.status.invalid", parameter[1],
		    parameter[2]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(403, answer);
	}
	if (e.getMessage().contentEquals("address not found")) {
	    String answer = LocaleResource.getString(locale, "exception.address");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("meal outOfStock")) {
	    String answer = LocaleResource.getString(locale, "exception.order.meal.outOfStock", e.getMessage()
		    .substring(e.getMessage().lastIndexOf(".")).replace(".", ""));
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("ingredient outOfStock")) {
	    String answer = LocaleResource.getString(locale, "exception.order.ingredient.outOfStock", e.getMessage()
		    .substring(e.getMessage().lastIndexOf(".")).replace(".", ""));
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("beverage outOfStock")) {
	    String answer = LocaleResource.getString(locale, "exception.order.beverage.outOfStock", e.getMessage()
		    .substring(e.getMessage().lastIndexOf(".")).replace(".", ""));
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response loginExceptionHandler(Exception e, String locale, String... message) {
	if (e.getMessage().contentEquals("invalid password")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.password");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("employee found")) {
	    String answer = LocaleResource.getString(locale, "exception.employee.found", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("token null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.token.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("server exception")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.server");
	    log.log(Level.SEVERE, answer + ". Failed to persist employee token.");
	    return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
	}
	if (e.getMessage().contentEquals("oauth")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.facebook.token.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("unsuficient data from facebook")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.facebook.insufficient.data");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("user cellphone not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.user.found.cellphone");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("user email not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.user.found.email");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public String orderExceptionHandlerString(Exception e, String locale, String message) {
	if (e.getMessage().contentEquals("no enum const")) {
	    String answer = LocaleResource.getString(locale, "exception.order.status.found", message);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(400, answer);
	}
	if (e.getMessage().contentEquals("order not found")) {
	    String answer = LocaleResource.getString(locale, "exception.order.found", message);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(400, answer);
	}
	if (e.getMessage().contentEquals("max fetchtime")) {
	    String answer = LocaleResource.getString(locale, "exception.order.fetchtime.max");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(400, answer);
	}
	if (e.getMessage().contentEquals("denied")) {
	    String answer = LocaleResource.getString(locale, "exception.permission");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(403, answer);
	}
	if (e.getMessage().contentEquals("user not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.me.token");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(400, answer);
	}
	
	return genericExceptionHandlerString(e, locale);
    }
    
    public Response subsidiaryExceptionHandler(Exception e, String locale, String... parameter) {
	if (e.getMessage().contentEquals("add delivery")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.delivery", parameter[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("subsidiary not found")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.found", parameter[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("name length")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.name.invalid");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("bad cnpj")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.cnpj");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("propertyPath=name")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.name.invalid");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("Invalid e-mail")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.email.invalid");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("name length")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.name.invalid");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("invalid zipcode")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.zipcode");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("invalid neighborhood")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.neighborhood");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("invalid city")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.city");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("invalid street")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.street");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("invalid number")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.number");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("open hour")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.opentime.create.open.hour",
		    parameter[1]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("open minute")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.opentime.create.open.minute",
		    parameter[1]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("close hour")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.opentime.create.close.hour",
		    parameter[2]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("close minute")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.opentime.create.close.minute",
		    parameter[2]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response deliveryExceptionHandler(Exception e, String locale, String... message) {
	if (e.getMessage().contains("99")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.delivery.value");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("delivery area does not exist")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.delivery.found", message[0],
		    message[1]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("invalid tax")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.delivery.value");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	// String answer = LocaleResource
	// .getString(locale, "exception.subsidiary.delivery.update",
	// message[0], message[1]);
	// log.log(Level.SEVERE, answer);
	// return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
	return genericExceptionHandlerResponse(e, locale);
	
    }
    
    public Response orderTypeExceptionHandlerResponse(Exception e, String locale) {
	if (e.getMessage().equalsIgnoreCase("ordertype without online module")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.ordertype.module.online");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(403, answer);
	}
	if (e.getMessage().equalsIgnoreCase("ordertype without local module")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.ordertype.module.local");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(403, answer);
	}
	if (e.getMessage().equalsIgnoreCase("ordertype without needed module")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.ordertype.module.needed");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(403, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response beverageExceptionHandler(Exception e, String locale, String... message) {
	if (e.getMessage().contains("price")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.beverage.price");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
	}
	if (e.getMessage().contains("NoResultException")) {
	    String answer = LocaleResource
		    .getString(locale, "exception.restaurant.beverage.removed.exists", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
	}
	if (e.getMessage().contentEquals("wrong beverage id")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.beverage.removed", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("wrong id rb")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.beverage.update", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("beverage already added")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.beverage.already.added", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("beverage not found")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.beverage.found", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("file error")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.upload.found");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response printExceptionHandlerResponse(Exception e, String locale) {
	if (e.getMessage().contentEquals("config null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.config.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("no printer")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.printer");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("no printer name")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.printer.name");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("no printer ip")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.printer.ip");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("subsidiaryip null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.subsidiary.ip.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("template null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.template.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("print delivery user null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.delivery.user.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("printer belong")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.subsidiary.printer.belong");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public String printExceptionHandlerString(Exception e, String locale) {
	if (e.getMessage().contentEquals("config null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.config.null");
	    log.log(Level.INFO, answer);
	    return answer;
	}
	if (e.getMessage().contentEquals("no printer")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.printer");
	    log.log(Level.INFO, answer);
	    return answer;
	}
	if (e.getMessage().contentEquals("no printer name")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.printer.name");
	    log.log(Level.INFO, answer);
	    return answer;
	}
	if (e.getMessage().contentEquals("no printer ip")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.printer.ip");
	    log.log(Level.INFO, answer);
	    return answer;
	}
	if (e.getMessage().contentEquals("subsidiaryip null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.subsidiary.ip.null");
	    log.log(Level.INFO, answer);
	    return answer;
	}
	if (e.getMessage().contentEquals("print delivery user null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.delivery.user.null");
	    log.log(Level.INFO, answer);
	    return answer;
	}
	
	// String answer =
	// LocaleResource.getProperty(locale).getProperty("exception.order.print");
	// log.log(Level.SEVERE, answer);
	// return answer;
	return genericExceptionHandlerString(e, locale);
    }
    
    public Response tokenExceptionHandler(Exception e, String locale) {
	if (e.getMessage().contentEquals("token expired")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.token.expired");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(410, answer);
	}
	if (e.getMessage().contentEquals("Invalid user token")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.token.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(410, answer);
	}
	if (e.getMessage().contains("id to load")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.token.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(410, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public String ratingExceptionHandler(Exception e, String locale, String... message) {
	if (e.getMessage().contains("No entity found for query")) {
	    String answer = LocaleResource.getString(locale, "exception.order.found", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(401, answer);
	}
	
	return genericExceptionHandlerString(e, locale);
    }
    
    public Response ratingExceptionHandlerResponse(Exception e, String locale, String... message) {
	if (e.getMessage().contains("No entity found for query")) {
	    String answer = LocaleResource.getString(locale, "exception.order.found", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("Existent reply")) {
	    String answer = LocaleResource.getString(locale, "exception.rating.reply.exists", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("Existent rejoinder")) {
	    String answer = LocaleResource.getString(locale, "exception.rating.rejoinder.exists", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response userExceptionHandler(Exception e, String locale, String... message) {
	if (e.getMessage().contentEquals("invalid cpf")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("email_UNIQUE")) {
	    String answer = LocaleResource.getString(locale, "exception.email.unique", message[0]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(409, answer);
	}
	if (e.getMessage().contains("cpf_UNIQUE")) {
	    String answer = LocaleResource.getString(locale, "exception.cpf.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(409, answer);
	}
	if (e.getMessage().contains("cellphone_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cellphone.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(409, answer);
	}
	if (e.getMessage().contentEquals("user null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.user.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("propertyPath=number")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.number");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("idCity")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.me.address.city");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("Invalid password!")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.password");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("token expired")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.password");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("password invalid")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.password");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("user not found")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.me.token");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("not allowed order user")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.order.user.allowed");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contentEquals("No entity found for query")) {
	    String answer = LocaleResource.getString(locale, "exception.user.found");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(404, answer);
	}
	if (e.getClass().isInstance(JMSException.class)) {
	    String answer = LocaleResource.getString(locale, "exception.user.send.email", message[0]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(503, answer);
	}
	if (e.getClass().isInstance(PersistenceException.class)) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.user.create.access");
	    log.log(Level.SEVERE, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
	}
	
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response fileExceptionHandler(Exception e, String locale) {
	if (e.getMessage().contains("file error")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.upload.found");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response boardExceptionHandler(Exception e, String locale) {
	if (e.getMessage().contentEquals("no data")) {
	    String answer = LocaleResource.getString(locale, "exception.board.data");
	    return RedeFoodAnswerGenerator.generateErrorAnswer(200, answer);
	}
	if (e.getMessage().contentEquals("No entity found for query")) {
	    String answer = LocaleResource.getString(locale, "exception.board.found");
	    return RedeFoodAnswerGenerator.generateErrorAnswer(206, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response employeeExceptions(Exception e, String locale, String... message) {
	if (e.getMessage().contentEquals("employee null")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.user.null");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("propertyPath=cpf")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.wo.cpf");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	if (e.getMessage().contains("propertyPath=firstName")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.wo.firstname");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	if (e.getMessage().contains("propertyPath=number")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.number");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	
	if (e.getMessage().contains("propertyPath=lastName")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.wo.lastname");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	if (e.getMessage().contains("propertyPath=cellphone")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.wo.cellphone");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	if (e.getMessage().contains("propertyPath=email")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.wo.email");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	if (e.getMessage().contains("propertyPath=password")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.employee.wo.password");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	if (e.getMessage().contains("cpf_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.unique");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(409, answer);
	}
	
	if (e.getMessage().contains("email_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.email.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	
	if (e.getMessage().contains("cellphone_UNIQUE")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.unique");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("file error")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.upload.found");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("invalid password")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.password");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("invalid cpf")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.cpf.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("Invalid e-mail")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.email.invalid");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("bad id")) {
	    String answer = LocaleResource.getString(locale, "exception.employee.bad.id", message[2]);
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(401, answer);
	}
	if (e.getMessage().contains("Address")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	String answer = LocaleResource.getString(locale, "exception.employee.save", message[0], message[1]);
	log.log(Level.SEVERE, answer);
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public String cepExceptionHandler(Exception e, String locale) {
	String answer = LocaleResource.getProperty(locale).getProperty("exception.cep.found");
	log.log(Level.WARNING, answer);
	return RedeFoodAnswerGenerator.generateErrorAnswerString(401, answer);
    }
    
    public Response cityExceptionHandler(Exception e, String locale) {
	return genericExceptionHandlerResponse(e, locale);
	
    }
    
    public Response mealExceptions(Exception e, String locale, Object... parameter) {
	if (e.getMessage().contains("restaurant not found")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.found", parameter[0]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("subsidiary not found")) {
	    String answer = LocaleResource.getString(locale, "exception.subsidiary.found", parameter[0]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("name length")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.meal.name");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("price")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.meal.price", parameter[2],
		    parameter[0]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("mealType not found")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.mealtype.found", parameter[2],
		    parameter[0]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contentEquals("meal not found")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.meal.found", parameter[3],
		    parameter[0]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("Duplicate entry")) {
	    String answer = LocaleResource.getString(locale, "exception.restaurant.mealtype.duplicated", parameter[2],
		    parameter[3]);
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	if (e.getMessage().contains("file error")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.upload.found");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response neighborhoodExceptions(Exception e, String locale) {
	if (e.getMessage().contentEquals("invalid city")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.address.city");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	return genericExceptionHandlerResponse(e, locale);
    }
    
    public Response genericExceptionHandlerResponse(Exception e, String locale) {
	if (!e.getMessage().isEmpty() && e.getMessage().contains("Data truncation")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic.data.truncation");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(413, answer);
	}
	
	if (e.getMessage().contains("No entity found for query")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic.no.result");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
	}
	
	String answer = LocaleResource.getProperty(locale).getProperty("exception.generic");
	log.log(Level.SEVERE, answer);
	log.log(Level.SEVERE, exceptionPlace(locale, e.getMessage()));
	return RedeFoodAnswerGenerator.generateErrorAnswer(500, answer);
    }
    
    public String genericExceptionHandlerString(Exception e, String locale) {
	if (!e.getMessage().isEmpty() && e.getMessage().contains("Data truncation")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic.data.truncation");
	    log.log(Level.WARNING, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(401, answer);
	}
	
	if (e.getMessage().contains("No entity found for query")) {
	    String answer = LocaleResource.getProperty(locale).getProperty("exception.generic.no.result");
	    log.log(Level.INFO, answer);
	    return RedeFoodAnswerGenerator.generateErrorAnswerString(401, answer);
	}
	String answer = LocaleResource.getProperty(locale).getProperty("exception.generic");
	log.log(Level.SEVERE, answer);
	log.log(Level.SEVERE, exceptionPlace(locale, e.getMessage()));
	return RedeFoodAnswerGenerator.generateErrorAnswerString(500, answer);
    }
    
    private String exceptionPlace(String locale, String message) {
	String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
	String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
	String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
	int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
	
	return LocaleResource.getString(locale, "exception.generic.where", message, className, methodName, lineNumber);
	
    }
    
}
