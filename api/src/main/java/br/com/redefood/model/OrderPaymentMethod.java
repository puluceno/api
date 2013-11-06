package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "OrderPaymentMethod", schema = "RedeFood")
public class OrderPaymentMethod implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idOrderPaymentMethod", nullable = false)
	private Integer idOrderPaymentMethod;
	@Max(value = 9999)
	@Min(value = 0)
	@Column(name = "value", precision = 6, scale = 2)
	private Double value;
	@Max(value = 9999)
	@Min(value = 0)
	@Column(name = "tip", precision = 6, scale = 2)
	private Double tip;
	@JoinColumn(name = "idPaymentMethod", referencedColumnName = "idPaymentMethod", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private PaymentMethod paymentMethod;
	@JoinColumn(name = "idOrders", referencedColumnName = "idOrders", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Orders order;

	public OrderPaymentMethod() {
	}

	public OrderPaymentMethod(Integer idOrderPaymentMethod) {
		this.idOrderPaymentMethod = idOrderPaymentMethod;
	}

	public Integer getIdOrderPaymentMethod() {
		return idOrderPaymentMethod;
	}

	public OrderPaymentMethod(Double value, PaymentMethod idPaymentMethod) {
		this.value = value;
		paymentMethod = idPaymentMethod;
	}

	public OrderPaymentMethod(Double value, Double tip, PaymentMethod paymentMethod, Orders order) {
		this.value = value;
		this.tip = tip;
		this.paymentMethod = paymentMethod;
		this.order = order;
	}

	public void setIdOrderPaymentMethod(Integer idOrderPaymentMethod) {
		this.idOrderPaymentMethod = idOrderPaymentMethod;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getTip() {
		return tip;
	}

	public void setTip(Double tip) {
		this.tip = tip;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod idPaymentMethod) {
		paymentMethod = idPaymentMethod;
	}

	@JsonIgnore
	public Orders getOrder() {
		return order;
	}

	public void setOrder(Orders order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idOrderPaymentMethod == null) ? 0 : idOrderPaymentMethod.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderPaymentMethod other = (OrderPaymentMethod) obj;
		if (idOrderPaymentMethod == null) {
			if (other.idOrderPaymentMethod != null)
				return false;
		} else if (!idOrderPaymentMethod.equals(other.idOrderPaymentMethod))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (paymentMethod == null) {
			if (other.paymentMethod != null)
				return false;
		} else if (!paymentMethod.equals(other.paymentMethod))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderPaymentMethod [idOrderPaymentMethod=" + idOrderPaymentMethod + ", value=" + value
				+ ", idPaymentMethod=" + paymentMethod + ", idOrders=" + order + "]";
	}

}
