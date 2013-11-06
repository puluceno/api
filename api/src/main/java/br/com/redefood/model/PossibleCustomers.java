package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "PossibleCustomers", schema = "RedeFood")
public class PossibleCustomers implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idPossibleCustomers", nullable = false)
	private Integer idPossibleCustomers;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 45)
	private String name;
	@Basic(optional = false)
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid e-mail")
	@Column(name = "email", nullable = false, length = 100)
	private String email;
	@Column(name = "phone", length = 17)
	private String phone;
	@Basic(optional = false)
	@Column(name = "enterprise", nullable = false, length = 45)
	private String enterprise;
	@Basic(optional = false)
	@Column(name = "position", nullable = false, length = 45)
	private String position;
	@Basic(optional = false)
	@Column(name = "city", nullable = false, length = 60)
	private String city;
	@Column(name = "whereHear", length = 100)
	private String whereHear;
	@Basic(optional = false)
	@Column(name = "inTouch", nullable = false)
	private boolean inTouch = false;
	@Basic(optional = false)
	@Column(name = "registerDate", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date registerDate = new Date();
	@Column(name = "madeContact")
	@Temporal(TemporalType.TIMESTAMP)
	private Date madeContact;

	public PossibleCustomers() {
	}

	public PossibleCustomers(Integer idPossibleCustomers) {
		this.idPossibleCustomers = idPossibleCustomers;
	}

	public PossibleCustomers(Integer idPossibleCustomers, String name, String email, String enterprise,
			String position, String city, boolean inTouch, Date registerDate) {
		this.idPossibleCustomers = idPossibleCustomers;
		this.name = name;
		this.email = email;
		this.enterprise = enterprise;
		this.position = position;
		this.city = city;
		this.inTouch = inTouch;
		this.registerDate = registerDate;
	}

	public PossibleCustomers(Integer idPossibleCustomers, String name, String email, String enterprise,
			String position, String city) {
		this.idPossibleCustomers = idPossibleCustomers;
		this.name = name;
		this.email = email;
		this.enterprise = enterprise;
		this.position = position;
		this.city = city;
	}

	public Integer getIdPossibleCustomers() {
		return idPossibleCustomers;
	}

	public void setIdPossibleCustomers(Integer idPossibleCustomers) {
		this.idPossibleCustomers = idPossibleCustomers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getWhereHear() {
		return whereHear;
	}

	public void setWhereHear(String whereHear) {
		this.whereHear = whereHear;
	}

	public boolean getInTouch() {
		return inTouch;
	}

	public void setInTouch(boolean inTouch) {
		this.inTouch = inTouch;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getMadeContact() {
		return madeContact;
	}

	public void setMadeContact(Date madeContact) {
		this.madeContact = madeContact;
	}
}