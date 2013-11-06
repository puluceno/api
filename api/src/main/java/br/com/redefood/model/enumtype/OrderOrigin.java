package br.com.redefood.model.enumtype;

import br.com.redefood.util.LocaleResource;

public enum OrderOrigin {

	SQUARE,STORE;	

	public String toString(String locale) {
		return LocaleResource.getProperty(locale).getProperty(this.name());
	}

	@Override
	public String toString() {
		return LocaleResource.getProperty("pt_br").getProperty(this.name());
	}

}