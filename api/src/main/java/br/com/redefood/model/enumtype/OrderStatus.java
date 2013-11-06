package br.com.redefood.model.enumtype;

import br.com.redefood.util.LocaleResource;

public enum OrderStatus {

	ORDER_SENT, PREPARING, DELIVERING, DELIVERED, CANCELED, NOT_DELIVERED, WAITING_PICKUP;	

	public String toString(String locale) {
		return LocaleResource.getProperty(locale).getProperty(this.name());
	}

	@Override
	public String toString() {
		return LocaleResource.getProperty("pt_br").getProperty(this.name());
	}

}