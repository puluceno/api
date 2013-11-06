package br.com.redefood.model.complex;

import org.codehaus.jackson.annotate.JsonProperty;

import br.com.redefood.model.Employee;
import br.com.redefood.model.Module;
import br.com.redefood.model.Restaurant;
import br.com.redefood.model.Subsidiary;

public class RestaurantComplexType {

	@JsonProperty
	private Restaurant restaurant;
	@JsonProperty
	private Subsidiary subsidiary;
	@JsonProperty
	private Employee employee;
	@JsonProperty
	private Module module;

	public RestaurantComplexType() {
	}

	public RestaurantComplexType(Restaurant restaurant, Subsidiary subsidiary,
			Employee employee) {
		this.restaurant = restaurant;
		this.subsidiary = subsidiary;
		this.employee = employee;
	}

	public RestaurantComplexType(Restaurant restaurant, Subsidiary subsidiary,
			Employee employee, Module module) {
		this.restaurant = restaurant;
		this.subsidiary = subsidiary;
		this.employee = employee;
		this.setModule(module);
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employee == null) ? 0 : employee.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result
				+ ((restaurant == null) ? 0 : restaurant.hashCode());
		result = prime * result
				+ ((subsidiary == null) ? 0 : subsidiary.hashCode());
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
		RestaurantComplexType other = (RestaurantComplexType) obj;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (restaurant == null) {
			if (other.restaurant != null)
				return false;
		} else if (!restaurant.equals(other.restaurant))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RestaurantComplexType [restaurant=" + restaurant
				+ ", subsidiary=" + subsidiary + ", employee=" + employee
				+ ", module=" + module + "]";
	}

}
