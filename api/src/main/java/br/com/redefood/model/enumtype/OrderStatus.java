package br.com.redefood.model.enumtype;

import br.com.redefood.util.LocaleResource;

public enum OrderStatus {

	/**
	 * Order Sent = 0
	 */
	ORDER_SENT,
	/**
	 * Preparing = 1
	 */
	PREPARING,
	/**
	 * Delivering = 2
	 */
	DELIVERING,
	/**
	 * Delivered = 3
	 */
	DELIVERED,
	/**
	 * Canceled = 4
	 */
	CANCELED,
	/**
	 * Not Delivered = 5
	 */
	NOT_DELIVERED,
	/**
	 * Waiting Pickup = 6
	 */
	WAITING_PICKUP;

	public String toString(String locale) {
		return LocaleResource.getProperty(locale).getProperty(name());
	}

	@Override
	public String toString() {
		return LocaleResource.getProperty("pt_br").getProperty(name());
	}

}