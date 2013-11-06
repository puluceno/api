package br.com.redefood.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "Order_has_Meal", schema = "RedeFood")
public class OrderhasMeal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idOrders", nullable = false)
	private Integer idOrders;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idMeal", nullable = false)
	private Integer idMeal;

	public Integer getIdOrders() {
		return idOrders;
	}

	public void setIdOrders(Integer idOrders) {
		this.idOrders = idOrders;
	}

	public Integer getIdMeal() {
		return idMeal;
	}

	public void setIdMeal(Integer idMeal) {
		this.idMeal = idMeal;
	}

}
