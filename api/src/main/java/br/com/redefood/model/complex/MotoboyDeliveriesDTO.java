package br.com.redefood.model.complex;

import java.io.Serializable;

public class MotoboyDeliveriesDTO implements Serializable {
	private static final long serialVersionUID = -514427439425539241L;

	private String motoboyName;
	private Short id;
	private Long numberOfDeliveries;

	public MotoboyDeliveriesDTO(String motoboyName, Short id, Long numberOfDeliveries) {
		this.motoboyName = motoboyName;
		this.id = id;
		this.numberOfDeliveries = numberOfDeliveries;
	}

	public String getMotoboyName() {
		return motoboyName;
	}

	public void setMotoboyName(String motoboyName) {
		this.motoboyName = motoboyName;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public Long getNumberOfDeliveries() {
		return numberOfDeliveries;
	}

	public void setNumberOfDeliveries(Long numberOfDeliveries) {
		this.numberOfDeliveries = numberOfDeliveries;
	}

}
