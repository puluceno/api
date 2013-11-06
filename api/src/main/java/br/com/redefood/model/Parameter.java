/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Parameter", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = Parameter.FIND_PARAMETER_BY_KEY, query = "SELECT p.value FROM Parameter p WHERE p.key = :key") })
public class Parameter implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_PARAMETER_BY_KEY = "FIND_PARAMETER_BY_KEY";

	@Id
	@Size(min = 1, max = 64)
	@Column(name = "key_", nullable = false, length = 64)
	@JsonIgnore
	private String key;
	@Basic(optional = false)
	@NotNull
	@Column(name = "value", nullable = false, length = 255)
	private String value;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 300)
	@Column(name = "description", nullable = false, length = 300)
	private String description;

	public Parameter() {
	}

	public Parameter(String key, String value, String description) {
		this.key = key;
		this.value = value;
		this.description = description;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		Parameter other = (Parameter) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
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
		return "Parameter [key=" + key + ", value=" + value + ", description=" + description + "]";
	}

}
