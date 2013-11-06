package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author pulu
 */
// @Cache(maxAge = 600)
@Entity
@Table(name = "Login", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = Login.FIND_BY_TOKEN, query = "SELECT l FROM Login l WHERE l.token = :token"),
		@NamedQuery(name = Login.FIND_BY_USERID, query = "SELECT l FROM Login l WHERE l.idUser = :idUser") })
public class Login implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_TOKEN = "FIND_BY_TOKEN";
	public static final String FIND_BY_USERID = "FIND_BY_USERID";

	@Id
	@Basic(optional = false)
	@Column(name = "token", nullable = false, length = 64)
	private String token;
	@Basic(optional = false)
	@Column(name = "idUser", nullable = false)
	private int idUser;
	@Basic(optional = false)
	@Column(name = "lastSeen", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastSeen;
	@Basic(optional = false)
	@Column(name = "ip", nullable = false, length = 15)
	private String ip;
	@Basic(optional = false)
	@Column(name = "expired", nullable = false)
	private boolean expired;

	public Login() {
	}

	public Login(String token) {
		this.token = token;
	}

	public Login(String token, int idUser, Date lastSeen, String ip) {
		this.token = token;
		this.idUser = idUser;
		this.lastSeen = lastSeen;
		this.ip = ip;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public Date getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(Date logilastSeennTime) {
		lastSeen = logilastSeennTime;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (expired ? 1231 : 1237);
		result = prime * result + idUser;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((lastSeen == null) ? 0 : lastSeen.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		Login other = (Login) obj;
		if (expired != other.expired)
			return false;
		if (idUser != other.idUser)
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (lastSeen == null) {
			if (other.lastSeen != null)
				return false;
		} else if (!lastSeen.equals(other.lastSeen))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Login [token=" + token + ", idUser=" + idUser + ", lastSeen=" + lastSeen + ", ip=" + ip + ", expired="
				+ expired + "]";
	}

}