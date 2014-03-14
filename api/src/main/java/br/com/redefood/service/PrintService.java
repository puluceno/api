package br.com.redefood.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

import br.com.redefood.model.BeverageOrder;
import br.com.redefood.model.Board;
import br.com.redefood.model.Configuration;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealIngredientTypes;
import br.com.redefood.model.MealIngredientTypeshasIngredient;
import br.com.redefood.model.MealOrder;
import br.com.redefood.model.MealOrderIngredient;
import br.com.redefood.model.Orders;
import br.com.redefood.model.PaymentMethod;
import br.com.redefood.model.Printer;
import br.com.redefood.model.enumtype.TypeOrder;
import br.com.redefood.rest.OrderResource;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.PrinterConstants;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodUtils;

public class PrintService {

	@PersistenceContext(unitName = "RedeFood")
	public EntityManager em;

	public static String print(Orders order, String locale, Configuration config, String clientIP, PaymentMethod pm)
			throws Exception {
		String answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.configured");
		boolean sleep = false;
		for (Printer printer : config.getPrinters()) {

			if (printer.getPrintOrder()) {
				if (!order.getMeals().isEmpty()) {
					answer = printOrderKitchen(order, locale, printer, clientIP);
					sleep = true;
				}
			}
			if (printer.getPrintOrderClient()) {
				if (sleep) {
					Thread.sleep(200);
				}
				answer = printOrderClient(order, locale, printer, clientIP);
				sleep = true;
			}
			if (printer.getPrintOrderNumber()) {
				if (sleep) {
					Thread.sleep(200);
				}
				answer = printOrderNumber(order, locale, printer, clientIP);
				sleep = true;
			}
			if (order.getUser() != null)
				if (printer.getPrintDelivery()) {
					if (sleep) {
						Thread.sleep(200);
					}
					answer = printDelivery(order, locale, printer, clientIP, pm);
					sleep = true;
				}
		}
		return answer;
	}

