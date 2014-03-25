package br.com.redefood.model.complex;

import java.io.Serializable;
import java.util.HashMap;

public class SiteDTO implements Serializable {
	private static final long serialVersionUID = 1258051926151381705L;

	private String template;
	private String theme;
	private HashMap<String, Object> restaurant = new HashMap<String, Object>();
	private HashMap<String, Object> subsidiaries = new HashMap<String, Object>();

	public SiteDTO(String template, String theme, String restaurantName, String logo, Short idRestaurant,
			Short idSubsidiary, String subsidiaryName) {
		this.template = template;
		this.theme = theme;
		restaurant.put("logo", logo);
		restaurant.put("name", restaurantName);
		restaurant.put("id", idRestaurant);
		subsidiaries.put("name", subsidiaryName);
		subsidiaries.put("id", idSubsidiary);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public HashMap<String, Object> getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(HashMap<String, Object> restaurant) {
		this.restaurant = restaurant;
	}

	public HashMap<String, Object> getSubsidiaries() {
		return subsidiaries;
	}

	public void setSubsidiaries(HashMap<String, Object> subsidiaries) {
		this.subsidiaries = subsidiaries;
	}

}
