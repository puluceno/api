package br.com.redefood.model.complex;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class RedeFoodPassword {

	@JsonProperty
	private String newpass;
	@JsonProperty
	private String current;

	public RedeFoodPassword() {
	}

	public RedeFoodPassword(String newpass, String current) {
		this.newpass = newpass;
		this.current = current;
	}

	public String getNewpass() {
		return newpass;
	}

	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((current == null) ? 0 : current.hashCode());
		result = prime * result + ((newpass == null) ? 0 : newpass.hashCode());
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
		RedeFoodPassword other = (RedeFoodPassword) obj;
		if (current == null) {
			if (other.current != null)
				return false;
		} else if (!current.equals(other.current))
			return false;
		if (newpass == null) {
			if (other.newpass != null)
				return false;
		} else if (!newpass.equals(other.newpass))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RedeFoodPassword [newpass=" + newpass + ", current=" + current + "]";
	}

}