	public static String printOrderKitchen(Orders order, String locale, Printer printer, String clientIP)
			throws Exception {

		InitialContext ctx = new InitialContext();
		OrderResource em = (OrderResource) ctx.lookup("java:global/api/OrderResource");

		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date"));
		printerData.add(": ");
		printerData.add(formatDate(order.getOrderMade()));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.number"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_SUPER_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		if (order.getOrderType().getType() == TypeOrder.LOCAL) {
			printerData.add(order.getLocalOrderNumber().toString());
		} else {
			printerData.add(order.getTotalOrderNumber().toString());
		}
		printerData.add(PrinterConstants.LINE_BREAK);
		if (order.getOrderType() != null && order.getOrderType().getId() == 5) {
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.to.go"));
			printerData.add(PrinterConstants.LINE_BREAK);
		}

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);
		printerData.add("--------------------------------------------------");
		if (order.getNote() != null && !order.getNote().isEmpty()) {
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.note"));
			printerData.add(": ");
			printerData.add(order.getNote());
			printerData.add(PrinterConstants.LINE_BREAK);
			printerData.add("--------------------------------------------------");
		}

		// começa a tratar os ingredientes

		List<MealOrder> mealsOrder = order.getMealsOrder();
		MealOrder mealOrder = null;
		for (int k = 0; k < mealsOrder.size(); k++) {
			mealOrder = mealsOrder.get(k);
			List<String> ingredients = new ArrayList<String>();

			List<String> addedIngredients = new ArrayList<String>();
			List<String> addedOptionals = new ArrayList<String>();
			List<String> ingredientsNotMultiple = new ArrayList<String>();
			for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
				// lista com todos os ingredientes adicionados
				if (mealOrderIngredient.getPrice() == 0.0) {
					addedIngredients.add(mealOrderIngredient.getName());
				}

				// lista com todos os opcionais adicionados
				if (mealOrderIngredient.getPrice() != 0.0) {
					addedOptionals.add(mealOrderIngredient.getName());
				}

			}

			Meal meal = em.findMeal(mealOrder.getIdMeal());

			for (MealIngredientTypes mit : meal.getMealIngredientTypes()) {
				for (MealIngredientTypeshasIngredient mithi : mit.getMealIngredientTypeshasIngredient()) {

					// lista com todos os ingredientes inclusos no produto
					if ((mithi.getPrice() == null || mithi.getPrice() == 0.0) && mit.getMultiple()) {
						ingredients.add(mithi.getIngredient().getName());
					}
					if ((mithi.getPrice() == null || mithi.getPrice() == 0.0) && !mit.getMultiple()) {
						ingredientsNotMultiple.add(mithi.getIngredient().getName());
					}
				}
			}

			// nesta lista vai sobrar os outros ingredientes sem preço que
			// precisam ser impressos, e q nao sao multiplos
			ingredientsNotMultiple.removeAll(ingredients);

			ingredients.removeAll(addedIngredients);

			// removes duplicated items
			HashSet<String> hs = new HashSet<String>();
			hs.addAll(ingredientsNotMultiple);
			ingredientsNotMultiple.clear();
			ingredientsNotMultiple.addAll(hs);

			for (String string : ingredientsNotMultiple) {
				if (addedIngredients.contains(string)) {
					addedOptionals.add(string);
				}
			}

			// começa a impressão
			// Print meal name
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(k + 1 + ". ");
			printerData.add(mealOrder.getName());
			printerData.add(PrinterConstants.BOLD_OFF);
			printerData.add(": ");
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(mealOrder.getNote());
			printerData.add(PrinterConstants.LINE_BREAK);

			// Imprime cada ingrediente removido
			for (String name : ingredients) {
				printerData.add(PrinterConstants.ALLINGMENT_RIGHT);
				printerData.add(PrinterConstants.FONT_SMALL);
				printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.no") + " ");
				printerData.add(PrinterConstants.BOLD_OFF);
				printerData.add(name);
				printerData.add(PrinterConstants.LINE_BREAK);
			}

			// Imprime cada ingrediente adicionado além dos normais
			for (String name : addedOptionals) {
				printerData.add(PrinterConstants.ALLINGMENT_RIGHT);
				printerData.add(PrinterConstants.FONT_WIDE);
				printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.plus") + " ");
				printerData.add(PrinterConstants.FONT_SMALL);
				// printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(name);
				// printerData.add(PrinterConstants.BOLD_OFF);
				printerData.add(PrinterConstants.LINE_BREAK);
			}
			printerData.add("-------------------------------------------------");
			printerData.add(PrinterConstants.LINE_BREAK);

		}
		// termina o tratamento dos ingredientes

		printerData.add(PrinterConstants.PARTIAL_CUT_PAPER);
		printerData.add(PrinterConstants.CLEAR);

		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {

			return LocaleResource.getProperty(locale).getProperty("exception.order.print");
		}

		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

	public static String printOrderClient(Orders order, String locale, Printer printer, String clientIP)
			throws Exception {
		// TODO: rever pq está imprimindo errado
		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		// Pre-Header
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.redefood.header"));
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.redefood.url"));
		printerData.add(PrinterConstants.LINE_BREAK);

		// Header
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date"));
		printerData.add(": ");
		printerData.add(formatDate(order.getOrderMade()));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.number"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_SUPER_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		if (order.getOrderType().getType() == TypeOrder.LOCAL) {
			printerData.add(order.getLocalOrderNumber().toString());
		} else {
			printerData.add(order.getTotalOrderNumber().toString());
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);
		printerData.add("--------------------------------------------------");
		if (order.getNote() != null && !order.getNote().isEmpty()) {
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.note"));
			printerData.add(": ");
			printerData.add(order.getNote());
			printerData.add(PrinterConstants.LINE_BREAK);
			printerData.add("--------------------------------------------------");
		}

		for (MealOrder mealOrder : order.getMealsOrder()) {
			printerData.add(PrinterConstants.BOLD_OFF);
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(mealOrder.getName());
			printerData.add(PrinterConstants.HORIZONTAL_TAB);
			printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
			printerData.add(mealOrder.getPrice().toString());
			printerData.add(PrinterConstants.LINE_BREAK);

			for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
				if (mealOrderIngredient.getPrice() != null && mealOrderIngredient.getPrice() > 0.0) {
					printerData.add(PrinterConstants.FONT_SMALL);
					printerData.add(PrinterConstants.BOLD_ON);
					printerData.add(mealOrderIngredient.getName());
					printerData.add(PrinterConstants.HORIZONTAL_TAB);
					printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
					printerData.add(formatDouble(mealOrderIngredient.getPrice()));
					printerData.add(PrinterConstants.BOLD_OFF);
					printerData.add(PrinterConstants.LINE_BREAK);
				}
			}
		}
		printerData.add("--------------------------------------------------");

		// Beverages header
		if (order.getBeveragesOrder() != null && !order.getBeveragesOrder().isEmpty()) {
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.beverages"));
			printerData.add(": ");
			printerData.add(PrinterConstants.LINE_BREAK);

			// Beverages names
			for (BeverageOrder beverageOrder : order.getBeveragesOrder()) {
				printerData.add(PrinterConstants.FONT_SMALL);
				printerData.add(PrinterConstants.ALLINGMENT_LEFT);
				printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(beverageOrder.getName());
				printerData.add(PrinterConstants.HORIZONTAL_TAB);
				printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
				printerData.add(formatDouble(beverageOrder.getPrice()));
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}

		// To go?
		if (order.getOrderType() != null && order.getOrderType().getId() == 5) {
			printerData.add("-------------------------------------------------");
			printerData.add(PrinterConstants.LINE_BREAK);
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.to.go"));
			printerData.add(PrinterConstants.LINE_BREAK);
		}

		// Total order price
		printerData.add("--------------------------------------------------");
		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.totalvalue"));
		printerData.add(": ");
		printerData.add(PrinterConstants.ALLINGMENT_RIGHT);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		printerData.add(formatDouble(order.getTotalPrice()));

		printerData.add(PrinterConstants.CUT_PAPER);
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {

			return LocaleResource.getProperty(locale).getProperty("exception.order.print");
		}

		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

	public static String printOrderNumber(Orders order, String locale, Printer printer, String clientIP) {
		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date"));
		printerData.add(": ");
		printerData.add(formatDate(order.getOrderMade()));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.number"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_SUPER_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		if (order.getOrderType().getType() == TypeOrder.LOCAL) {
			printerData.add(order.getLocalOrderNumber().toString());
		} else {
			printerData.add(order.getTotalOrderNumber().toString());
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		if (order.getOrderType() != null && order.getOrderType().getId() == 5) {
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.to.go"));
		}
		printerData.add(PrinterConstants.CUT_PAPER);
		printerData.add(PrinterConstants.CLEAR);

		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {
			return LocaleResource.getProperty(locale).getProperty("exception.order.print");
		}

		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

	public static String printDelivery(Orders order, String locale, Printer printer, String clientIP, PaymentMethod pm) {
		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		// Pre-Header
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(order.getSubsidiary().getRestaurant().getSlogan());
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(RedeFoodUtils.urlBuilder(order.getSubsidiary().getRestaurant().getSubdomain()));
		printerData.add(PrinterConstants.LINE_BREAK);

		// Header
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date"));
		printerData.add(": ");
		printerData.add(formatDate(order.getOrderMade()));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.number"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_SUPER_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		if (order.getOrderType().getType() == TypeOrder.LOCAL) {
			printerData.add(order.getLocalOrderNumber().toString());
		} else {
			printerData.add(order.getTotalOrderNumber().toString());
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		// Address
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.FONT_SMALL);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.user.name"));
		printerData.add(": ");
		printerData.add(order.getUser().getFirstName().toUpperCase());
		printerData.add(PrinterConstants.LINE_BREAK);
		if (order.getOrderType().getId().intValue() == 6) {
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.pickup"));
			printerData.add(PrinterConstants.BOLD_OFF);
			printerData.add(PrinterConstants.FONT_NORMAL);
		} else {
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.user.address"));
			printerData.add(": ");
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(order.getAddress().getStreet());
			printerData.add(", nº");
			printerData.add(order.getAddress().getNumber());
			printerData.add(", ");
			printerData.add(order.getAddress().getNeighborhood().getName());
			if (order.getAddress().getComplement() != null && !order.getAddress().getComplement().equals("")) {
				printerData.add(" - " + order.getAddress().getComplement());
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.FONT_SMALL);

		// Home phone
		printerData.add("--------------------------------------------------");
		if (order.getUser().getPhone() != null && order.getUser().getPhone().length() > 7) {
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.phone"));
			printerData.add(": ");
			printerData.add(order.getUser().getPhone());
		}

		// Separate phone and cellphone
		if (order.getUser().getPhone() != null && order.getUser().getCellphone() != null
				&& order.getUser().getPhone().length() > 7 && order.getUser().getCellphone().length() > 7) {
			printerData.add(" / ");
		}

		// Cellphone
		if (order.getUser().getCellphone() != null && order.getUser().getCellphone().length() > 7) {
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.cellphone"));
			printerData.add(": ");
			printerData.add(order.getUser().getCellphone());
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		// Observation
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);
		printerData.add("--------------------------------------------------");
		if (order.getNote() != null && !order.getNote().isEmpty()) {
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.observation"));
			printerData.add(PrinterConstants.BOLD_OFF);
			printerData.add(": ");
			printerData.add(order.getNote());
			printerData.add(PrinterConstants.LINE_BREAK);
			printerData.add("--------------------------------------------------");
		}

		// Meals names
		if (!order.getMealsOrder().isEmpty()) {
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.orderlabel"));
			printerData.add(": ");
			printerData.add(PrinterConstants.LINE_BREAK);
		}
		MealOrder mealOrder = null;
		List<MealOrder> mealsOrder = order.getMealsOrder();
		for (int j = 0; j < mealsOrder.size(); j++) {
			mealOrder = mealsOrder.get(j);
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(j + 1 + ". ");
			printerData.add(mealOrder.getName());
			printerData.add(PrinterConstants.BOLD_OFF);
			printerData.add(": ");

			if (mealOrder.getPrice() != null && mealOrder.getPrice().doubleValue() > 0.0) {
				printerData.add(PrinterConstants.FONT_NORMAL);
				printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
				printerData.add(mealOrder.getPrice().toString());
			}
			printerData.add(PrinterConstants.LINE_BREAK);
			if (mealOrder.getNote() != null && !mealOrder.getNote().isEmpty()) {
				printerData.add(PrinterConstants.FONT_SMALL);
				printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(mealOrder.getNote());
				printerData.add(PrinterConstants.BOLD_OFF);
				printerData.add(PrinterConstants.LINE_BREAK);
			}

			boolean printOptional = true;
			int size = mealOrder.getMealOrderIngredients().size();
			for (int i = 0; i < size; i++) {
				MealOrderIngredient mealOrderIngredient = mealOrder.getMealOrderIngredients().get(i);
				printerData.add(PrinterConstants.FONT_SMALL);
				if (printOptional && mealOrderIngredient.getPrice() != null
						&& mealOrderIngredient.getPrice().compareTo(0.0) > 0) {
					printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.optionals"));
					printerData.add(": ");
					printOptional = false;
				}
				if (mealOrderIngredient.getPrice() != null && mealOrderIngredient.getPrice().compareTo(0.0) > 0) {
					printerData.add(mealOrderIngredient.getName());
					printerData.add(" (");
					printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
					printerData.add(formatDouble(mealOrderIngredient.getPrice()));
					printerData.add(")");
					if (i < size && mealOrder.getMealOrderIngredients().get(i + 1).getPrice() != null
							&& mealOrder.getMealOrderIngredients().get(i + 1).getPrice().compareTo(0.0) > 0) {
						printerData.add(", ");
					}
				}
			}

			printerData.add(PrinterConstants.LINE_BREAK);
			printerData.add("--------------------------------------------------");
			printerData.add(PrinterConstants.LINE_BREAK);
		}

		// Beverages header
		if (order.getBeveragesOrder() != null && !order.getBeveragesOrder().isEmpty()) {
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			// Beverages names
			for (BeverageOrder beverageOrder : order.getBeveragesOrder()) {
				printerData.add(PrinterConstants.FONT_NORMAL);
				printerData.add(PrinterConstants.ALLINGMENT_LEFT);
				printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(beverageOrder.getName());
				printerData.add(": ");
				if (beverageOrder.getPrice() != null && beverageOrder.getPrice() > 0.0) {
					printerData.add(PrinterConstants.ALLINGMENT_RIGHT);
					printerData.add(PrinterConstants.FONT_SMALL);
					printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
					printerData.add(formatDouble(beverageOrder.getPrice()));
				}
				printerData.add(PrinterConstants.LINE_BREAK);
			}
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add("--------------------------------------------------");
		}

		// Payment Method
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.paymentmethod"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(pm == null ? LocaleResource.getProperty(locale).getProperty("order.print.not.selected") : pm
				.getName());
		if (order.getOrderChange() != null) {
			printerData.add(", ");
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.change"));
			printerData.add(" ");
			printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
			printerData.add(formatDouble(order.getOrderChange()));
			printerData.add(" (");
			printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
			printerData.add(formatDouble(order.getOrderChange() - order.getTotalPrice()));
			printerData.add(")");
		}
		printerData.add(PrinterConstants.FONT_SMALL);
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add("--------------------------------------------------");

		// Taxes and total order price
		if (order.getDeliveryPrice() != null) {
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.deliverytax"));
			printerData.add(": ");
			printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
			printerData.add(PrinterConstants.ALLINGMENT_RIGHT);
			printerData.add(formatDouble(order.getDeliveryPrice()));
			printerData.add(PrinterConstants.LINE_BREAK);
		}
		// Total order price
		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.totalvalue"));
		printerData.add(": ");
		printerData.add(PrinterConstants.ALLINGMENT_RIGHT);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		printerData.add(formatDouble(order.getTotalPrice()));
		printerData.add(PrinterConstants.BOLD_OFF);

		// Footer
		printerData.add(PrinterConstants.FONT_SMALL);
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add("--------------------------------------------------");
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(LocaleResource.getString(locale, "order.print.footer",
				RedeFoodUtils.urlBuilder(order.getSubsidiary().getRestaurant().getSubdomain())
						+ RedeFoodConstants.DEFAULT_RATING_SUFFIX + order.getId()));
		// End of printing
		printerData.add(PrinterConstants.CUT_PAPER);
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {
			return LocaleResource.getProperty(locale).getProperty("exception.order.print");
		}
		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

	/**
	 * Imprime os dados obtidos via consulta na dashboard.
	 * 
	 * @param locale
	 * @param printer
	 * @param clientIP
	 * @param to
	 * @param from
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String printCashier(String locale, Printer printer, String clientIP, Map<String, Object> dash,
			Date from, Date to) {

		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		// Header
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date.from"));
		printerData.add(": ");
		printerData.add(formatDate(from));
		printerData.add(" - ");
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date.to"));
		printerData.add(": ");
		printerData.add(formatDate(to));
		printerData.add(PrinterConstants.LINE_BREAK);

		// Cashier data
		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.total.revenue"));
		printerData.add(": ");
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.FONT_WIDE);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		if (dash.get("totalRenevue").equals("null")) {
			printerData.add((String) dash.get("totalOrderValue"));
		} else {
			printerData.add(String.valueOf(dash.get("totalRenevue")));
		}

		printerData.add(PrinterConstants.LINE_BREAK);

		// Delivery costs
		if (dash.get("totalDeliveryValue") != null && !dash.get("totalDeliveryValue").toString().isEmpty()
				&& !dash.get("totalDeliveryValue").toString().equals("0.0")
				&& !dash.get("totalDeliveryValue").toString().equals("null")) {
			printerData.add(PrinterConstants.FONT_BIG);
			printerData.add(PrinterConstants.BOLD_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.delivery.costs"));
			printerData.add(": ");
			printerData.add(PrinterConstants.BOLD_OFF);
			printerData.add(PrinterConstants.FONT_WIDE);
			printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
			printerData.add(String.valueOf(dash.get("totalDeliveryValue")));
			printerData.add(PrinterConstants.LINE_BREAK);
		}

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.payment.methods"));
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		List<HashMap<String, String>> moneyByPMList = (List<HashMap<String, String>>) dash.get("moneyByPaymentMethod");
		for (Iterator<HashMap<String, String>> iterator = moneyByPMList.iterator(); iterator.hasNext();) {
			HashMap<String, String> moneyByPM = iterator.next();

			printerData.add(moneyByPM.get("name"));
			printerData.add(": ");
			printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
			printerData.add(moneyByPM.get("qty"));
			if (iterator.hasNext()) {
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.orders.qty"));
		printerData.add(": ");
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.FONT_WIDE);
		printerData.add(String.valueOf(dash.get("orderQty")));
		printerData.add(PrinterConstants.LINE_BREAK);

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.orders.type"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		List<HashMap<String, String>> orderTypeList = (List<HashMap<String, String>>) dash.get("orderTypeSells");
		for (Iterator<HashMap<String, String>> iterator = orderTypeList.iterator(); iterator.hasNext();) {
			HashMap<String, String> orderType = iterator.next();

			printerData.add(orderType.get("name"));
			printerData.add(": ");
			printerData.add(orderType.get("qty"));
			if (iterator.hasNext()) {
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		// TODO: pedidos cancelados

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.meal.type"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		List<HashMap<String, String>> mealTypeSoldList = (List<HashMap<String, String>>) dash.get("mealTypeSold");
		for (Iterator<HashMap<String, String>> iterator = mealTypeSoldList.iterator(); iterator.hasNext();) {
			HashMap<String, String> mealType = iterator.next();

			printerData.add(mealType.get("name"));
			printerData.add(": ");
			printerData.add(mealType.get("qty"));
			if (iterator.hasNext()) {
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.meal.sold"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		List<HashMap<String, String>> mealSoldList = (List<HashMap<String, String>>) dash.get("mealSold");
		for (Iterator<HashMap<String, String>> iterator = mealSoldList.iterator(); iterator.hasNext();) {
			HashMap<String, String> meal = iterator.next();

			printerData.add(meal.get("name"));
			printerData.add(": ");
			printerData.add(meal.get("qty"));
			if (iterator.hasNext()) {
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.beverage.type"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		List<HashMap<String, String>> beverageTypeSoldList = (List<HashMap<String, String>>) dash
				.get("beverageTypeSold");
		for (Iterator<HashMap<String, String>> iterator = beverageTypeSoldList.iterator(); iterator.hasNext();) {
			HashMap<String, String> beverageType = iterator.next();

			printerData.add(beverageType.get("name"));
			printerData.add(": ");
			printerData.add(beverageType.get("qty"));
			if (iterator.hasNext()) {
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		printerData.add(PrinterConstants.FONT_BIG);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.beverage.sold"));
		printerData.add(": ");
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		List<HashMap<String, String>> beverageSoldList = (List<HashMap<String, String>>) dash.get("beverageSold");
		for (Iterator<HashMap<String, String>> iterator = beverageSoldList.iterator(); iterator.hasNext();) {
			HashMap<String, String> beverageSold = iterator.next();

			printerData.add(beverageSold.get("name"));
			printerData.add(": ");
			printerData.add(beverageSold.get("qty"));
			if (iterator.hasNext()) {
				printerData.add(PrinterConstants.LINE_BREAK);
			}
		}
		printerData.add(PrinterConstants.LINE_BREAK);

		// End of printing
		printerData.add(PrinterConstants.CUT_PAPER);
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {
			return LocaleResource.getProperty(locale).getProperty("exception.cashier.print");
		}
		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

	public static String printBoardOrders(Board b, String locale, Printer printer, String clientIP) {

		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		// Pre-Header
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.redefood.header"));
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.redefood.url"));
		printerData.add(PrinterConstants.LINE_BREAK);

		// Header
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(PrinterConstants.FONT_WIDE);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.number"));
		printerData.add(": ");
		printerData.add(b.getNumber().toString());
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(PrinterConstants.LINE_BREAK);
		// Open and close time
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date.from"));
		printerData.add(": ");
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(formatDate(b.getOpenTime()));
		printerData.add(" ");
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.date.to"));
		printerData.add(": ");
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(formatDate(new Date()));
		printerData.add(PrinterConstants.LINE_BREAK);
		// Residence time
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.residence.time"));
		printerData.add(": ");

		Hours hours = Hours.hoursBetween(LocalDateTime.fromDateFields(b.getOpenTime()),
				LocalDateTime.fromDateFields(new Date()));

		Minutes minutes = Minutes.minutesBetween(LocalDateTime.fromDateFields(b.getOpenTime()),
				LocalDateTime.fromDateFields(new Date()));

		if (hours.getHours() == 0) {
			printerData.add(String.valueOf(minutes.getMinutes()) + " minutos");
		} else {
			printerData.add((String.valueOf(hours.getHours()).contentEquals("1") ? String.valueOf(hours.getHours())
					+ " hora" : String.valueOf(hours.getHours()) + " horas")
					+ " e "
					+ String.valueOf(minutes.getMinutes() > 59 ? minutes.getMinutes() - hours.getHours() * 60 : minutes
							.getMinutes()) + " minutos");
		}
		printerData.add(PrinterConstants.LINE_BREAK);
		// Employee name
		if (b.getOrders() != null && !b.getOrders().isEmpty() && b.getOrders().get(0) != null
				&& b.getOrders().get(0).getEmployee() != null
				&& b.getOrders().get(0).getEmployee().getFirstName() != null) {
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.serveur"));
			printerData.add(": ");
			printerData.add(b.getOrders().get(0).getEmployee().getFirstName().toUpperCase() + " ");
			printerData.add(PrinterConstants.HORIZONTAL_TAB);
		}
		// People number
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.peoplenumber"));
		printerData.add(": ");
		printerData.add(b.getPeopleNumber().toString());
		printerData.add(PrinterConstants.LINE_BREAK);

		// It is not fiscal
		printerData.add("--------------------------------------------------");
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.ALLINGMENT_CENTER);
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.not.fiscal"));
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add("--------------------------------------------------");
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.ALLINGMENT_LEFT);

		// trata dados para impressão
		List<MealOrder> meals = new ArrayList<MealOrder>();
		List<BeverageOrder> beverages = new ArrayList<BeverageOrder>();
		for (Orders o : b.getOrders()) {
			meals.addAll(o.getMealsOrder());
			beverages.addAll(o.getBeveragesOrder());
		}

		// Meals header
		if (!meals.isEmpty()) {
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(PrinterConstants.UNDERLINE_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.meals"));
			printerData.add(PrinterConstants.UNDERLINE_OFF);
			printerData.add(": ");
			printerData.add(PrinterConstants.LINE_BREAK);
			// Meals
			for (MealOrder mealOrder : meals) {
				printerData.add(PrinterConstants.FONT_SMALL);
				printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(mealOrder.getName());
				printerData.add(PrinterConstants.HORIZONTAL_TAB);
				if (mealOrder.getName().length() <= 15) {
					printerData.add(PrinterConstants.HORIZONTAL_TAB);
				}
				if (mealOrder.getName().length() <= 5) {
					printerData.add(PrinterConstants.HORIZONTAL_TAB);
				}
				printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
				printerData.add(formatDouble(mealOrder.getPrice().doubleValue()));
				printerData.add(PrinterConstants.BOLD_OFF);
				printerData.add(PrinterConstants.LINE_BREAK);
				// Meal payed ingredients
				for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
					if (mealOrderIngredient.getPrice() != null && mealOrderIngredient.getPrice() > 0.0) {
						printerData.add(PrinterConstants.FONT_SMALL);
						printerData.add("+ " + mealOrderIngredient.getName());
						printerData.add(PrinterConstants.HORIZONTAL_TAB);
						if (mealOrderIngredient.getName().length() <= 15) {
							printerData.add(PrinterConstants.HORIZONTAL_TAB);
						}
						if (mealOrderIngredient.getName().length() <= 5) {
							printerData.add(PrinterConstants.HORIZONTAL_TAB);
						}
						printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
						printerData.add(formatDouble(mealOrderIngredient.getPrice()));
						printerData.add(PrinterConstants.BOLD_OFF);
						printerData.add(PrinterConstants.LINE_BREAK);
					}
				}
			}
		}

		// Beverages header
		if (!beverages.isEmpty()) {
			printerData.add("--------------------------------------------------");
			printerData.add(PrinterConstants.ALLINGMENT_LEFT);
			printerData.add(PrinterConstants.FONT_SMALL);
			printerData.add(PrinterConstants.UNDERLINE_ON);
			printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.beverages"));
			printerData.add(PrinterConstants.UNDERLINE_OFF);
			printerData.add(": ");
			printerData.add(PrinterConstants.LINE_BREAK);

			// Beverages names
			for (BeverageOrder beverageOrder : beverages) {
				printerData.add(PrinterConstants.FONT_SMALL);
				printerData.add(PrinterConstants.ALLINGMENT_LEFT);
				printerData.add(PrinterConstants.BOLD_ON);
				printerData.add(beverageOrder.getName());
				printerData.add(PrinterConstants.HORIZONTAL_TAB);
				if (beverageOrder.getName().length() <= 15) {
					printerData.add(PrinterConstants.HORIZONTAL_TAB);
				}
				if (beverageOrder.getName().length() <= 5) {
					printerData.add(PrinterConstants.HORIZONTAL_TAB);
				}
				printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
				printerData.add(formatDouble(beverageOrder.getPrice()));
				printerData.add(PrinterConstants.BOLD_OFF);
				printerData.add(PrinterConstants.LINE_BREAK);
			}
			printerData.add("--------------------------------------------------");
		} else {
			printerData.add("--------------------------------------------------");
		}

		// SubTotal
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.subtotal"));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		printerData.add(formatDouble(b.getBill().doubleValue()));
		printerData.add(PrinterConstants.LINE_BREAK);
		// Service tax
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.service.tax"));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		printerData.add(formatDouble(b.getBill().setScale(2).doubleValue() * 0.1));
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add("--------------------------------------------------");

		// Total
		printerData.add(PrinterConstants.BOLD_ON);
		printerData.add(PrinterConstants.FONT_WIDE);
		printerData.add(PrinterConstants.UNDERLINE_ON);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.total"));
		printerData.add(PrinterConstants.UNDERLINE_OFF);
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		printerData.add(formatDouble(b.getBill().add(b.getBill().setScale(2).multiply(new BigDecimal(0.1)).setScale(2))
				.doubleValue()));
		printerData.add(PrinterConstants.LINE_BREAK);
		printerData.add(PrinterConstants.BOLD_OFF);
		printerData.add(PrinterConstants.FONT_NORMAL);
		printerData.add(LocaleResource.getProperty(locale).getProperty("order.print.board.splitby"));
		printerData.add(PrinterConstants.HORIZONTAL_TAB);
		printerData.add(LocaleResource.getProperty(locale).getProperty("CURRENCY"));
		printerData.add(formatDouble(b.getBill().add(b.getBill().multiply(new BigDecimal(0.1)))
				.divide(new BigDecimal(b.getPeopleNumber())).doubleValue()));
		printerData.add(PrinterConstants.LINE_BREAK);

		// End of printing
		printerData.add(PrinterConstants.CUT_PAPER);
		printerData.add(PrinterConstants.CLEAR);
		printerData.add(PrinterConstants.UTF8);

		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {
			return LocaleResource.getProperty(locale).getProperty("exception.order.print");
		}
		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

	private static String formatDate(Date orderDate) {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return fmt.format(orderDate);
	}

	private static String formatDouble(Double number) {
		return String.format("%.2f", number);
	}

	/**
	 * Método para tentar achar o status do papel. Não foi possível colocá-lo em
	 * prática ainda.
	 * 
	 * @param printer
	 * @param clientIP
	 * @param locale
	 * @return
	 */
	public static String findPaperStatus(Printer printer, String clientIP, String locale) {
		List<String> printerData = new ArrayList<String>();
		printerData.add(PrinterConstants.PAPER_STATUS);
		try {
			PrintClient.sendToPrinter(clientIP, printer, printerData, locale);
		} catch (Exception e) {
			return LocaleResource.getProperty(locale).getProperty("exception.order.print");
		}
		return LocaleResource.getProperty(locale).getProperty("order.print");
	}

}

// TODO: problema q eu sei: se pedir uma pizza 1/3 de um sabor, e 2/3 de outro
// sabor, só vai imprimir 2 sabores, não sendo possível saber qual deles é o que
// possui 2/3