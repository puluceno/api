package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import br.com.redefood.model.enumtype.SubsidiaryMessageLocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "SubsidiaryMessages", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = SubsidiaryMessages.FIND_MESSAGES_BY_SUBSIDIARY_ID, query = "SELECT s FROM SubsidiaryMessages s WHERE s.subsidiary.idSubsidiary = :idSubsidiary") })
public class SubsidiaryMessages implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_MESSAGES_BY_SUBSIDIARY_ID = "FIND_MESSAGES_BY_SUBSIDIARY_ID";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idSubsidiaryMessages", nullable = false)
	private Integer idSubsidiaryMessages;
	@Basic(optional = false)
	@Column(name = "message", nullable = false, length = 500)
	private String message;
	@Column(name = "location")
	@Enumerated(EnumType.STRING)
	private SubsidiaryMessageLocation messageLocation;
	@JoinColumn(name = "subsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary subsidiary;

	public SubsidiaryMessages() {
	}

	public SubsidiaryMessages(Integer idSubsidiaryMessages) {
		this.idSubsidiaryMessages = idSubsidiaryMessages;
	}

	public SubsidiaryMessages(String message, SubsidiaryMessageLocation messageLocation) {
		this.message = message;
		this.messageLocation = messageLocation;
	}

	public SubsidiaryMessages(String message, SubsidiaryMessageLocation messageLocation, Subsidiary subsidiary) {
		this.message = message;
		this.messageLocation = messageLocation;
		this.subsidiary = subsidiary;
	}

	@JsonProperty("id")
	public Integer getId() {
		return idSubsidiaryMessages;
	}

	@JsonProperty("id")
	public void setId(Integer idSubsidiaryMessages) {
		this.idSubsidiaryMessages = idSubsidiaryMessages;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonProperty("location")
	public SubsidiaryMessageLocation getLocation() {
		return messageLocation;
	}

	@JsonProperty("location")
	public void setLocation(SubsidiaryMessageLocation messageLocation) {
		this.messageLocation = messageLocation;
	}

	@JsonIgnore
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}

	@JsonIgnore
	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idSubsidiaryMessages == null) ? 0 : idSubsidiaryMessages.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((messageLocation == null) ? 0 : messageLocation.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
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
		SubsidiaryMessages other = (SubsidiaryMessages) obj;
		if (idSubsidiaryMessages == null) {
			if (other.idSubsidiaryMessages != null)
				return false;
		} else if (!idSubsidiaryMessages.equals(other.idSubsidiaryMessages))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (messageLocation != other.messageLocation)
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
		return "SubsidiaryMessages [idSubsidiaryMessages=" + idSubsidiaryMessages + ", message=" + message
				+ ", messageLocation=" + messageLocation + ", subsidiary=" + subsidiary + "]";
	}

}