package br.com.redefood.model.complex;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class RedeFoodIP {

	@JsonProperty
	private String subsidiaryIP;

	public RedeFoodIP() {
	}

	public RedeFoodIP(String subsidiaryIP) {
		this.subsidiaryIP = subsidiaryIP;
	}

	public String getSubsidiaryIP() {
		return subsidiaryIP;
	}

	public void setSubsidiaryIP(String subsidiaryIP) {
		this.subsidiaryIP = subsidiaryIP;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((subsidiaryIP == null) ? 0 : subsidiaryIP.hashCode());
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
		RedeFoodIP other = (RedeFoodIP) obj;
		if (subsidiaryIP == null) {
			if (other.subsidiaryIP != null)
				return false;
		} else if (!subsidiaryIP.equals(other.subsidiaryIP))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RedeFoodIP [subsidiaryIP=" + subsidiaryIP + "]";
	}

}
