package br.com.redefood.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
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

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.BeverageOrder;
import br.com.redefood.model.Board;
import br.com.redefood.model.BoardStats;
import br.com.redefood.model.Configuration;
import br.com.redefood.model.MealOrder;
import br.com.redefood.model.OrderPaymentMethod;
import br.com.redefood.model.Orders;
import br.com.redefood.model.PaymentMethod;
import br.com.redefood.model.Printer;
import br.com.redefood.model.complex.RedeFoodIP;
import br.com.redefood.service.PrintService;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class BoardResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;
	@Inject
	private OrderResource or;

	// @Securable
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public String findOrdersByBoard(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBoard") Integer idBoard) {

		try {
			Board b = (Board) em.createNamedQuery(Board.FIND_OPEN_BILL_TO_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("idBoard", idBoard).getSingleResult();

			for (Orders o : b.getOrders()) {
				if (o.getEmployee() != null) {
					Hibernate.initialize(o.getEmployee());
				}
				if (o.getMealsOrder() != null) {
					for (MealOrder mo : o.getMealsOrder())
						if (mo.getMealOrderIngredients() != null && !mo.getMealOrderIngredients().isEmpty()) {
							Hibernate.initialize(mo.getMealOrderIngredients());
						}
				}
				if (o.getBeveragesOrder() != null) {
					Hibernate.initialize(o.getBeveragesOrder());
				}
				if (o.getOrderPaymentMethod() != null) {
					for (OrderPaymentMethod opm : o.getOrderPaymentMethod()) {
						Hibernate.initialize(opm.getPaymentMethod());
					}
				}
			}

			return mapper.writeValueAsString(b);
		} catch (Exception e) {
			return eh.boardExceptionHandler(e, locale).getEntity().toString();
		}
	}

	@POST
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}/open")
	@Consumes("application/json")
	@Produces("application/json;charset=UTF8")
	public Response openBoardBill(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idBoard") Integer idBoard, Board board) {
		try {

			Board toMerge = em.find(Board.class, board.getId());
			if (toMerge == null)
				throw new Exception("find board");
			toMerge.setCredit(board.getCredit());
			toMerge.setAvailable(false);
			toMerge.setOpenTime(new Date());
			toMerge.setPeopleNumber(board.getPeopleNumber());

			em.merge(toMerge);
			em.flush();

			log.log(Level.INFO, "Mesa " + idBoard + " aberta com " + board.getPeopleNumber() + " pessoas.");

			return Response.status(200).entity(mapper.writeValueAsString(toMerge)).build();

		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	@PUT
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}/pay")
	@Consumes("application/json")
	@Produces("application/json;charset=UTF8")
	public Response payBoardBill(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idBoard") Integer idBoard, @QueryParam("idMealOrder") List<Integer> idsMealOrder,
			@QueryParam("idBeverageOrder") List<Integer> idsBeverageOrder, List<OrderPaymentMethod> ordersPaymentMethods) {

		try {
			log.log(Level.INFO, "Executing a payment to board id " + idBoard + " to subsidiary id " + idSubsidiary);

			for (OrderPaymentMethod opm : ordersPaymentMethods) {

				// Find order by idOrders, idSubsidiary and idBoard
				Orders o = (Orders) em.createNamedQuery(Orders.FIND_BY_ID_ORDER_AND_BOARD)
						.setParameter("idSubsidiary", idSubsidiary).setParameter("idBoard", idBoard)
						.setParameter("idOrders", opm.getOrder().getId()).getSingleResult();

				// Set MealOrder as payed (it is an unchecked dummy)
				if (idsMealOrder != null && !idsMealOrder.isEmpty()) {
					for (Integer idMO : idsMealOrder) {
						for (MealOrder mo : o.getMealsOrder())
							if (mo.getId().intValue() == idMO.intValue()) {
								mo.setPayed(true);
								em.merge(mo);
							}
					}
				}

				// Set BeverageOrder as payed (it is an unchecked dummy)
				if (idsBeverageOrder != null && !idsBeverageOrder.isEmpty()) {
					for (Integer idBO : idsBeverageOrder) {
						for (BeverageOrder bo : o.getBeveragesOrder())
							if (bo.getId().intValue() == idBO.intValue()) {
								bo.setPayed(true);
								em.merge(bo);
							}
					}
				}

				// Set a new list if it was null
				if (o.getOrderPaymentMethod() == null) {
					o.setOrderPaymentMethod(new ArrayList<OrderPaymentMethod>());
				}

				// Cria um novo pagamento para o pedido
				if (opm.getIdOrderPaymentMethod() == null) {
					OrderPaymentMethod op = new OrderPaymentMethod(opm.getValue(), opm.getTip(), em.find(
							PaymentMethod.class, opm.getPaymentMethod().getId()), o);
					o.getOrderPaymentMethod().add(op);
					// em.merge(o);
				}

				// Edita um pagamento para o pedido
				if (opm.getIdOrderPaymentMethod() != null) {
					OrderPaymentMethod op = em.find(OrderPaymentMethod.class, opm.getIdOrderPaymentMethod());
					if (op != null && o.getTotalPrice().doubleValue() > op.getValue().doubleValue()) {
						op.setValue(op.getValue() + opm.getValue());
						op.setTip(op.getTip() + opm.getTip());
						em.merge(op);
					}
				}
				em.flush();
			}

			// Calculate and save credits to Board
			Board b = (Board) em.createNamedQuery(Board.FIND_ORDERS_TO_PAY_BY_BOARD)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("idBoard", idBoard).getSingleResult();

			BigDecimal credits = b.getCredit();

			for (Orders o : b.getOrders())
				if (!o.getBoardPayed()) {
					for (OrderPaymentMethod op : o.getOrderPaymentMethod()) {
						credits = credits.add(new BigDecimal(op.getValue()));
					}
				}
			b.setCredit(credits);
			em.merge(b);

			em.flush();

			double doubleValue = b.getBill().doubleValue();
			if (credits.doubleValue() == doubleValue)
				return closeBoardBill(locale, idSubsidiary, idBoard);
			// TODO: testar isso aqui pq as vezes os campos nao estão batendo

			// TODO: não está batendo porcausa do tipo, tem que usar BigDecimal!

			return Response.status(200).entity(mapper.writeValueAsString(b)).build();
		} catch (Exception e) {
			return eh.boardExceptionHandler(e, locale);
		}
	}

	@POST
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}/close")
	@Produces("application/json;charset=UTF8")
	public Response closeBoardBill(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary,
			@PathParam("idBoard") Integer idBoard) {

		try {
			log.log(Level.INFO, "Closing bill to board id " + idBoard + " to subsidiary id " + idSubsidiary);

			Board b = em.find(Board.class, idBoard);
			// TODO: nunca varrer todos os pedidos, sempre filtrá-los por
			// namedquery

			if (b != null && !b.getAvailable()) {
				for (Orders orders : b.getOrders())
					if (!orders.getBoardPayed()) {
						orders.setBoardPayed(true);
					}

				if (b.getBoardStats() == null) {
					b.setBoardStats(new ArrayList<BoardStats>());
				}

				b.getBoardStats()
						.add(new BoardStats(b.getNumber(), b.getOpenTime(), new Date(), b.getPeopleNumber(), b
								.getBill(), b));
				b.setAvailable(true);
				b.setPeopleNumber(new Short("0"));
				b.setBill(new BigDecimal(0.0));
				b.setCredit(new BigDecimal(0.0));

				em.merge(b);
				em.flush();
			}
			return Response.status(200).build();
		} catch (Exception e) {
			return eh.genericExceptionHandlerResponse(e, locale);
		}
	}

	// @Securable
	@POST
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}/print/{idPrinter:[0-9][0-9]*}")
	@Produces("application/json;charset=UTF8")
	public Response printBoardOrders(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBoard") Integer idBoard,
			@PathParam("idPrinter") Short idPrinter, @HeaderParam("subsidiaryIP") RedeFoodIP subsidiaryIP) {

		try {
			Board b = (Board) em.createNamedQuery(Board.FIND_OPEN_BILL_TO_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("idBoard", idBoard).getSingleResult();

			Configuration config = em.find(Configuration.class, b.getSubsidiary().getConfiguration().getId());
			Printer printer = em.find(Printer.class, idPrinter);
			or.validatePrint(config, subsidiaryIP, printer);

			if (b.getOrders() != null && !b.getOrders().isEmpty()) {
				PrintService.printBoardOrders(b, locale, printer, subsidiaryIP.getSubsidiaryIP());
			}

			return Response.status(200).build();
		} catch (Exception e) {
			return eh.printExceptionHandlerResponse(e, locale);
		}
	}

	// @Owner
	@GET
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/boards/{idBoard:[0-9][0-9]*}/orders")
	@Produces("application/json;charset=UTF8")
	public String findClosedOrdersByBoard(@HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idBoard") Integer idBoard) {

		try {
			Board b = (Board) em.createNamedQuery(Board.FIND_CLOSED_BILL_TO_SUBSIDIARY)
					.setParameter("idSubsidiary", idSubsidiary).setParameter("idBoard", idBoard).getSingleResult();

			for (Orders order : b.getOrders()) {
				for (OrderPaymentMethod opm : order.getOrderPaymentMethod()) {
					Hibernate.initialize(opm.getPaymentMethod());
				}
			}

			return mapper.writeValueAsString(b);
		} catch (Exception e) {
			return eh.boardExceptionHandler(e, locale).toString();
		}
	}
}
