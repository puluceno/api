package br.com.redefood.rest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Days;

import br.com.redefood.exceptions.RedeFoodExceptionHandler;
import br.com.redefood.model.Account;
import br.com.redefood.model.OrderType;
import br.com.redefood.model.RedeFoodData;
import br.com.redefood.model.Subsidiary;
import br.com.redefood.model.SubsidiaryModule;
import br.com.redefood.model.enumtype.PaymentType;
import br.com.redefood.model.enumtype.TypeOrder;
import br.com.redefood.service.BoletoGenerator;
import br.com.redefood.util.HibernateMapper;
import br.com.redefood.util.RedeFoodConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/subsidiary/{idSubsidiary:[0-9][0-9]*}")
@Stateless
public class AccountResource extends HibernateMapper {
	private static final ObjectMapper mapper = HibernateMapper.getMapper();
	@Inject
	private EntityManager em;
	@Inject
	private Logger log;
	@Inject
	private RedeFoodExceptionHandler eh;

	@GET
	@Path("/account")
	@Produces("application/json;charset=UTF8")
	public String findAccountTotal(@HeaderParam("locale") String locale, @PathParam("idSubsidiary") Short idSubsidiary) {

		try {
			Subsidiary subsidiary = em.find(Subsidiary.class, idSubsidiary);

			Hibernate.initialize(subsidiary.getAccountList());

			return mapper.writeValueAsString(subsidiary.getAccountList());

		} catch (Exception e) {
			return eh.genericExceptionHandlerString(e, locale);
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	// TODO: works only in standalone mode
	@Schedule(dayOfMonth = "1", hour = "04", minute = "45", second = "45", persistent = false)
	private void executeAccountCalculation() {
		try {

			// TODO: TROCAR TODOS OS SQL'S ABAIXO POR ESTE ÚNICO:
			String queryFoda = "SELECT MIN(o.orderMade) AS firstOrder, MAX(o.orderMade) AS lastOrder, COUNT(*) AS totalOrders, "
					+ "MAX(o.totalPrice) AS biggestOrder, MIN(o.totalPrice) AS smallestOrder, "
					+ "MAX(TIMEDIFF(o.orderSent,o.orderMade)) AS biggestTime, "
					+ "MIN(TIMEDIFF(o.orderSent,o.orderMade)) AS smallestTime, "
					+ "SEC_TO_TIME(AVG(TIMEDIFF(o.orderSent,o.orderMade))) AS averageTime, "
					+ "SUM(o.totalPrice) AS totalOrderPrice, AVG(o.totalPrice) AS averageOrderPrice, "
					+ "SUM(o.deliveryPrice) AS totalDeliveryPrice, AVG(o.deliveryPrice) AS averageDeliveryPrice, "
					+ "(SUM(o.totalPrice)-SUM(o.deliveryPrice))*0.05 + "
					+ "(SELECT coalesce((SELECT sm.price FROM Subsidiary_Module sm WHERE sm.idSubsidiary = :idSubsidiary "
					// TODO:somar com os módulos de valor fixo que o lojista
					// possui, se possuir outros módulos, por exemplo
					+ "AND sm.idModule=:idModule) , (SELECT m.defaultPrice FROM Module m WHERE m.idModule=:idModule))) AS toPay "
					+ "FROM Orders o "
					+ "WHERE o.orderMade >= CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY - INTERVAL 1 MONTH "
					+ "AND o.orderMade  < CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY "
					+ "AND o.idSubsidiary = :idSubsidiary "
					+ "AND o.orderStatus <> 'CANCELED' "
					+ "AND o.idOrderType IN (1,2,6) AND o.orderSent IS NOT NULL";

			String queryString = "SELECT sum(o.totalPrice)-sum(o.deliveryPrice) " + "FROM Orders o "
					+ "WHERE o.orderMade >= CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY - INTERVAL 1 MONTH "
					+ "AND o.orderMade  < CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY "
					+ "AND o.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' AND o.idOrderType IN (1,2,6)";
			Query queryValue = em.createNativeQuery(queryString);

			String queryOrderQuantityString = "SELECT count(*) " + "FROM Orders o "
					+ "WHERE o.orderMade >= CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY - INTERVAL 1 MONTH "
					+ "AND o.orderMade  < CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY "
					+ "AND o.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' AND o.idOrderType IN (1,2,6)";
			Query queryOrderQuantity = em.createNativeQuery(queryOrderQuantityString);

			String queryStringFrom = "SELECT MIN(o.orderMade) " + "FROM Orders o "
					+ "WHERE o.orderMade >= CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY - INTERVAL 1 MONTH "
					+ "AND o.orderMade  < CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY "
					+ "AND o.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' AND o.idOrderType IN (1,2,6)";
			Query queryFrom = em.createNativeQuery(queryStringFrom);

			String queryStringTo = "SELECT MAX(o.orderMade) " + "FROM Orders o "
					+ "WHERE o.orderMade >= CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY - INTERVAL 1 MONTH "
					+ "AND o.orderMade  < CURRENT_DATE - INTERVAL DAYOFMONTH(CURRENT_DATE)-1 DAY "
					+ "AND o.idSubsidiary = :idSubsidiary AND o.orderStatus <> 'CANCELED' AND o.idOrderType IN (1,2,6)";
			Query queryTo = em.createNativeQuery(queryStringTo);

			List<Subsidiary> subsidiaries = em.createNamedQuery(Subsidiary.FIND_ALL_SUBSIDIARIES).getResultList();

			for (Subsidiary subsidiary : subsidiaries) {
				try {
					log.log(Level.INFO, "Generating costs to subsidiary " + subsidiary.getId() + " relative to month "
							+ Calendar.getInstance().get(Calendar.MONTH - 1));

					BigDecimal totalOValue = (BigDecimal) queryValue.setParameter("idSubsidiary", subsidiary.getId())
							.getSingleResult();

					Double totalOrderValue = 0.0;
					if (totalOValue != null) {
						totalOrderValue = Double.parseDouble(totalOValue.toPlainString());
					}

					for (SubsidiaryModule subsidiaryModule : subsidiary.getSubsidiaryModules()) {
						Double modulePrice = 0.0;
						Double percentage = 0.0;
						Double totalPrice = 0.0;
						Short ordersQuantity = null;
						Date fromDate = null;
						Date toDate = null;
						boolean moduleSite = false;

						// Calcula o percentual do Módulo Loja Virtual e
						// adiciona no valor total da conta
						if (subsidiaryModule.getModule().getId().intValue() == 3) {
							moduleSite = true;

							percentage = RedeFoodConstants.DEFAULT_VIRTUAL_STORE_PERCENTAGE;

							// If set to deactivate, deactivate the module after
							// charge for it.
							if (subsidiaryModule.getDeactivate() && subsidiaryModule.getActive()) {
								subsidiaryModule.setActive(false);
								subsidiaryModule.setDateDeactivated(new Date());
								clearOrderTypes(subsidiary, subsidiaryModule);
							}

							// Calculates the total price and get the orders
							// quantity to register into subsidiary's account.
							modulePrice = totalOrderValue * (percentage / 100);

							BigInteger ordersQtd = (BigInteger) queryOrderQuantity.setParameter("idSubsidiary",
									subsidiary.getId()).getSingleResult();

							ordersQuantity = new Short(ordersQtd.toString());

							fromDate = (Date) queryFrom.setParameter("idSubsidiary", subsidiary.getId())
									.getSingleResult();

							toDate = (Date) queryTo.setParameter("idSubsidiary", subsidiary.getId()).getSingleResult();

							// If the module has a specific price to that
							// subsidiary, use it, otherwise use the default
							// price.
							Double monthlyCosts = 0.0;
							if (subsidiaryModule.getPrice() != null) {
								monthlyCosts = subsidiaryModule.getPrice();
							} else {
								monthlyCosts = subsidiaryModule.getModule().getDefaultPrice();
							}

							// faz o valor proporcional
							DateTime today = new DateTime();
							DateTime startDate = new DateTime(subsidiaryModule.getStartDate());
							Days days = Days.daysBetween(today, startDate);

							if (days.getDays() > -28) {
								Double i = (double) (days.getDays() * -1);
								Double j = (double) (i / 29);
								monthlyCosts = monthlyCosts * j;
							}

							// Sum orders percentage to fixed monthly costs
							modulePrice += monthlyCosts;

						} else if (subsidiaryModule.getModule().getValueType().contentEquals("%")
								&& subsidiaryModule.getModule().getPaymentType().equals(PaymentType.MONTHLY)
								&& subsidiaryModule.getActive() && !moduleSite) {

							// If the module has a specific price to that
							// subsidiary, use it, otherwise use the default
							// price.
							if (subsidiaryModule.getPrice() != null) {
								percentage = subsidiaryModule.getPrice();
							} else {
								percentage = subsidiaryModule.getModule().getDefaultPrice();
							}
							// If set to deactivate, deactivate the module after
							// charge for it.
							if (subsidiaryModule.getDeactivate() && subsidiaryModule.getActive()) {
								subsidiaryModule.setActive(false);
								subsidiaryModule.setDateDeactivated(new Date());
								clearOrderTypes(subsidiary, subsidiaryModule);
							}
							// Calculates the total price and get the orders
							// quantity to register into subsidiary's account.
							modulePrice = totalOrderValue * (percentage / 100);

							BigInteger ordersQtd = (BigInteger) queryOrderQuantity.setParameter("idSubsidiary",
									subsidiary.getId()).getSingleResult();

							ordersQuantity = new Short(ordersQtd.toString());

							fromDate = (Date) queryFrom.setParameter("idSubsidiary", subsidiary.getId())
									.getSingleResult();

							toDate = (Date) queryTo.setParameter("idSubsidiary", subsidiary.getId()).getSingleResult();

							// If the module is a single payment and it's not
							// been
							// charged yet, charge it and set it to charged.
						} else if (subsidiaryModule.getModule().getValueType().contentEquals("R$")
								&& subsidiaryModule.getModule().getPaymentType().equals(PaymentType.ONCE)
								&& !subsidiaryModule.getCharged() && subsidiaryModule.getActive()) {

							// If the module has a specific price to that
							// subsidiary, use it, otherwise use the default
							// price.
							if (subsidiaryModule.getPrice() != null) {
								modulePrice += subsidiaryModule.getPrice();
								subsidiaryModule.setCharged(true);
							} else {
								modulePrice = subsidiaryModule.getModule().getDefaultPrice();
								subsidiaryModule.setCharged(true);
							}
							// If set to deactivate, deactivate the module after
							// charge for it.
							if (subsidiaryModule.getDeactivate() && subsidiaryModule.getActive()) {
								subsidiaryModule.setActive(false);
								subsidiaryModule.setDateDeactivated(new Date());
								clearOrderTypes(subsidiary, subsidiaryModule);
							}
							// If the module has a monthly payment, just add to
							// account.
						} else if (subsidiaryModule.getModule().getValueType().contentEquals("R$")
								&& subsidiaryModule.getModule().getPaymentType().equals(PaymentType.MONTHLY)
								&& subsidiaryModule.getActive() && !moduleSite) {
							// If the module has a specific price to that
							// subsidiary, use it, otherwise use the default
							// price.
							if (subsidiaryModule.getPrice() != null) {
								modulePrice = subsidiaryModule.getPrice();
							} else {
								modulePrice = subsidiaryModule.getModule().getDefaultPrice();
							}

							// faz o valor proporcional
							DateTime today = new DateTime();
							DateTime startDate = new DateTime(subsidiaryModule.getStartDate());
							Days days = Days.daysBetween(today, startDate);

							if (days.getDays() > -28) {
								Double i = (double) (days.getDays() * -1);
								Double j = (double) (i / 29);
								modulePrice = modulePrice * j;
							}

							// If set to deactivate, deactivate the module after
							// charge for it.
							if (subsidiaryModule.getDeactivate() && subsidiaryModule.getActive()) {
								subsidiaryModule.setActive(false);
								subsidiaryModule.setDateDeactivated(new Date());
								// If the deactivated module is a Online or
								// Local Module, deactivate also their referred
								// OrderType
								clearOrderTypes(subsidiary, subsidiaryModule);
							}
						}
						// Creates a register to that charged value.

						if (modulePrice.intValue() != 0) {

							if (fromDate == null) {
								String queryLastLocalDate = "SELECT max(a.toDate) FROM Account a WHERE a.subsidiary.idSubsidiary = :idSubsidiary AND a.description LIKE 'Local%'";
								try {
									fromDate = (Date) em.createQuery(queryLastLocalDate)
											.setParameter("idSubsidiary", subsidiary.getId()).getSingleResult();

									if (fromDate == null) {
										fromDate = subsidiaryModule.getStartDate();
									}
								} catch (Exception e) {
									fromDate = subsidiaryModule.getStartDate();
								}
							}

							if (toDate == null) {
								toDate = new Date();
							}

							Account account = new Account((short) Calendar.getInstance().get(Calendar.MONTH), Calendar
									.getInstance().get(Calendar.YEAR), fromDate, toDate, ordersQuantity, modulePrice,
									false, subsidiary, subsidiaryModule.getModule().getName() + ": "
											+ subsidiaryModule.getModule().getDescription(), new Date());
							subsidiary.getAccountList().add(account);
						}

					}
					if (subsidiary.getAccountList() != null) {
						List<Account> subAccounts = new ArrayList<Account>(subsidiary.getAccountList());
						for (Account account : subAccounts) {
							Set<Account> setItems = new HashSet<Account>(subsidiary.getAccountList());
							subsidiary.getAccountList().clear();
							subsidiary.getAccountList().addAll(setItems);
						}
					}
					em.merge(subsidiary);
					em.flush();
					log.log(Level.INFO,
							"Account created to Subsidiary " + subsidiary.getId() + ". Date: "
									+ Calendar.getInstance().get(Calendar.MONTH) + "/"
									+ Calendar.getInstance().get(Calendar.YEAR));

					// TODO: gerar boleto
					// generateBankSlip(subsidiary);

				} catch (Exception e) {
					log.log(Level.SEVERE, "Error when creating account to subsidiary " + subsidiary.getId());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error initiating account creation.");
		}
	}

	/**
	 * Pegar as contas geradas DO DIA pra cada loja, somar os valores e gerar os
	 * boletos. Enviá-los por email. Depois tem que criar um método para a loja
	 * criar boleto em tempo real em sua conta no redefood, ou imprimir segunda
	 * via caso tenha vencido a 1ª via.
	 * 
	 * @param subsidiary
	 */
	@SuppressWarnings("unchecked")
	// @Schedule(dayOfMonth = "1", hour = "05", minute = "15", second = "45",
	// persistent = false)
	@Schedule(dayOfMonth = "24", hour = "14", minute = "40", second = "30", persistent = false)
	private void generateBankSlip() {

		try {

			List<Subsidiary> subsidiaries = em.createNamedQuery(Subsidiary.FIND_ALL_SUBSIDIARIES).getResultList();

			for (Subsidiary subsidiary : subsidiaries) {

				List<Account> accounts = em.createNamedQuery(Account.FIND_ACCOUNT_BY_GENERATED_DATE)
						.setParameter("idSubsidiary", subsidiary.getId()).setParameter("generatedDate", new Date())
						.getResultList();

				BoletoGenerator a = new BoletoGenerator();
				a.generateBankSlip(subsidiary, accounts, em.find(RedeFoodData.class, "17.168.644/0001-96"));

				for (@SuppressWarnings("unused")
				Account account : accounts) {
					// agora, precisa somar o valor e gerar o boleto. No texto
					// de
					// instruções, tem que dizer quanto de cada módulo está
					// sendo
					// cobrado. Nas 2 ultimas instruções, colocar a data do
					// vencimento e colocar que pode retirar 2ª via no site.
					// Colocar
					// também que vai aumtomaticamente pra protesto depois de 5
					// dias
					// do vencimento

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void clearOrderTypes(Subsidiary subsidiary, SubsidiaryModule subsidiaryModule) {
		// TODO: testar isso!
		List<OrderType> toRemove = new ArrayList<OrderType>();

		for (OrderType orderType : subsidiary.getOrderTypes()) {

			// If the deactivated module is Local, remove referred OrderType
			if (orderType.getType() == TypeOrder.LOCAL || orderType.getType() == TypeOrder.PHONE
					&& subsidiaryModule.getModule().getId().intValue() == 1 && subsidiaryModule.getDeactivate() == true) {
				toRemove.add(orderType);
			}

			if (orderType.getType() == TypeOrder.ONLINE && subsidiaryModule.getModule().getId().intValue() == 2
					&& subsidiaryModule.getDeactivate() == true) {
				toRemove.add(orderType);
			}
		}
		subsidiary.getOrderTypes().removeAll(toRemove);
	}

}
