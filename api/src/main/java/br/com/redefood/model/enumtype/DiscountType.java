package br.com.redefood.model.enumtype;

import br.com.redefood.util.LocaleResource;

public enum DiscountType {

	FIXED, PERCENTAGE, DELIVERY_TAX;

	public String toString(String locale) {
		return LocaleResource.getProperty(locale).getProperty(name());
	}

	@Override
	public String toString() {
		return LocaleResource.getProperty("pt_br").getProperty(name());
	}

}
