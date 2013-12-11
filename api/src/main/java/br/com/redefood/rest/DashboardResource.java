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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import br.com.redefood.annotations.Owner;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Meal;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Orders;
import br.com.redefood.model.Printer;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.complex.MotoboyDeliveriesDTO;
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
			@QueryParam("printer") Short idPrinter, @QueryParam("clientIP") String clientIP,
			@DefaultValue("0") @QueryParam("idMeal") Integer idMeal) {

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
			Map<String, Object> dashboardData = new LinkedHashMap<String, Object>();

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

			// Average Orders sold by Subsidiary, day of week and Date
			List<Object[]> avgOrdersByDayList = em
					.createNativeQuery(getNativeQuery(Orders.AVG_ORDERS_BY_SUBSIDIARY_AND_DAY_OF_WEEK))
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			List<HashMap<String, Object>> avgOrdersByDay = new ArrayList<HashMap<String, Object>>();
			for (Object[] object : avgOrdersByDayList) {
				HashMap<String, Object> avgOrderByDay = new HashMap<String, Object>();
				avgOrderByDay.put("dayOfWeek", object[0]);
				avgOrderByDay.put("avgOrderQty", object[1]);
				avgOrdersByDay.add(avgOrderByDay);
			}
			dashboardData.put("avgOrdersByDay", avgOrdersByDay);

			// Average Orders sold by Subsidiary, day, hour and Date
			List<Object[]> avgOrdersByHourList = em
					.createNativeQuery(getNativeQuery(Orders.AVG_ORDERS_BY_SUBSIDIARY_AND_HOUR))
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();

			List<HashMap<String, List<HashMap<Object, Object>>>> avgOrdersByDayAndHour = new ArrayList<HashMap<String, List<HashMap<Object, Object>>>>();
			LinkedHashMap<String, List<HashMap<Object, Object>>> avgOrderByHour = new LinkedHashMap<String, List<HashMap<Object, Object>>>();
			for (Object[] object : avgOrdersByHourList) {
				if (!avgOrderByHour.containsKey(object[0])) {
					List<HashMap<Object, Object>> avgOrdersByDayName = new ArrayList<HashMap<Object, Object>>();
					HashMap<Object, Object> byHour = new HashMap<Object, Object>();
					byHour.put(object[1], object[2]);
					avgOrdersByDayName.add(byHour);
					avgOrderByHour.put((String) object[0], avgOrdersByDayName);
				} else {
					HashMap<Object, Object> byHour = new HashMap<Object, Object>();
					byHour.put(object[1], object[2]);
					avgOrderByHour.get(object[0]).add(byHour);
				}
			}
			avgOrdersByDayAndHour.add(avgOrderByHour);
			dashboardData.put("avgOrdersByHour", avgOrdersByDayAndHour);

			if (!idMeal.equals(0)) {
				// Average Meals sold by Subsidiary, day of week and Date
				List<Object[]> avgMealsByDayList = em
						.createNativeQuery(getNativeQuery(Meal.AVG_BY_SUBSIDIARY_AND_DAY_OF_WEEK))
						.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
						.setParameter("idMeal", idMeal).getResultList();

				List<HashMap<String, Object>> avgMealsByDay = new ArrayList<HashMap<String, Object>>();
				for (Object[] object : avgMealsByDayList) {
					HashMap<String, Object> avgMealByDay = new HashMap<String, Object>();
					avgMealByDay.put("dayOfWeek", object[0]);
					avgMealByDay.put("avgMealQty", object[1]);
					avgMealsByDay.add(avgMealByDay);
				}
				dashboardData.put("avgMealByDay", avgMealsByDay);

				// Average Meals sold by Subsidiary, hour and Date
				List<Object[]> avgMealsByHourList = em
						.createNativeQuery(getNativeQuery(Meal.AVG_BY_SUBSIDIARY_AND_HOUR))
						.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
						.setParameter("idMeal", idMeal).getResultList();

				List<HashMap<String, List<HashMap<Object, Object>>>> avgMealByDayAndHour = new ArrayList<HashMap<String, List<HashMap<Object, Object>>>>();
				LinkedHashMap<String, List<HashMap<Object, Object>>> avgMealByHour = new LinkedHashMap<String, List<HashMap<Object, Object>>>();
				for (Object[] object : avgMealsByHourList) {
					if (!avgMealByHour.containsKey(object[0])) {
						List<HashMap<Object, Object>> avgMealsByDayName = new ArrayList<HashMap<Object, Object>>();
						HashMap<Object, Object> byHour = new HashMap<Object, Object>();
						byHour.put(object[1], object[2]);
						avgMealsByDayName.add(byHour);
						avgMealByHour.put((String) object[0], avgMealsByDayName);
					} else {
						HashMap<Object, Object> byHour = new HashMap<Object, Object>();
						byHour.put(object[1], object[2]);
						avgMealByHour.get(object[0]).add(byHour);
					}
				}
				avgMealByDayAndHour.add(avgMealByHour);
				dashboardData.put("avgMealByHour", avgMealByDayAndHour);
			}

			// Motoboy related stuff
			List<MotoboyDeliveriesDTO> motoboyDeliveries = em.createNamedQuery(Employee.FIND_DELIVERIES_BY_MOTOBOY)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("from", from).setParameter("to", to)
					.getResultList();
			dashboardData.put("motoboyDeliveries", motoboyDeliveries);

			// Orders related stuff
			dashboardData.put("orderQty", qtdy);
			dashboardData.put("avgOrderPrice",
					new BigDecimal(avgOrderPrice == null ? 0 : avgOrderPrice).setScale(2, RoundingMode.HALF_DOWN));
			dashboardData.put("minOrderPrice", minO);
			dashboardData.put("maxOrderPrice", maxO);

			// Adds the money sum
			for (Object money : resultMoney) {
				String a[] = mapper.writeValueAsString(money).replace("[", "").replace("]", "").replace("\"", "")
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
			Double percent = new BigDecimal(Double.parseDouble(hashMap.get("qty")) / total * 100).setScale(2,
					RoundingMode.HALF_DOWN).doubleValue();
			hashMap.put("percent", percent.toString());
		}

		return returnList;
	}

	private String getNativeQuery(String queryName) {
		StringBuffer query = new StringBuffer();
		switch (queryName) {
		case Orders.AVG_ORDERS_BY_SUBSIDIARY_AND_DAY_OF_WEEK:
			query.append("SELECT day_of_week, ");
			query.append("AVG(order_count) AS avg_orders_per_day ");
			query.append("FROM (SELECT Dayname(o.ordermade)   day_of_week, ");
			query.append("Dayofweek(o.ordermade) day_num, ");
			query.append("To_days(o.ordermade)   dataa, ");
			query.append("Count(*) order_count ");
			query.append("FROM Orders o ");
			query.append("WHERE o.idsubsidiary = :idSubsidiary ");
			query.append("AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'");
			query.append("GROUP BY dataa) temp ");
			query.append("GROUP BY day_of_week ");
			query.append("ORDER BY day_num");
			break;

		case Orders.AVG_ORDERS_BY_SUBSIDIARY_AND_HOUR:
			query.append("SELECT day_of_week, ");
			query.append("hour_of_day, ");
			query.append("AVG(order_count) AS avg_orders_per_day ");
			query.append("FROM (SELECT Dayname(o.ordermade) day_of_week, ");
			query.append("Hour(o.ordermade) hour_of_day, ");
			query.append("Dayofweek(o.ordermade) day_num, ");
			query.append("To_days(o.ordermade) dataa, ");
			query.append("COUNT(*) order_count ");
			query.append("FROM Orders o ");
			query.append("WHERE o.idsubsidiary = :idSubsidiary ");
			query.append("AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'");
			query.append("GROUP BY hour_of_day, ");
			query.append("dataa) temp ");
			query.append("GROUP BY hour_of_day, ");
			query.append("day_of_week ");
			query.append("ORDER BY day_num, ");
			query.append("hour_of_day");
			break;

		case Meal.AVG_BY_SUBSIDIARY_AND_DAY_OF_WEEK:
			query.append("SELECT day_of_week, ");
			query.append("AVG(order_count) AS avg_orders_per_day ");
			query.append("FROM (SELECT Dayname(o.ordermade) day_of_week, ");
			query.append("Dayofweek(o.ordermade) day_num, ");
			query.append("To_days(o.ordermade) dataa, ");
			query.append("COUNT(ohm.idmeal) order_count ");
			query.append("FROM Orders o ");
			query.append("JOIN Order_has_Meal ohm USING (idorders) ");
			query.append("WHERE o.idsubsidiary = :idSubsidiary ");
			query.append("AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'");
			query.append("AND ohm.idmeal = :idMeal ");
			query.append("GROUP BY dataa) temp ");
			query.append("GROUP BY day_of_week ");
			query.append("ORDER BY day_num");
			break;

		case Meal.AVG_BY_SUBSIDIARY_AND_HOUR:
			query.append("SELECT day_of_week, ");
			query.append("hour_of_day, ");
			query.append("AVG(order_count) AS avg_orders_per_day ");
			query.append("FROM (SELECT Dayname(o.ordermade)   day_of_week, ");
			query.append("Hour(o.ordermade) hour_of_day, ");
			query.append("Dayofweek(o.ordermade) day_num, ");
			query.append("To_days(o.ordermade) dataa, ");
			query.append("COUNT(ohm.idmeal) order_count ");
			query.append("FROM Orders o ");
			query.append("JOIN Order_has_Meal ohm USING (idorders) ");
			query.append("WHERE o.idsubsidiary = :idSubsidiary ");
			query.append("AND o.orderMade BETWEEN :from AND :to AND o.orderStatus <> 'CANCELED'");
			query.append("AND ohm.idmeal = :idMeal ");
			query.append("GROUP  BY hour_of_day, ");
			query.append("dataa) temp ");
			query.append("GROUP BY hour_of_day, ");
			query.append("day_of_week ");
			query.append("ORDER BY day_num, ");
			query.append("hour_of_day");
			break;

		default:
			break;
		}

		return query.toString();
	}

}
