/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Notifications", schema = "RedeFood")
public class Notifications implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "idUser", nullable = false)
	@GeneratedValue(generator = "gen")
	@GenericGenerator(name = "gen", strategy = "foreign", parameters = @Parameter(name = "property", value = "user"))
	private Integer idUser;
	@Basic(optional = false)
	@Column(name = "emailAction", nullable = false)
	private boolean emailAction;
	@Basic(optional = false)
	@Column(name = "emailNewsletter", nullable = false)
	private boolean emailNewsletter;
	@Basic(optional = false)
	@Column(name = "smsNewsletter", nullable = false)
	private boolean smsNewsletter;
	@JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = false, insertable = false, updatable = false)
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private User user;

	public Notifications() {
	}

	public Notifications(Integer idUser) {
		this.idUser = idUser;
	}

	public Notifications(Integer idUser, boolean emailAction, boolean emailNewsletter, boolean smsNewsletter) {
		this.idUser = idUser;
		this.emailAction = emailAction;
		this.emailNewsletter = emailNewsletter;
		this.smsNewsletter = smsNewsletter;
	}

	public Notifications(boolean emailAction, boolean emailNewsletter, boolean smsNewsletter, User user) {
		this.emailAction = emailAction;
		this.emailNewsletter = emailNewsletter;
		this.smsNewsletter = smsNewsletter;
		this.user = user;
	}

	public Notifications(boolean emailAction, boolean emailNewsletter, boolean smsNewsletter) {
		this.emailAction = emailAction;
		this.emailNewsletter = emailNewsletter;
		this.smsNewsletter = smsNewsletter;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public boolean getEmailAction() {
		return emailAction;
	}

	public void setEmailAction(boolean emailAction) {
		this.emailAction = emailAction;
	}

	public boolean getEmailNewsletter() {
		return emailNewsletter;
	}

	public void setEmailNewsletter(boolean emailNewsletter) {
		this.emailNewsletter = emailNewsletter;
	}

	public boolean getSmsNewsletter() {
		return smsNewsletter;
	}

	public void setSmsNewsletter(boolean smsNewsletter) {
		this.smsNewsletter = smsNewsletter;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (emailAction ? 1231 : 1237);
		result = prime * result + (emailNewsletter ? 1231 : 1237);
		result = prime * result + (smsNewsletter ? 1231 : 1237);
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
		Notifications other = (Notifications) obj;
		if (emailAction != other.emailAction)
			return false;
		if (emailNewsletter != other.emailNewsletter)
			return false;
		if (smsNewsletter != other.smsNewsletter)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Notifications [idUser=" + idUser + ", emailAction=" + emailAction + ", emailNewsletter="
				+ emailNewsletter + ", smsNewsletter=" + smsNewsletter + ", user=" + user + "]";
	}
}
