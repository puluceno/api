package br.com.redefood.rest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import br.com.redefood.annotations.Securable;
import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Employee;
import br.com.redefood.model.Login;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.Orders;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.enumtype.OrderStatus;
import br.com.redefood.model.enumtype.TypeOrder;
import br.com.redefood.util.HibernateMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@Path("")
public class OrderStatusResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private RedeFoodExceptionHandler eh;
	@Inject
	private EntityManager em;
	@Inject
	private OrderResource or;

	@GET
	@Path("/order/status")
	@Produces("application/json;charset=UTF8")
	public String findOrderStatus(@HeaderParam("locale") String locale) {

		try {
			Map<String, String> status = new HashMap<String, String>();

			for (OrderStatus oStatus : OrderStatus.values()) {
				status.put(oStatus.name(), oStatus.toString(locale));
			}

			return mapper.writeValueAsString(status);

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@Securable
	@PUT
	@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}/order/{idOrder:[0-9][0-9]*}/status")
	@Consumes("application/json")
	public Response alterOrderStatus(@HeaderParam("token") String token, @HeaderParam("locale") String locale,
			@PathParam("idSubsidiary") Short idSubsidiary, @PathParam("idOrder") Integer idOrder,
			@QueryParam("cancelReason") String cancelReason, @QueryParam("orderStatus") OrderStatus newStatus,
			@QueryParam("idMotoboy") Short idMotoboy) {

		Orders order = null;

		try {
			if (newStatus == null)
				throw new Exception("status null");
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);
			if (subsidiary == null)
				throw new Exception("subsidiary not found");

			Login login = em.find(Login.class, token);
			if (login == null)
				throw new Exception("employee not found");

			Employee employee = em.find(Employee.class, (short) login.getIdUser());
			if (employee == null)
				throw new Exception("employee not found");

			if (!subsidiary.getEmployees().contains(employee))
				throw new Exception("employee does not work at subsidiary");

			order = em.find(Orders.class, idOrder);
			if (order == null)
				throw new Exception("order not found");

			validateOrderStatus(order, newStatus, cancelReason);

			if (newStatus.equals(OrderStatus.DELIVERING) && idMotoboy != null) {
				order.setOrderSent(new Date());
				order.setEmployee(em.find(Employee.class, idMotoboy));
			}

			order.setCancelReason(cancelReason);
			order.setOrderStatus(newStatus);
			order.setEmployeeLastStatus(employee.getFirstName());

			if (order.getBoard() != null) {
				order.getBoard().setBill(
						order.getBoard().getBill().subtract(new BigDecimal(order.getTotalPrice()).setScale(2)));
			}

			em.merge(order);
			em.flush();

			if (newStatus.equals(OrderStatus.WAITING_PICKUP) && !order.getOrderType().getType().equals(TypeOrder.LOCAL)
					&& order.getUser().getNotifications().getEmailAction()) {
				or.sendOrderWaitingNotification(order);
			}

			if (newStatus.equals(OrderStatus.CANCELED) && !order.getOrderType().getType().equals(TypeOrder.LOCAL)
					&& order.getUser().getNotifications().getEmailAction()) {
				or.sendOrderCanceledNotification(order);
			}

			return Response.status(200).build();

		} catch (Exception e) {
			return eh.orderExceptionHandlerResponse(e, locale, idOrder, newStatus,
					order != null ? order.getOrderStatus() : "order null");
		}
	}

	private void validateOrderStatus(Orders order, OrderStatus newStatus, String cancelReason) throws Exception {

		if (newStatus.equals(OrderStatus.CANCELED) && (cancelReason == null || cancelReason.isEmpty()))
			throw new Exception("reason empty");
		if (newStatus.equals(OrderStatus.CANCELED) && cancelReason != null && cancelReason.length() < 10)
			throw new Exception("reason length");

		switch (order.getOrderStatus()) {

		case PREPARING:
			// Pickup Online só vai de Preparando para Aguardando Retirada
			if (order.getOrderType().getId().equals(OrderType.PICKUP_ONLINE)
					&& !newStatus.equals(OrderStatus.WAITING_PICKUP))
				throw new Exception("invalid status");

			// Local de Preparando só vai para Cancelado, Entregue, Não Entregue
			// ou permanece em Preparando
			if ((order.getOrderType().getId().equals(OrderType.LOCAL) || order.getOrderType().getId()
					.equals(OrderType.LOCAL_TO_GO))
					&& !newStatus.equals(OrderStatus.DELIVERED)
					&& !newStatus.equals(OrderStatus.CANCELED)
					&& !newStatus.equals(OrderStatus.NOT_DELIVERED) && !newStatus.equals(OrderStatus.PREPARING))
				throw new Exception("invalid status");

			// Delivery Online só vai de Preparando para Saiu para entrega ou
			// Cancelado
			if (order.getOrderType().getId().equals(OrderType.DELIVERY_ONLINE)
					&& !(newStatus.equals(OrderStatus.DELIVERING) || newStatus.equals(OrderStatus.CANCELED)))
				throw new Exception("invalid status");

			break;

		case DELIVERING:
			// Saiu para entrega só vai para Entregue, Não Entregue ou Permanece
			// como Saiu para entrega (neste caso o intuito é trocar o
			// entregador)
			if (!newStatus.equals(OrderStatus.DELIVERED) && !newStatus.equals(OrderStatus.NOT_DELIVERED)
					&& !newStatus.equals(OrderStatus.DELIVERING))
				throw new Exception("invalid status");
			break;

		case DELIVERED:
			// Entregue só vai para Entregue
			if (!newStatus.equals(OrderStatus.DELIVERED))
				throw new Exception("invalid status");
			break;

		case CANCELED:
			// Cancelado só permanece como Cancelado
			if (!newStatus.equals(OrderStatus.CANCELED))
				throw new Exception("invalid status");
			break;

		case NOT_DELIVERED:
			// Não entregue vai para Saiu Para Entrega (Re-Entrega), Cancelado
			// ou permanece como Não Entregue
			if (!newStatus.equals(OrderStatus.NOT_DELIVERED) && !newStatus.equals(OrderStatus.CANCELED)
					&& !newStatus.equals(OrderStatus.DELIVERING))
				throw new Exception("invalid status");
			break;

		case WAITING_PICKUP:
			// Aguardando retirada vai para Entregue ou Não Entregue
			if (!newStatus.equals(OrderStatus.DELIVERED) && !newStatus.equals(OrderStatus.NOT_DELIVERED))
				throw new Exception("invalid status");
			break;

			// ORDER_SENT = 0
			// Pedido enviado só vai para preparando ou cancelado.
		default:
			if (!newStatus.equals(OrderStatus.PREPARING) && !newStatus.equals(OrderStatus.CANCELED))
				throw new Exception("invalid status");
			break;
		}

	}

}
