package br.com.redefood.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.Hibernate;

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.mail.notificator.Notificator;
import br.com.redefood.mail.notificator.restaurant.SubsidiaryOrderReceivedNotificator;
import br.com.redefood.mail.notificator.user.OrderCanceledNotificator;
import br.com.redefood.mail.notificator.user.OrderPlacedNotificator;
import br.com.redefood.mail.notificator.user.OrderWaitingNotificator;
import br.com.redefood.model.Address;
import br.com.redefood.model.Beverage;
import br.com.redefood.model.BeverageOrder;
import br.com.redefood.model.Board;
import br.com.redefood.model.Configuration;
import br.com.redefood.model.DeliveryArea;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Ingredient;
import br.com.redefood.model.Login;
import br.com.redefood.model.Meal;
import br.com.redefood.model.MealIngredientTypes;
import br.com.redefood.model.MealIngredientTypeshasIngredient;
import br.com.redefood.model.MealOrder;
import br.com.redefood.model.MealOrderIngredient;
import br.com.redefood.model.OrderPaymentMethod;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Orders;
import br.com.redefood.model.PaymentMethod;
import br.com.redefood.model.Printer;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.User;
import br.com.redefood.model.complex.EmailDataDTO;
import br.com.redefood.model.complex.RedeFoodIP;
import br.com.redefood.model.enumtype.OrderOrigin;
import br.com.redefood.model.enumtype.OrderStatus;
import br.com.redefood.model.enumtype.TypeOrder;
import br.com.redefood.service.PrintService;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.LocaleResource;
import br.com.redefood.util.RedeFoodAnswerGenerator;
import br.com.redefood.util.RedeFoodConstants;
import br.com.redefood.util.RedeFoodMailUtil;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class OrderResource extends HibernateMapper {

	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@SuppressWarnings("unchecked")
	@Securable
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/orders")
	@Produces("application/json;charset=UTF8")
	public String findOrdersBySubsidiary(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @DefaultValue("") @QueryParam("status") String status,
			@DefaultValue("6") @QueryParam("fetchtime") Short fetchTime,
			@DefaultValue("0") @QueryParam("ordertype") Short idOrderType,
			@DefaultValue("1") @QueryParam("offset") Integer offset,
			@DefaultValue("30") @QueryParam("limit") Integer limit) {

		try {

			List<Orders> orders = null;

			Timestamp orderTime = new Timestamp(System.currentTimeMillis() - fetchTime.longValue() * 60L * 60L * 1000L);

			if (status != null && !status.contentEquals("")) {
				orders = em.createNamedQuery(Orders.FIND_BY_ORDERSTATUS_AND_TIME)
						.setParameter("idSubsidiary", idSubsidiary)
						.setParameter("orderStatus", OrderStatus.valueOf(status.toUpperCase()))
						.setParameter("fetchTime", orderTime).setMaxResults(limit).setFirstResult(offset - 1)
						.getResultList();
			} else {
				Query query = null;
				if (idOrderType != 0) {
					query = em.createNamedQuery(Orders.FIND_BY_SUBSIDIARY_AND_ORDERTYPE)
							.setParameter("orderTime", orderTime).setParameter("idSubsidiary", idSubsidiary)
							.setParameter("idOrderType", idOrderType);
				} else {
					query = em.createNamedQuery(Orders.FIND_BY_SUBSIDIARY_AND_TIME)
							.setParameter("orderTime", orderTime).setParameter("idSubsidiary", idSubsidiary);
				}
				orders = query.setMaxResults(limit).setFirstResult(offset - 1).getResultList();
			}

			for (Orders order : orders) {
				if (order.getAddress() != null) {
					Hibernate.initialize(order.getAddress().getNeighborhood());
					Hibernate.initialize(order.getAddress().getCity());
				}

				Hibernate.initialize(order.getBeveragesOrder());

				Hibernate.initialize(order.getOrderType());

				for (OrderPaymentMethod opm : order.getOrderPaymentMethod()) {
					Hibernate.initialize(opm.getPaymentMethod());
				}

				if (order.getUser() != null) {
					Hibernate.initialize(order.getUser());
				}
				if (order.getEmployee() != null) {
					Hibernate.initialize(order.getEmployee());
				}

				for (MealOrder mealOrder : order.getMealsOrder()) {
					Hibernate.initialize(mealOrder.getMealOrderIngredients());
				}

				for (OrderPaymentMethod opm : order.getOrderPaymentMethod()) {
					Hibernate.initialize(opm.getPaymentMethod());
				}

			}
			Long count = (Long) em.createNamedQuery(Orders.COUNT_ORDERS_BY_SUBSIDIARY)
					.setParameter("orderTime", orderTime).setParameter("idSubsidiary", idSubsidiary).getSingleResult();

			HashMap<String, Object> ordersCount = new HashMap<String, Object>();
			ordersCount.put("orders", orders);
			ordersCount.put("count", count);

			return mapper.writeValueAsString(ordersCount);

		} catch (Exception e) {
			if (e.getMessage().contains("No enum const"))
				return eh.orderExceptionHandlerString(new Exception("no enum const"), locale, status);
			if (e.getMessage().contentEquals("max fetchtime"))
				return eh.orderExceptionHandlerString(e, locale, null);
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@Securable
	@POST
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order")
	@Consumes("application/json")
	public Response newOrder(@HeaderParam("token") String token, @HeaderParam("subsidiaryIP") RedeFoodIP subsidiaryIP,
			@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary, Orders order,
			@QueryParam("idBoard") Integer idBoard, @QueryParam("preventPrint") Boolean preventPrint) {

		try {

			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			if (subsidiary == null)
				throw new Exception("subsidiary not found");

			OrderType ot = em.find(OrderType.class, order.getOrderType().getId());
			if (!subsidiary.getOrderTypes().contains(ot))
				throw new Exception("ordertype not accepted");

			Login login = em.find(Login.class, token);
			if (login == null)
				throw new Exception("user not found");

			Integer min = (Integer) em.createNamedQuery(User.FIND_USER_MIN_ID).getSingleResult();
			if (min == null) {
				min = new Integer(100000);
			}
			User user = null;
			Employee employee = null;
			if (login.getIdUser() >= min) {
				user = em.find(User.class, login.getIdUser());
				if (user == null)
					throw new Exception("user not found");
				if (user.getCpf() == null || user.getCpf().length() < 11)
					throw new Exception("invalid cpf");
				if (user.getCellphone() == null || user.getCellphone().length() < 10)
					throw new Exception("order wo cellphone");
			} else {
				employee = em.find(Employee.class, (short) login.getIdUser());
				if (employee == null)
					throw new Exception("user not found");
			}

			BigDecimal orderPrice = new BigDecimal(0).setScale(2);
			DeliveryArea deliver = null;

			if (order.getAddress() != null && order.getAddress().getId() != null) {
				Address address = em.find(Address.class, order.getAddress().getId());

				if (address == null
						&& (order.getOrderType().getId().intValue() == 1
								|| order.getOrderType().getId().intValue() == 2 || order.getOrderType().getId()
								.intValue() == 3))
					throw new Exception("address not found");

				if (address.getUser().getId() != (user.getId() == null ? employee.getId() : user.getId())
						&& (order.getOrderType().getId().intValue() == 1
								|| order.getOrderType().getId().intValue() == 2 || order.getOrderType().getId()
								.intValue() == 3))
					throw new Exception("address not bound to user");

				deliver = validateDeliveryArea(subsidiary, address);
				orderPrice = orderPrice.add(new BigDecimal(deliver.getTax()));
				order.setAddress(address);
			}

			order.setSubsidiary(subsidiary);
			if (user != null) {
				order.setUser(user);
			}
			if (employee != null) {
				order.setEmployee(employee);
			}

			List<MealOrder> mealsOrder = new ArrayList<MealOrder>();
			List<BeverageOrder> beveragesOrder = new ArrayList<BeverageOrder>();
			List<Beverage> beverageAux = new ArrayList<Beverage>();
			List<Meal> mealAux = new ArrayList<Meal>();

			if (order.getMeals() != null && !order.getMeals().isEmpty()) {
				for (Meal meal : order.getMeals()) {
					Meal m = em.find(Meal.class, meal.getId());

					// verifica se o produto está em estoque
					verifyOutOfStock(m);

					orderPrice = orderPrice.add(m.getPrice());
					List<MealOrderIngredient> mealOrderIngs = new ArrayList<MealOrderIngredient>();

					for (MealIngredientTypes mit : meal.getMealIngredientTypes()) {
						// Adiciona os ingredientes de um Meal à tabela de
						// histórico
						for (MealIngredientTypeshasIngredient miti : mit.getMealIngredientTypeshasIngredient()) {

							MealIngredientTypeshasIngredient mithi = em.find(MealIngredientTypeshasIngredient.class,
									miti.getId());
							if (mithi == null)
								throw new Exception("ingredient not found");
							if (mithi.getPrice() == null)
								throw new Exception("ingredient price ." + mithi.getIngredient().getName());
							if (mithi.getPrice() != 0) {
								orderPrice = orderPrice.add(new BigDecimal(mithi.getPrice()));
							}

							// verifica se o produto está em estoque
							verifyOutOfStock(mithi.getIngredient());

							MealOrderIngredient mealOrderIng = new MealOrderIngredient(mithi.getIngredient().getId(),
									mithi.getIngredient().getName(), mithi.getIngredient().getIngredientType().getId(),
									mithi.getIngredient().getIngredientType().getName(), mithi.getPrice());
							mealOrderIngs.add(mealOrderIng);
						}
					}
					// Adiciona um Meal à tabela de histórico
					MealOrder mealOrder = new MealOrder(m.getId(), m.getName(), m.getMealType().getId(), m
							.getMealType().getName(), m.getPrice(), order, mealOrderIngs, meal.getNote());
					meal.setNote(null);

					for (MealOrderIngredient moi : mealOrder.getMealOrderIngredients()) {
						moi.setMealOrder(mealOrder);
					}
					mealsOrder.add(mealOrder);
					mealAux.add(m);
				}
				order.getMeals().clear();
				order.getMeals().addAll(mealAux);
			}

			// Adiciona as bebidas ao histórico
			if (order.getBeverages() != null && !order.getBeverages().isEmpty()) {
				for (Beverage beverage : order.getBeverages()) {

					Beverage bev = null;
					try {
						bev = (Beverage) em.createNamedQuery(Beverage.FIND_BY_SUBSIDIARY_AND_BEVERAGE)
								.setParameter("idSubsidiary", subsidiary.getId())
								.setParameter("idBeverage", beverage.getId().shortValue()).getSingleResult();

					} catch (Exception e) {
						if (e.getMessage().contains("No entity found for query"))
							throw new Exception("beverage not found");
						else
							throw new Exception("duplicated beverage for restaurant");
					}
					if (bev == null)
						throw new Exception("beverage not found");

					verifyOutOfStock(bev);

					BeverageOrder beverageOrder = new BeverageOrder(bev.getId(), bev.getName(), bev.getPrice(), order,
							beverage.getNote());
					bev.setNote(null);

					beveragesOrder.add(beverageOrder);
					orderPrice = orderPrice.add(new BigDecimal(bev.getPrice()));

					beverageAux.add(bev);

				}
				order.getBeverages().clear();
				order.getBeverages().addAll(beverageAux);
			}

			if (beveragesOrder != null) {
				order.setBeveragesOrder(beveragesOrder);
			}
			if (mealsOrder != null) {
				order.setMealsOrder(mealsOrder);
			}
			if (subsidiary.getMinOrder() != null && order.getOrderType().getId().intValue() == 1) {
				BigDecimal subtract = orderPrice.subtract(new BigDecimal(deliver.getTax()));
				if (orderPrice.compareTo(new BigDecimal(subsidiary.getMinOrder())) < 0
						|| subtract.compareTo(new BigDecimal(0)) == 0)
					throw new Exception("order price minimum");
			}

			if ((order.getOrderType().getId().intValue() == 4 || order.getOrderType().getId().intValue() == 5 || order
					.getOrderType().getId().intValue() == 8)
					&& orderPrice.compareTo(new BigDecimal(0)) <= 0)
				throw new Exception("order price minimum");

			// find Board
			Board board = null;
			if (idBoard != null) {
				board = em.find(Board.class, idBoard);
			}

			Orders toPersist = validateOrder(order, orderPrice, idSubsidiary, board);

			// persist order
			em.persist(toPersist);

			// add to board if board exists
			if (board != null) {
				if (board.getOrders() == null) {
					List<Orders> orders = new ArrayList<Orders>();
					toPersist.setBoard(board);
					orders.add(toPersist);
					board.setOrders(orders);
				} else {
					toPersist.setBoard(board);
					board.getOrders().add(toPersist);
				}
				// board.setCredit(0.0 + board.getCredit());
				board.setBill(orderPrice.add(board.getBill()).setScale(2));

				em.merge(board);
			}

			em.flush();

			String answer = "";
			if (board == null) {
				answer = LocaleResource.getString(locale, "order.created", order.getId(),
						user == null ? employee.getId() : user.getId(), subsidiary.getId());
			} else {
				answer = LocaleResource.getString(locale, "order.created.board", order.getId(), board.getNumber(),
						user == null ? employee.getId() : user.getId(), subsidiary.getId());
			}

			log.log(Level.INFO, answer);

			// Send order to User e-mail address
			if (order.getOrderType().getType() != TypeOrder.LOCAL
					&& order.getUser().getNotifications().getEmailAction()) {
				sendOrderToUserEmail(toPersist,
						em.find(PaymentMethod.class, order.getOrderPaymentMethod().get(0).getPaymentMethod().getId()));
			}

			// Prevent re-print - this may occur if an item from an order is
			// cancelled. As the order is re-created
			// this prevents the order from be printed again.
			if (preventPrint == null || !preventPrint) {
				// Get printer parameters
				Configuration config = em.find(Configuration.class, order.getSubsidiary().getConfiguration().getId());

				if (config != null) {
					// Send order to Subsidiary e-mail address
					if (order.getSubsidiary().getConfiguration().isReceiveOrdersByMail()
							&& order.getOrderType().getType() != TypeOrder.LOCAL) {
						sendOrderToSubsidiaryEmail(
								toPersist,
								em.find(PaymentMethod.class, order.getOrderPaymentMethod().get(0).getPaymentMethod()
										.getId()));
					}

					// Return if configured to not print the order
					for (Printer printer : config.getPrinters()) {
						if (!printer.getPrintDelivery() && !printer.getPrintOrder() && !printer.getPrintOrderClient()
								&& !printer.getPrintOrderNumber()) {
							if (order.getOrderType().getType() != TypeOrder.LOCAL)
								return RedeFoodAnswerGenerator.generateSuccessOrderPOST(toPersist.getId(),
										toPersist.getTotalOrderNumber(), 201, null);
							else
								return RedeFoodAnswerGenerator.generateSuccessOrderPOST(toPersist.getId(),
										toPersist.getLocalOrderNumber(), 201, null);
						}
					}
				}

				try {
					// Só executa se não for ONLINE
					if (order.getOrderType().getType() != TypeOrder.ONLINE) {
						if (subsidiaryIP == null)
							throw new Exception("subsidiary ip null");
						PaymentMethod pm = null;
						if (board == null) {
							pm = em.find(PaymentMethod.class, order.getOrderPaymentMethod().get(0).getPaymentMethod()
									.getId());
						}
						log.log(Level.INFO,
								PrintService.print(toPersist, locale, config, subsidiaryIP.getSubsidiaryIP(), pm));
					}

				} catch (Exception e) {
					return RedeFoodAnswerGenerator.generateSuccessOrderPOST(toPersist.getId(),
							toPersist.getLocalOrderNumber(), 201, eh.printExceptionHandlerString(e, locale));
				}
			}
			// Resposta para pedidos LOCAL e PHONE
			if (toPersist.getOrderType().getType() != TypeOrder.ONLINE)
				return RedeFoodAnswerGenerator.generateSuccessOrderPOST(toPersist.getId(),
						toPersist.getLocalOrderNumber(), 201, null);

			// Resposta para pedidos ONLINE
			return RedeFoodAnswerGenerator.generateSuccessOrderPOST(toPersist.getId(), toPersist.getTotalOrderNumber(),
					201, null);

		} catch (Exception e) {
			return eh.orderExceptionHandlerResponse(e, locale);
		}
	}

	@Securable
	@POST
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order/{idOrder:[0-9][0-9]*}/print/{idPrinter:[0-9][0-9]*}")
	public Response printOrder(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idOrder") Integer idOrder, @PathParam("idPrinter") Short idPrinter,
			@QueryParam("template") String template, @HeaderParam("subsidiaryIP") RedeFoodIP subsidiaryIP) {

		try {
			if (template == null || template.isEmpty())
				throw new Exception("template null");

			Orders order = em.find(Orders.class, idOrder);
			if (order == null || order.getSubsidiary() == null) {
				System.err.println("Order null when printing!");
			}

			Configuration config = order != null ? order.getSubsidiary().getConfiguration() : null;

			Printer printer = em.find(Printer.class, idPrinter);

			validatePrint(config, subsidiaryIP, printer);

			String answer = "";
			String[] templates = template.split(",");
			for (String temp : templates) {
				switch (temp) {
				case "printOrder":
					answer = PrintService.printOrderKitchen(order, locale, printer, subsidiaryIP.getSubsidiaryIP());
					Thread.sleep(250);
					break;
				case "printOrderClient":
					answer = PrintService.printOrderClient(order, locale, em.find(Printer.class, idPrinter),
							subsidiaryIP.getSubsidiaryIP());
					Thread.sleep(250);
					break;
				case "printOrderNumber":
					answer = PrintService.printOrderNumber(order, locale, em.find(Printer.class, idPrinter),
							subsidiaryIP.getSubsidiaryIP());
					Thread.sleep(250);
					break;
				case "printDelivery":
					if (order.getUser() == null)
						throw new Exception("print delivery user null");
					answer = PrintService.printDelivery(
							order,
							locale,
							em.find(Printer.class, idPrinter),
							subsidiaryIP.getSubsidiaryIP(),
							em.find(PaymentMethod.class, order.getOrderPaymentMethod().get(0).getPaymentMethod()
									.getId()));
					Thread.sleep(250);
					break;
				default:
					answer = LocaleResource.getProperty(locale).getProperty("exception.order.print.template.invalid");
					break;
				}
			}

			log.log(Level.INFO, "Trying to print order "
					+ (order != null ? String.valueOf(order.getId()) : "order null") + " to printer: "
					+ config.getPrinters().get(0).getName() + " with ip " + config.getPrinters().get(0).getIp()
					+ " - Subsidiary IP: " + subsidiaryIP.getSubsidiaryIP() + " .Answer: " + answer);

			if (answer.contentEquals("Não foi possível se comunicar com a impressora ou ela está desconectada."))
				return RedeFoodAnswerGenerator.generateErrorAnswer(400, answer);
			return Response.status(200).build();
		} catch (Exception e) {
			return eh.printExceptionHandlerResponse(e, locale);
		}
	}

	public void validatePrint(Configuration config, RedeFoodIP subsidiaryIP, Printer printer) throws Exception {
		if (printer == null)
			throw new Exception("no printer");
		if (config == null)
			throw new Exception("config null");

		if (config.getPrinters() == null)
			throw new Exception("no printer");

		if (config.getPrinters().get(0) == null)
			throw new Exception("no printer");

		if (config.getPrinters().get(0).getName() == null || config.getPrinters().get(0).getName().isEmpty()
				|| config.getPrinters().get(0).getName().length() < 1)
			throw new Exception("no printer name");

		if (config.getPrinters().get(0).getIp() == null || config.getPrinters().get(0).getIp().isEmpty()
				|| config.getPrinters().get(0).getIp().length() < 7)
			throw new Exception("no printer ip");

		if (subsidiaryIP == null)
			throw new Exception("subsidiary ip null");

		if (subsidiaryIP.getSubsidiaryIP() == null || subsidiaryIP.getSubsidiaryIP().isEmpty()
				|| subsidiaryIP.getSubsidiaryIP().length() < 7)
			throw new Exception("subsidiaryip null");

	}

	private DeliveryArea validateDeliveryArea(Subsidiary subsidiary, Address address) throws Exception {
		DeliveryArea deliver = null;
		try {
			deliver = (DeliveryArea) em.createNamedQuery(DeliveryArea.FIND_DELIVER_EXISTS)
					.setParameter("idSubsidiary", subsidiary.getId())
					.setParameter("idNeighborhood", address.getNeighborhood().getId()).getSingleResult();
		} catch (Exception e) {
			throw new Exception("cannot deliver address");
		}
		if (deliver == null)
			throw new Exception("cannot deliver address");

		return deliver;
	}

	private Orders validateOrder(Orders order, BigDecimal orderPrice, Short idSubsidiary, Board board) throws Exception {
		if (order.getOrderType() == null)
			throw new Exception("orderType null");
		if (order.getTotalPrice() == null)
			throw new Exception("totalPrice null");
		if (order.getOrderPaymentMethod() == null && board == null)
			throw new Exception("paymentMethod null");
		BigDecimal clientPrice = new BigDecimal(order.getTotalPrice());
		clientPrice = clientPrice.setScale(2, RoundingMode.HALF_UP);
		orderPrice.setScale(2, RoundingMode.HALF_UP);
		if (orderPrice.compareTo(clientPrice) != 0) {
			log.log(Level.WARNING,
					"Subsidiary:" + order.getSubsidiary().getId() + " | TotalPrice: " + order.getTotalPrice()
							+ " | Calculated Price: " + orderPrice);
			throw new Exception("totalPrice wrong");
		}

		// Increase LocalOrder
		if (order.getOrderType().getId() == OrderType.LOCAL || order.getOrderType().getId() == OrderType.LOCAL_TO_GO
				|| order.getOrderType().getId() == OrderType.EMPLOYEE_MEAL) {
			Integer lastLocalOrderNumber = (Integer) em.createNamedQuery(Orders.FIND_LAST_LOCAL_ORDER_NUMBER)
					.setParameter("idSubsidiary", idSubsidiary).getSingleResult();

			if (lastLocalOrderNumber.intValue() >= 999) {
				order.setLocalOrderNumber(1);
			} else {
				order.setLocalOrderNumber(++lastLocalOrderNumber);
			}
		}

		// Increase TotalOrder
		Integer lastTotalOrderNumber = (Integer) em.createNamedQuery(Orders.FIND_TOTAL_ORDER_NUMBER_BY_SUBSIDIARY)
				.setParameter("idSubsidiary", idSubsidiary).getSingleResult();

		if (lastTotalOrderNumber.intValue() >= 999) {
			order.setTotalOrderNumber(1);
		} else {
			order.setTotalOrderNumber(++lastTotalOrderNumber);
		}

		// fix Payment Methods
		if (board == null) {
			for (OrderPaymentMethod opm : order.getOrderPaymentMethod()) {
				opm.setOrder(order);
			}
		}

		order.setOrderType(em.find(OrderType.class, order.getOrderType().getId()));
		order.setOrderMade(Calendar.getInstance().getTime());
		if (order.getOrderType().getId().intValue() == 4 || order.getOrderType().getId().intValue() == 5
				|| order.getOrderType().getId().intValue() == 8) {
			order.setOrderStatus(OrderStatus.PREPARING);
		} else {
			order.setOrderStatus(OrderStatus.ORDER_SENT);
		}
		return order;
	}

	@Securable
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order/{idOrder:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String findOrderDetails(@HeaderParam("locale") String locale, @HeaderParam("token") String token,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idOrder") Integer idOrder) {
		try {
			// TODO: só usuário dono do pedido pode visualizar, e também apenas
			// funcionários que trabalham na loja supra-citada - criar
			// annotation employee, que verifica além do perfil, se ele trabalha
			// na loja do PathParam

			Orders order = em.find(Orders.class, idOrder);

			if (order == null)
				throw new Exception("order not found");

			Login login = em.find(Login.class, token);
			if (login.getIdUser() > 100000) {
				User user = em.find(User.class, login.getIdUser());
				if (!user.getOrders().contains(order))
					throw new Exception("denied");
			} else {
				Employee employee = em.find(Employee.class, Short.valueOf(String.valueOf(login.getIdUser())));
				int count = 0;
				for (Subsidiary subsidiary : employee.getSubsidiaryList()) {
					if (subsidiary.getId().intValue() != idSubsidiary.intValue()) {
						count++;
					}
				}
				if (count == employee.getSubsidiaryList().size())
					throw new Exception("denied");
			}

			for (MealOrder mealOrder : order.getMealsOrder()) {
				if (mealOrder.getMealOrderIngredients() != null) {
					Hibernate.initialize(mealOrder.getMealOrderIngredients());
				}
			}

			Hibernate.initialize(order.getBeveragesOrder());

			if (order.getAddress() != null && order.getAddress().getCity() != null
					&& order.getAddress().getNeighborhood() != null) {
				Hibernate.initialize(order.getAddress().getCity());
				Hibernate.initialize(order.getAddress().getNeighborhood());
			}

			for (OrderPaymentMethod opm : order.getOrderPaymentMethod()) {
				if (opm.getPaymentMethod() != null) {
					Hibernate.initialize(opm.getPaymentMethod());
				}
			}

			Hibernate.initialize(order.getOrderType());

			if (order.getEmployee() != null) {
				Hibernate.initialize(order.getEmployee());
			}

			return mapper.writeValueAsString(order);

		} catch (Exception e) {
			return eh.orderExceptionHandlerString(e, locale, idOrder.toString());
		}
	}

	// FIXME just a test
	@SuppressWarnings("unchecked")
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order/{idOrder:[0-9][0-9]*}/new2")
	@Produces("application/json;charset=UTF8")
	public String findOrderDetailsNew2(@HeaderParam("locale") String locale, @HeaderParam("token") String token,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idOrder") Integer idOrder) {
		try {
			Orders order = em.find(Orders.class, idOrder);

			LinkedHashMap<String, Object> readValue = mapper.readValue(
					findOrderDetails(locale, token, idSubsidiary, idOrder), LinkedHashMap.class);

			readValue.put("fixedMeals", fixIngredientListString(order.getMealsOrder()));

			return mapper.writeValueAsString(readValue);

		} catch (Exception e) {
			return eh.orderExceptionHandlerString(e, locale, idOrder.toString());
		}
	}

	// FIXME
	// @Securable
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order/{idOrder:[0-9][0-9]*}/new")
	@Produces("application/json;charset=UTF8")
	public String findOrderDetailsNew(@HeaderParam("locale") String locale, @HeaderParam("token") String token,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idOrder") Integer idOrder) {
		try {
			// TODO: só usuário dono do pedido pode visualizar, e também apenas
			// funcionários que trabalham na loja supra-citada - criar
			// annotation employee, que verifica além do perfil, se ele trabalha
			// na loja do PathParam

			Orders order = em.find(Orders.class, idOrder);

			if (order == null)
				throw new Exception("order not found");

			Login login = em.find(Login.class, token);
			if (login.getIdUser() > 100000) {
				User user = em.find(User.class, login.getIdUser());
				if (!user.getOrders().contains(order))
					throw new Exception("denied");
			} else {
				Employee employee = em.find(Employee.class, Short.valueOf(String.valueOf(login.getIdUser())));
				int count = 0;
				for (Subsidiary subsidiary : employee.getSubsidiaryList()) {
					if (subsidiary.getId().intValue() != idSubsidiary.intValue()) {
						count++;
					}
				}
				if (count == employee.getSubsidiaryList().size())
					throw new Exception("denied");
			}

			LinkedHashMap<String, Object> orderMap = new LinkedHashMap<String, Object>();
			orderMap.put("totalPrice", order.getTotalPrice());
			orderMap.put("orderStatus", order.getOrderStatus());
			orderMap.put("note", order.getNote());
			orderMap.put("orderMade", order.getOrderMade());
			orderMap.put("orderSent", order.getOrderSent());
			orderMap.put("discountCoupon", order.getDiscountCoupon());

			HashMap<String, Object> orderType = new HashMap<String, Object>();
			orderType.put("id", order.getOrderType().getId());
			orderType.put("name", order.getOrderType().getName());
			orderType.put("type", order.getOrderType().getType());

			orderMap.put("orderType", orderType);
			orderMap.put("beveragesOrder", order.getBeveragesOrder());
			orderMap.put("mealsOrder", fixIngredientListString(order.getMealsOrder()));

			return mapper.writeValueAsString(orderMap);

		} catch (Exception e) {
			return eh.orderExceptionHandlerString(e, locale, idOrder.toString());
		}
	}

	// FIXME: mudar para método privado, que recebe a lista de mealsOrder, e
	// retorna o objeto para posteriormente ser processsado
	private HashMap<String, List<HashMap<String, Object>>> fixIngredientListString(List<MealOrder> meals)
			throws JsonProcessingException {

		HashMap<String, List<HashMap<String, Object>>> mealsGroupedByType = new HashMap<String, List<HashMap<String, Object>>>();
		List<Object> mealsOrderList = new ArrayList<Object>();

		HashMap<String, Object> mealsOrder = new HashMap<String, Object>();

		// começa a tratar os ingredientes
		for (MealOrder mealOrder : meals) {
			List<HashMap<String, String>> removedIngredients = new ArrayList<HashMap<String, String>>();

			List<HashMap<String, String>> addedIngredients = new ArrayList<HashMap<String, String>>();
			List<HashMap<String, String>> addedOptionals = new ArrayList<HashMap<String, String>>();
			List<HashMap<String, String>> ingredientsNotMultiple = new ArrayList<HashMap<String, String>>();
			for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
				// lista com todos os ingredientes adicionados
				if (mealOrderIngredient.getPrice() == 0.0) {
					HashMap<String, String> moi = new HashMap<String, String>();
					moi.put("name", mealOrderIngredient.getName());
					moi.put("ingredientType", mealOrderIngredient.getIngredientTypeName());
					addedIngredients.add(moi);

				}

				// lista com todos os opcionais adicionados
				if (mealOrderIngredient.getPrice() != 0.0) {
					HashMap<String, String> moi = new HashMap<String, String>();
					moi.put("name", mealOrderIngredient.getName());
					moi.put("ingredientType", mealOrderIngredient.getIngredientTypeName());
					addedOptionals.add(moi);
				}

			}

			Meal meal = em.find(Meal.class, mealOrder.getIdMeal());

			for (MealIngredientTypes mit : meal.getMealIngredientTypes()) {
				for (MealIngredientTypeshasIngredient mithi : mit.getMealIngredientTypeshasIngredient()) {

					// lista com todos os ingredientes inclusos no produto
					if ((mithi.getPrice() == null || mithi.getPrice() == 0.0) && mit.getMultiple()) {
						HashMap<String, String> moi = new HashMap<String, String>();
						moi.put("name", mithi.getIngredient().getName());
						moi.put("ingredientType", mithi.getIngredient().getIngredientType().getName());
						removedIngredients.add(moi);
					}
					if ((mithi.getPrice() == null || mithi.getPrice() == 0.0) && !mit.getMultiple()) {
						HashMap<String, String> moi = new HashMap<String, String>();
						moi.put("name", mithi.getIngredient().getName());
						moi.put("ingredientType", mithi.getIngredient().getIngredientType().getName());
						ingredientsNotMultiple.add(moi);
					}
				}
			}

			// nesta lista vai sobrar os outros ingredientes sem preço que
			// precisam ser impressos, e q nao sao multiplos
			ingredientsNotMultiple.removeAll(removedIngredients);

			removedIngredients.removeAll(addedIngredients); // removed
			// ingredients done

			for (HashMap<String, String> hashMap : ingredientsNotMultiple) {
				if (addedIngredients.contains(hashMap)) {
					HashMap<String, String> moi = new HashMap<String, String>();
					moi.put("name", hashMap.get("name"));
					moi.put("ingredientType", hashMap.get("ingredientType"));
					addedOptionals.add(moi);
				}
			}
			// added optionals beyond already included ingredients, done

			HashMap<String, Object> ingredientsEdit = new HashMap<String, Object>();

			// group elements by categoryName
			HashMap<String, List<String>> removedIngredientsGroupedByType = groupElementsByKey(removedIngredients,
					"ingredientType", "name");

			HashMap<String, List<String>> addedOptionalsGroupedByType = groupElementsByKey(addedOptionals,
					"ingredientType", "name");

			ingredientsEdit.put("removed", removedIngredientsGroupedByType);
			ingredientsEdit.put("added", addedOptionalsGroupedByType);

			mealsOrder.put(mealOrder.getName(), ingredientsEdit);
			mealsOrder.put("mealType", mealOrder.getMealTypeName());

			if (mealsOrderList.contains(mealOrder.getMealTypeName())) {
				HashMap<String, Object> aux = new HashMap<String, Object>();
				aux.put(mealOrder.getName(), mealsOrder.get(mealOrder.getName()));
				mealsGroupedByType.get(mealsOrder.get("mealType")).add(aux);
			} else {
				HashMap<String, Object> aux = new HashMap<String, Object>();
				List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
				list.add(aux);
				aux.put(mealOrder.getName(), mealsOrder.get(mealOrder.getName()));
				mealsGroupedByType.put((String) mealsOrder.get("mealType"), list);
				mealsOrderList.add(mealOrder.getMealTypeName());
			}

		}// closes 1st for

		return mealsGroupedByType;
	}

	// FIXME TESTE
	@GET
	@Path("/order/{idOrder:[0-9][0-9]*}/fix")
	@Produces("application/json;charset=UTF8")
	public String fixIngredientList(@PathParam("idOrder") Integer idOrder) throws JsonProcessingException {

		HashMap<String, List<HashMap<String, Object>>> mealsGroupedByType = new HashMap<String, List<HashMap<String, Object>>>();
		List<Object> mealsOrderList = new ArrayList<Object>();

		HashMap<String, Object> mealsOrder = new HashMap<String, Object>();

		// começa a tratar os ingredientes
		for (MealOrder mealOrder : em.find(Orders.class, idOrder).getMealsOrder()) {
			List<HashMap<String, String>> removedIngredients = new ArrayList<HashMap<String, String>>();

			List<HashMap<String, String>> addedIngredients = new ArrayList<HashMap<String, String>>();
			List<HashMap<String, String>> addedOptionals = new ArrayList<HashMap<String, String>>();
			List<HashMap<String, String>> ingredientsNotMultiple = new ArrayList<HashMap<String, String>>();
			for (MealOrderIngredient mealOrderIngredient : mealOrder.getMealOrderIngredients()) {
				// lista com todos os ingredientes adicionados
				if (mealOrderIngredient.getPrice() == 0.0) {
					HashMap<String, String> moi = new HashMap<String, String>();
					moi.put("name", mealOrderIngredient.getName());
					moi.put("ingredientType", mealOrderIngredient.getIngredientTypeName());
					addedIngredients.add(moi);

				}

				// lista com todos os opcionais adicionados
				if (mealOrderIngredient.getPrice() != 0.0) {
					HashMap<String, String> moi = new HashMap<String, String>();
					moi.put("name", mealOrderIngredient.getName());
					moi.put("ingredientType", mealOrderIngredient.getIngredientTypeName());
					addedOptionals.add(moi);
				}

			}

			Meal meal = em.find(Meal.class, mealOrder.getIdMeal());

			for (MealIngredientTypes mit : meal.getMealIngredientTypes()) {
				for (MealIngredientTypeshasIngredient mithi : mit.getMealIngredientTypeshasIngredient()) {

					// lista com todos os ingredientes inclusos no produto
					if ((mithi.getPrice() == null || mithi.getPrice() == 0.0) && mit.getMultiple()) {
						HashMap<String, String> moi = new HashMap<String, String>();
						moi.put("name", mithi.getIngredient().getName());
						moi.put("ingredientType", mithi.getIngredient().getIngredientType().getName());
						removedIngredients.add(moi);
					}
					if ((mithi.getPrice() == null || mithi.getPrice() == 0.0) && !mit.getMultiple()) {
						HashMap<String, String> moi = new HashMap<String, String>();
						moi.put("name", mithi.getIngredient().getName());
						moi.put("ingredientType", mithi.getIngredient().getIngredientType().getName());
						ingredientsNotMultiple.add(moi);
					}
				}
			}

			// nesta lista vai sobrar os outros ingredientes sem preço que
			// precisam ser impressos, e q nao sao multiplos
			ingredientsNotMultiple.removeAll(removedIngredients);

			removedIngredients.removeAll(addedIngredients); // removed
			// ingredients done

			for (HashMap<String, String> hashMap : ingredientsNotMultiple) {
				if (addedIngredients.contains(hashMap)) {
					HashMap<String, String> moi = new HashMap<String, String>();
					moi.put("name", hashMap.get("name"));
					moi.put("ingredientType", hashMap.get("ingredientType"));
					addedOptionals.add(moi);
				}
			}
			// added optionals beyond already included ingredients, done

			HashMap<String, Object> ingredientsEdit = new HashMap<String, Object>();

			// group elements by categoryName
			HashMap<String, List<String>> removedIngredientsGroupedByType = groupElementsByKey(removedIngredients,
					"ingredientType", "name");

			HashMap<String, List<String>> addedOptionalsGroupedByType = groupElementsByKey(addedOptionals,
					"ingredientType", "name");

			ingredientsEdit.put("removed", removedIngredientsGroupedByType);
			ingredientsEdit.put("added", addedOptionalsGroupedByType);

			mealsOrder.put(mealOrder.getName(), ingredientsEdit);
			mealsOrder.put("mealType", mealOrder.getMealTypeName());

			if (mealsOrderList.contains(mealOrder.getMealTypeName())) {
				HashMap<String, Object> aux = new HashMap<String, Object>();
				aux.put(mealOrder.getName(), mealsOrder.get(mealOrder.getName()));
				mealsGroupedByType.get(mealsOrder.get("mealType")).add(aux);
			} else {
				HashMap<String, Object> aux = new HashMap<String, Object>();
				List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
				list.add(aux);
				aux.put(mealOrder.getName(), mealsOrder.get(mealOrder.getName()));
				mealsGroupedByType.put((String) mealsOrder.get("mealType"), list);
				mealsOrderList.add(mealOrder.getMealTypeName());
			}

		}// closes 1st for

		return mapper.writeValueAsString(mealsGroupedByType);
	}

	/**
	 * Method responsible for grouping a list of similars objects into a
	 * HashMap, given a key and the object you want to group.
	 * 
	 * @param toGroup
	 * @param key
	 * @param variable
	 * @return
	 */
	private HashMap<String, List<String>> groupElementsByKey(List<HashMap<String, String>> toGroup, String key,
			String variable) {
		HashMap<String, List<String>> removedIngredientsGroupedByType = new HashMap<String, List<String>>();
		for (HashMap<String, String> hashMap : toGroup) {
			if (removedIngredientsGroupedByType.containsKey(hashMap.get(key))) {
				removedIngredientsGroupedByType.get(hashMap.get(key)).add(hashMap.get(variable));
			} else {
				List<String> aux = new ArrayList<String>();
				aux.add(hashMap.get(variable));
				removedIngredientsGroupedByType.put(hashMap.get(key), aux);
			}
		}
		return removedIngredientsGroupedByType;
	}

	public Meal findMeal(Integer idMeal) {
		return em.find(Meal.class, idMeal);
	}

	private void verifyOutOfStock(Meal meal) throws Exception {
		if (meal.getOutOfStock() != null && meal.getOutOfStock().equals(Boolean.TRUE))
			throw new Exception("meal outOfStock" + "." + meal.getName());
	}

	private void verifyOutOfStock(Ingredient ingredient) throws Exception {
		if (ingredient.getOutOfStock() != null && ingredient.getOutOfStock().equals(Boolean.TRUE))
			throw new Exception("ingredient outOfStock" + "." + ingredient.getName());
	}

	private void verifyOutOfStock(Beverage beverage) throws Exception {
		if (beverage.getOutOfStock() != null && beverage.getOutOfStock().equals(Boolean.TRUE))
			throw new Exception("beverage outOfStock" + "." + beverage.getName());
	}

	public void sendOrderWaitingNotification(Orders order) throws Exception {
		Notificator notificator = new OrderWaitingNotificator();
		log.log(Level.INFO,
				"Sending e-mail to user " + order.getUser().getId() + ". Order " + order.getTotalOrderNumber()
						+ " Waiting.");
		notificator.send(prepareMessage(order, null));
	}

	public void sendOrderCanceledNotification(Orders order) throws Exception {
		Notificator notificator = new OrderCanceledNotificator();
		log.log(Level.INFO,
				"Sending e-mail to user " + order.getUser().getId() + ". Order " + order.getTotalOrderNumber()
						+ " Canceled.");

		notificator.send(prepareMessage(order, null));
	}

	private void sendOrderToSubsidiaryEmail(Orders order, PaymentMethod paymentMethod) throws Exception {
		Notificator notificator = new SubsidiaryOrderReceivedNotificator();
		String msg = "Sending order number " + order.getTotalOrderNumber().toString() + " to subsidiary "
				+ order.getSubsidiary().getConfiguration().getReceiveOrdersMailAddress();
		log.log(Level.INFO, msg);
		notificator.send(prepareMessage(order, paymentMethod));

	}

	private void sendOrderToUserEmail(Orders order, PaymentMethod paymentMethod) throws Exception {
		Notificator notificator = new OrderPlacedNotificator();
		String msg = "Sending order number " + order.getTotalOrderNumber().toString() + " to user "
				+ order.getUser().getEmail();
		log.log(Level.INFO, msg);
		notificator.send(prepareMessage(order, paymentMethod));
	}

	private HashMap<String, String> prepareMessage(Orders order, PaymentMethod paymentMethod) {
		EmailDataDTO<String, String> emailData = new EmailDataDTO<String, String>();

		if (OrderOrigin.SQUARE.equals(order.getOrderOrigin())) {
			RedeFoodMailUtil.prepareRedeFoodLogoAndFooter(emailData);
			emailData.put("ratingUrl", RedeFoodConstants.DEFAULT_RATING_URL + order.getId());
		} else {
			RedeFoodMailUtil.prepareSubsidiaryLogoAndFooter(emailData, order.getSubsidiary());
			emailData.put("ratingUrl", RedeFoodUtils.urlBuilder(order.getSubsidiary().getRestaurant().getSubdomain())
					+ RedeFoodConstants.DEFAULT_RATING_SUFFIX + order.getId());
		}

		emailData.put("userName", order.getUser().getFirstName().toUpperCase());
		emailData.put("subsidiaryPhone", order.getSubsidiary().getPhone1() == null ? "inexistente" : order
				.getSubsidiary().getPhone1());
		emailData.put("userEmail", order.getUser().getEmail());
		emailData.put("subsidiaryEmail", order.getSubsidiary().getConfiguration().getReceiveOrdersMailAddress());
		emailData.put("orderNumber", String.valueOf(order.getTotalOrderNumber()));
		emailData.put("orderMade", RedeFoodUtils.formatDateTime(order.getOrderMade()));
		emailData.put("restaurantName", order.getSubsidiary().getName());

		if (paymentMethod != null) {
			emailData.put("orderData", RedeFoodMailUtil.createHTMTableData(order, paymentMethod));
		} else {
			emailData.put("orderData", RedeFoodMailUtil.createTableHTMLtoPickUp(order));
		}

		if (order.getCancelReason() != null) {
			emailData.put("reason", order.getCancelReason());
		}

		return emailData;
	}

	@GET
	@Path("/order/{idOrders:[0-9][0-9]*}")
	public void printOrderTest(@PathParam("idOrders") Integer idOrder) throws NumberFormatException, Exception {
		Orders order = em.find(Orders.class, idOrder);
		// PrintService.printOrderKitchen(order, "pt_br", em.find(Printer.class,
		// new Short("12")), "1.1.1.3");
		PrintService.printDelivery(order, "pt_br", em.find(Printer.class, new Short("12")), "1.1.1.3", order
				.getOrderPaymentMethod().get(0).getPaymentMethod());
	}
}