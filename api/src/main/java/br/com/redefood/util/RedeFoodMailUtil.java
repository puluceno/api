package br.com.redefood.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.redefood.model.BeverageOrder;
import br.com.redefood.model.MealOrder;
import br.com.redefood.model.MealOrderIngredient;
import br.com.redefood.model.Orders;
import br.com.redefood.model.PaymentMethod;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.complex.EmailDataDTO;

public class RedeFoodMailUtil {
    
    public static String createHTMTableData(final Object... objects) {
	Orders order = (Orders) objects[0];
	
	StringBuilder table = new StringBuilder();
	
	table.append("<table style=\"border-collapse: collapse;background-color: white;border: 1px solid #C3C3C3;border-collapse: collapse;width: 100%;\"><thead><th style=\"width: 50%; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top; \">N&uacute;mero do pedido: ");
	table.append(order.getTotalOrderNumber());
	
	table.append("</th><th style=\"width: 50%; text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"2\">Data: ");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
	table.append(dateFormat.format(order.getOrderMade()));
	
	table.append("</th></thead><tbody><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Pratos</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Quantidade</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Valor unit&aacute;rio</b></td></tr>");
	
	Map<String, List<MealOrder>> map = new HashMap<String, List<MealOrder>>();
	for (MealOrder mo : order.getMealsOrder()) {
	    
	    // begin teste
	    String key = mo.getMealTypeName();
	    if (map.get(key) == null) {
		map.put(key, new ArrayList<MealOrder>());
	    }
	    map.get(key).add(mo);
	}
	Set<Entry<String, List<MealOrder>>> entrySet = map.entrySet();
	for (Entry<String, List<MealOrder>> entry : entrySet) {
	    // Meal Type name
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top; font-style:italic\" colspan=\"3\">");
	    table.append("<strong>");
	    table.append("Categoria: " + entry.getKey());
	    table.append("</strong>");
	    table.append("</td></tr>");
	    for (MealOrder mealOrder : map.get(entry.getKey())) {
		
		// Meal name
		table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("<strong>");
		table.append(mealOrder.getName());
		table.append("</strong>");
		
		// Meal optionals
		if (mealOrder.getMealOrderIngredients() != null && !mealOrder.getMealOrderIngredients().isEmpty()) {
		    table.append("<br/>");
		    table.append("<strong>Opcionais: </strong>");
		    for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
			table.append("<br/>");
			table.append(mealOrderIngredient.getName());
		    }
		}
		
		// Meal notes
		if (mealOrder.getNote() != null && !mealOrder.getNote().isEmpty()) {
		    table.append("<br/> <strong>Obs: </strong>");
		    table.append(mealOrder.getNote());
		}
		table.append("</td>");
		
		// Quantity
		table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("1");
		table.append("<br/>");
		for (int i = 0; i < mealOrder.getMealOrderIngredients().size(); i++) {
		    table.append("<br/>");
		    table.append("1");
		}
		table.append("</td>");
		
		// Meal and ingredients Price
		table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("<strong>");
		table.append(RedeFoodUtils.moneyFormatter(mealOrder.getPrice()));
		table.append("</strong>");
		table.append("<br/>");
		for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
		    table.append("<br/>");
		    if (mealOrderIngredient.getPrice() > 0.0) {
			table.append("<strong>");
			table.append(RedeFoodUtils.moneyFormatter(mealOrderIngredient.getPrice()));
			table.append("</strong>");
		    } else {
			table.append(RedeFoodUtils.moneyFormatter(mealOrderIngredient.getPrice()));
		    }
		    
		}
		
	    }
	    
	}// closes Food 'for'
	
	table.append("</td></tr>");
	if (order.getBeveragesOrder() != null && !order.getBeveragesOrder().isEmpty()) {
	    table.append("<tr><td  style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"3\">&nbsp;</td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Bebidas</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Quantidade</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Valor unit&aacute;rio</b></td></tr>");
	    
	    for (BeverageOrder beverageOrder : order.getBeveragesOrder()) {
		table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		
		table.append("<strong>");
		table.append(beverageOrder.getName());
		table.append("</strong>");
		
		// Meal notes
		if (beverageOrder.getNote() != null && !beverageOrder.getNote().isEmpty()) {
		    table.append("<br/> <strong>Obs: </strong>");
		    table.append(beverageOrder.getNote());
		}
		table.append("</td>");
		
		// Quantity
		table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("1");
		
		// Beverage Prices
		table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("<strong>");
		table.append(RedeFoodUtils.moneyFormatter(beverageOrder.getPrice()));
		table.append("</strong>");
		table.append("<br/>");
	    }
	}
	table.append("</td></tr>");
	
	// Order note
	if (order.getNote() != null && !order.getNote().isEmpty()) {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"3\">&nbsp;</td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Observações do pedido</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>");
	    table.append(order.getNote());
	    table.append("</td></tr>");
	}
	
	// Delivery Price
	table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"3\">&nbsp;</td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Taxa de entrega</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>");
	table.append(RedeFoodUtils.moneyFormatter(order.getDeliveryPrice()));
	
	// Order Type and Address
	if (order.getOrderType().getId().intValue() == 6) {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Endereço para retirar seu pedido</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append(order.getSubsidiary().getAddress().getStreet() + ", nº"
		    + order.getSubsidiary().getAddress().getNumber() + " - "
		    + order.getSubsidiary().getAddress().getNeighborhood().getName() + ". Telefone: "
		    + order.getSubsidiary().getPhone1());
	    table.append("</td></tr>");
	} else {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Endereço de entrega</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append(order.getAddress().getStreet()
		    + ", "
		    + order.getAddress().getNumber()
		    + (order.getAddress().getComplement() != null && !order.getAddress().getComplement().isEmpty() ? " - "
			    .concat(order.getAddress().getComplement()) : "") + "; "
			    + order.getAddress().getNeighborhood().getName() + " - " + order.getAddress().getCity().getName());
	    table.append("</td></tr>");
	}
	
	// Total Price
	table.append("</b></td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Valor total</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>");
	table.append(RedeFoodUtils.moneyFormatter(order.getTotalPrice()));
	table.append("</b></td></tr>");
	
	// PaymentMethod
	table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Forma de pagamento</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	table.append(((PaymentMethod) objects[1]).getName());
	table.append("</td></tr>");
	if (((PaymentMethod) objects[1]).getName() != null
		&& ((PaymentMethod) objects[1]).getName().equals(PaymentMethod.MONEY)) {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Troco para</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append(RedeFoodUtils.moneyFormatter(order.getOrderChange()));
	    table.append("</td></tr>");
	}
	
	// Close string
	table.append("</tbody></table>");
	
	return table.toString();
	
    }
    
    /*public static String createTableHTMLtoSubsidiaryOrderMail(final Object... objects) {
	Orders order = (Orders) objects[0];
	
	StringBuilder table = new StringBuilder();
	
	table.append("<table style=\"border-collapse: collapse;background-color: white;border: 1px solid #C3C3C3;border-collapse: collapse;width: 100%;\"><thead><th style=\"width: 50%; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top; \">N&uacute;mero do pedido: ");
	table.append(order.getTotalOrderNumber());
	
	table.append("</th><th style=\"width: 50%; text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"2\">Data: ");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
	table.append(dateFormat.format(order.getOrderMade()));
	
	table.append("</th></thead><tbody><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Pratos</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Quantidade</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Valor unit&aacute;rio</b></td></tr>");
	
	for (MealOrder mealOrder : order.getMealsOrder()) {
	    // TEST MEAL TYPE NAME
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append("<strong>");
	    table.append(mealOrder.getMealTypeName());
	    table.append("</strong>");
	    table.append("</td></tr>");
	    // FINISH TEST MEAL TYPE NAME
	    
	    // Meal name
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append("<strong>");
	    table.append(mealOrder.getName());
	    table.append("</strong>");
	    
	    // Meal optionals
	    if (mealOrder.getMealOrderIngredients() != null && !mealOrder.getMealOrderIngredients().isEmpty()) {
		table.append("<br/>");
		table.append("<strong>Opcionais: </strong>");
		for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
		    table.append("<br/>");
		    table.append(mealOrderIngredient.getName());
		}
	    }
	    
	    // Meal notes
	    if (mealOrder.getNote() != null && !mealOrder.getNote().isEmpty()) {
		table.append("<br/> <strong>Obs: </strong>");
		table.append(mealOrder.getNote());
	    }
	    table.append("</td>");
	    
	    // Quantity
	    table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append("1");
	    table.append("<br/>");
	    for (int i = 0; i < mealOrder.getMealOrderIngredients().size(); i++) {
		table.append("<br/>");
		table.append("1");
	    }
	    table.append("</td>");
	    
	    // Meal and ingredients Price
	    table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append("<strong>");
	    table.append(RedeFoodUtils.moneyFormatter(mealOrder.getPrice()));
	    table.append("</strong>");
	    table.append("<br/>");
	    for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
		table.append("<br/>");
		if (mealOrderIngredient.getPrice() > 0.0) {
		    table.append("<strong>");
		    table.append(RedeFoodUtils.moneyFormatter(mealOrderIngredient.getPrice()));
		    table.append("</strong>");
		} else {
		    table.append(RedeFoodUtils.moneyFormatter(mealOrderIngredient.getPrice()));
		}
		
	    }
	}
	
	if (order.getBeveragesOrder() != null && !order.getBeveragesOrder().isEmpty()) {
	    table.append("</td></tr><tr><td  style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"3\">&nbsp;</td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Bebidas</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Quantidade</b></td><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>Valor unit&aacute;rio</b></td></tr>");
	    for (BeverageOrder beverageOrder : order.getBeveragesOrder()) {
		table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		
		table.append("<strong>");
		table.append(beverageOrder.getName());
		table.append("</strong>");
		
		// Meal notes
		if (beverageOrder.getNote() != null && !beverageOrder.getNote().isEmpty()) {
		    table.append("<br/> <strong>Obs: </strong>");
		    table.append(beverageOrder.getNote());
		}
		table.append("</td>");
		
		// Quantity
		table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("1");
		
		// Beverage Prices
		table.append("<td style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
		table.append("<strong>");
		table.append(RedeFoodUtils.moneyFormatter(beverageOrder.getPrice()));
		table.append("</strong>");
		table.append("<br/>");
	    }
	}
	table.append("</td></tr>");
	
	// Order note
	if (order.getNote() != null && !order.getNote().isEmpty()) {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"3\">&nbsp;</td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Observações do pedido</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>");
	    table.append(order.getNote());
	    table.append("</td></tr>");
	}
	
	// Delivery Price
	table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\" colspan=\"3\">&nbsp;</td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Taxa de entrega</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>");
	table.append(RedeFoodUtils.moneyFormatter(order.getDeliveryPrice()));
	
	// Order Type and Address
	if (order.getOrderType().getId().intValue() == 6) {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">O cliente virá buscar o pedido.</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append("&nbsp;");
	    table.append("</td></tr>");
	} else {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Endereço de entrega</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append(order.getAddress().getStreet()
		    + ", "
		    + order.getAddress().getNumber()
		    + (order.getAddress().getComplement() != null && !order.getAddress().getComplement().isEmpty() ? " - "
			    .concat(order.getAddress().getComplement()) : "") + ";"
			    + order.getAddress().getNeighborhood().getName() + " - " + order.getAddress().getCity().getName());
	    table.append("</td></tr>");
	}
	
	// Total Price
	table.append("</b></td></tr><tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Valor total</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\"><b>");
	table.append(RedeFoodUtils.moneyFormatter(order.getTotalPrice()));
	table.append("</b></td></tr>");
	
	// PaymentMethod
	table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Forma de pagamento</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	table.append(((PaymentMethod) objects[1]).getName());
	table.append("</td></tr>");
	if (((PaymentMethod) objects[1]).getName() != null
		&& ((PaymentMethod) objects[1]).getName().equals(PaymentMethod.MONEY)) {
	    table.append("<tr><td style=\"border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">Troco para</td><td colspan=\"2\" style=\"text-align: center; border: 1px solid #C3C3C3;padding: 3px;vertical-align: top;\">");
	    table.append(RedeFoodUtils.moneyFormatter(order.getOrderChange()));
	    table.append("</td></tr>");
	}
	
	// Close string
	table.append("</tbody></table>");
	
	return table.toString();
    }*/
    
    public static String createTableHTMLtoPickUp(Orders order) {
	
	StringBuilder table = new StringBuilder();
	
	table.append("<table style=\"border-collapse: collapse;background-color: white;border-collapse: collapse;width: 100%;\"><thead><th style=\"width: 50%; padding: 3px;vertical-align: top; \">Endere&ccedil;o do estabelecimento: ");
	table.append(order.getSubsidiary().getAddress().getStreet() + ", nº"
		+ order.getSubsidiary().getAddress().getNumber() + " - "
		+ order.getSubsidiary().getAddress().getNeighborhood().getName() + ". Telefone: "
		+ order.getSubsidiary().getPhone1());
	table.append("</th></thead></table>");
	
	return table.toString();
    }
    
    /**
     * Method responsible for populating the logo and the footer for all emails
     * sent by the system
     * 
     * @param emailData
     *            {@link EmailDataDTO}
     */
    public static void prepareRedeFoodLogoAndFooter(EmailDataDTO<String, String> emailData) {
	emailData.put("subsidiaryUrl", "\"" + RedeFoodConstants.DEFAULT_REDEFOOD_URL + "\"");
	emailData.put("logo", "\"" + RedeFoodConstants.RESTAURANT_LOGO_PATH + RedeFoodConstants.REDEFOOD_LOGO_ICON
		+ "\"");
	emailData.put("subsidiaryName", RedeFoodConstants.REDEFOOD_NAME);
	emailData.put("facebook", RedeFoodConstants.REDEFOOD_FACEBOOK);
	emailData.put("urlHelp", "\"" + RedeFoodConstants.DEFAULT_REDEFOOD_URL + RedeFoodConstants.DEFAULT_HELP_SUFFIX
		+ "\"");
	emailData.put("contactEmail", RedeFoodConstants.REDEFOOD_SUPPORT_EMAIL);
	emailData.put("footerSlogan", RedeFoodConstants.REDEFOOD_SLOGAN);
	emailData.put("ratingUrl", emailData.get("originUrl") + RedeFoodConstants.DEFAULT_ADMIN_RATING_SUFFIX);
    }
    
    /**
     * Method responsible for populating the logo and the footer for all emails
     * sent by the system.
     * 
     * @param emailData
     *            {@link EmailDataDTO}
     * @param subsidiary
     *            {@link Subsidiary}
     */
    public static void prepareSubsidiaryLogoAndFooter(EmailDataDTO<String, String> emailData, Subsidiary subsidiary) {
	emailData.put("subsidiaryUrl", "\"" + RedeFoodUtils.urlBuilder(subsidiary.getRestaurant().getSubdomain())
		+ "\"");
	emailData.put("logo", "\"" + RedeFoodConstants.RESTAURANT_LOGO_PATH + subsidiary.getRestaurant().getLogo()
		+ "\"");
	emailData.put("subsidiaryName", subsidiary.getName());
	emailData.put("facebook", subsidiary.getFacebook());
	emailData.put("urlHelp", "\"" + RedeFoodUtils.urlBuilder(subsidiary.getRestaurant().getSubdomain())
		+ RedeFoodConstants.DEFAULT_HELP_SUFFIX + "\"");
	emailData.put("contactEmail", subsidiary.getEmail());
	emailData.put("footerSlogan", subsidiary.getRestaurant().getSlogan() == null ? "" : subsidiary.getRestaurant()
		.getSlogan());
	emailData.put("ratingUrl", emailData.get("originUrl") + RedeFoodConstants.DEFAULT_ADMIN_RATING_SUFFIX);
    }
    
}
