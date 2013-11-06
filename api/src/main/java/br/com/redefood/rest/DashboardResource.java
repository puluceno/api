package br.com.redefood.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import br.com.redefood.annotations.Owner;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Orders;
import br.com.redefood.model.Printer;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.service.PrintService;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.RedeFoodUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/dashboard")
public class DashboardResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private RedeFoodExceptionHandler eh;

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	@Owner
	@GET
	@Produces("application/json;charset=UTF8")
	public String fetchDashboardData(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @QueryParam("fromDate") Date from,
			@QueryParam("toDate") Date to, @QueryParam("fromHour") String hourFrom,
			@QueryParam("toHour") String hourTo, @QueryParam("print") Boolean print,
			@QueryParam("printer") Short idPrinter, @QueryParam("clientIP") String clientIP) {

		if (from == null) {
			from = new Date();
		}
		if (to == null) {
			to = new Date();
		}
		if (hourFrom == null || hourFrom.contentEquals("")) {
			from.setHours(0);
			from.setMinutes(0);
			from.setSeconds(0);
		} else {
			from.setHours(Integer.parseInt(hourFrom.split(":")[0]));
			from.setMinutes(Integer.parseInt(hourFrom.split(":")[1]));
			from.setSeconds(0);
		}
		if (hourTo == null || hourTo.contentEquals("")) {
			to.setHours(23);
			to.setMinutes(59);
			to.setSeconds(59);
		} else {
			to.setHours(Integer.parseInt(hourTo.split(":")[0]));
			to.setMinutes(Integer.parseInt(hourTo.split(":")[1]));
			to.setSeconds(59);
		}

		try {
			// Orders money
			List resultMoney = em.createNamedQuery(Subsidiary.FIND_TOTAL_MONEY_BY_DATE)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Orders money grouped by orderType
			List resultMoneyOrderType = em.createNamedQuery(Subsidiary.FIND_TOTAL_MONEY_BY_ORDERTYPE)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Order quantity
			Long qtdy = (Long) em.createNamedQuery(Orders.FIND_QTY_BETWEEN_DATES)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();

			// Orders price
			Double avgOrderPrice = (Double) em.createNamedQuery(Orders.FIND_AVG_ORDER_PRICE)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();
			Double maxO = (Double) em.createNamedQuery(Subsidiary.FIND_MAX_ORDER_PRICE).setParameter("from", from)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();
			Double minO = (Double) em.createNamedQuery(Subsidiary.FIND_MIN_ORDER_PRICE).setParameter("from", from)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();

			// Orders prepare time
			Double avgT = (Double) em.createNamedQuery(Subsidiary.FIND_AVG_PREPARETIME).setParameter("from", from)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();
			Integer maxT = (Integer) em.createNamedQuery(Subsidiary.FIND_MAX_PREPARETIME).setParameter("from", from)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();
			Integer minT = (Integer) em.createNamedQuery(Subsidiary.FIND_MIN_PREPARETIME).setParameter("from", from)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getSingleResult();

			// MealType sold
			List resultMtSold = em.createNamedQuery(Orders.MEAL_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Meal sold
			List resultMealSold = em.createNamedQuery(Orders.MEAL_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// BeverageType sold
			List resultBeverageTypeSold = em.createNamedQuery(Orders.BEVERAGE_TYPE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Beverage sold
			List resultBeverageSold = em.createNamedQuery(Orders.BEVERAGE_MOST_SOLD_BY_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// PaymentMethod used
			List resultPaymentMethodUsed = em.createNamedQuery(Orders.PAYMENT_METHOD_USED_BY_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Money per PaymentMethod used
			List resultMoneyPaymentMethod = em.createNamedQuery(Orders.MONEY_PAYMENT_METHOD_BY_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Orders to neighborhoods
			List resultNeighborhoods = em
					.createNamedQuery(Orders.COUNT_ORDERS_IN_NEIGHBORHOOD_AND_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Orders by OrderType
			List resultOrderTypes = em.createNamedQuery(OrderType.COUNT_BY_ORDERTYPE_SUBSIDIARY_AND_PERIOD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			// Orders Canceled by Subsidiary and Date
			List<Orders> ordersCanceled = em.createNamedQuery(Orders.FIND_CANCELED_BY_SUBSIDIARY_AND_DATE)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			Map<String, Object> dashboardData = new LinkedHashMap<String, Object>();
			dashboardData.put("orderQty", qtdy);
			dashboardData.put("avgOrderPrice",
					new BigDecimal(avgOrderPrice == null ? 0 : avgOrderPrice).setScale(2, RoundingMode.HALF_DOWN));
			dashboardData.put("minOrderPrice", minO);
			dashboardData.put("maxOrderPrice", maxO);

			// Adds the money sum
			for (Object object : resultMoney) {
				String a[] = mapper.writeValueAsString(object).replace("[", "").replace("]", "").replace("\"", "")
						.split(",");
				dashboardData.put("totalOrderValue", a[0]);
				dashboardData.put("totalDeliveryValue", a[1]);
				dashboardData.put("totalRenevue", a[2]);
			}

			if (avgT == null) {
				dashboardData.put("avgPrepareTime", avgT);
			} else {
				dashboardData.put("avgPrepareTime", RedeFoodUtils.formatTime(avgT.intValue()));
			}

			if (minT == null) {
				dashboardData.put("minPrepareTime", minT);
			} else {
				dashboardData.put("minPrepareTime", RedeFoodUtils.formatTime(minT.intValue()));
			}

			if (maxT == null) {
				dashboardData.put("maxPrepareTime", maxT);
			} else {
				dashboardData.put("maxPrepareTime", RedeFoodUtils.formatTime(maxT.intValue()));
			}

			List<HashMap<String, String>> ordersCanceledList = new ArrayList<HashMap<String, String>>();
			for (Orders order : ordersCanceled) {
				HashMap<String, String> ordersCanceledMap = new LinkedHashMap<String, String>();
				ordersCanceledMap.put("orderNumber", String.valueOf(order.getTotalOrderNumber()));
				ordersCanceledMap.put("orderValue", order.getTotalPrice().toString());
				ordersCanceledMap.put("employee", order.getEmployeeLastStatus());
				ordersCanceledMap.put("reason", order.getCancelReason());
				ordersCanceledList.add(ordersCanceledMap);
			}
			dashboardData.put("ordersCanceled", ordersCanceledList);

			dashboardData.put("mealTypeSold", generatePercent(resultMtSold));
			dashboardData.put("mealSold", generatePercent(resultMealSold));
			dashboardData.put("beverageTypeSold", generatePercent(resultBeverageTypeSold));
			dashboardData.put("beverageSold", generatePercent(resultBeverageSold));
			dashboardData.put("paymentTypeUsed", generatePercent(resultPaymentMethodUsed));
			dashboardData.put("moneyByPaymentMethod", generatePercent(resultMoneyPaymentMethod));
			dashboardData.put("neighborhoodSells", generatePercent(resultNeighborhoods));
			dashboardData.put("orderTypeSells", generatePercent(resultOrderTypes));
			dashboardData.put("moneyByOrderType", generatePercent(resultMoneyOrderType));

			if (print != null && print) {
				PrintService.printCashier(locale, em.find(Printer.class, idPrinter), clientIP, dashboardData, from, to);
			}

			return mapper.writeValueAsString(dashboardData);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	private List<HashMap<String, String>> generatePercent(List<Object> list) throws Exception {
		List<HashMap<String, String>> returnList = new ArrayList<HashMap<String, String>>();
		int total = 0;
		for (Object object : list) {
			String a[] = mapper.writeValueAsString(object).replace("[", "").replace("]", "").replace("\"", "")
					.split(",");
			HashMap<String, String> sellsMap = new LinkedHashMap<String, String>();
			sellsMap.put("qty", a[0]);
			sellsMap.put("name", a[1]);
			if (a.length == 4 && a[2] != null) {
				sellsMap.put("lat", a[2]);
			}
			if (a.length == 4 && a[3] != null) {
				sellsMap.put("lng", a[3]);
			}

			returnList.add(sellsMap);
			if (a[0].contains(".")) {
				total += Double.parseDouble(a[0]);
			} else {
				total += Integer.parseInt(a[0]);
			}
		}
		for (HashMap<String, String> hashMap : returnList) {
			Double percent = new BigDecimal((Double.parseDouble(hashMap.get("qty")) / total) * 100).setScale(2,
					RoundingMode.HALF_DOWN).doubleValue();
			hashMap.put("percent", percent.toString());
		}

		return returnList;
	}

}
