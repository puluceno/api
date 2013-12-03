/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Employee", schema = "RedeFood", uniqueConstraints = { @UniqueConstraint(columnNames = { "cpf" }) })
@NamedQueries({
	@NamedQuery(name = Employee.FIND_ALL_LESS_MYSELF, query = "SELECT e FROM Employee e INNER JOIN FETCH e.subsidiaryList s WHERE s.idSubsidiary = :idSubsidiary AND e.idEmployee <> :idEmployee"),
	@NamedQuery(name = Employee.FIND_BY_FIRSTNAME, query = "SELECT s.employeeList FROM Subsidiary s WHERE s.idSubsidiary = :idSubsidiary"),
	@NamedQuery(name = Employee.FIND_BY_CPF, query = "SELECT e FROM Employee e WHERE e.cpf = :cpf"),
	@NamedQuery(name = Employee.FIND_ID_PROFILE_BY_LOGIN, query = "SELECT e.profile.idProfile FROM Employee e WHERE e.idEmployee = :idEmployee"),
	@NamedQuery(name = Employee.FIND_BY_SUBSIDIARY_AND_PROFILE, query = "SELECT e FROM Employee e JOIN e.subsidiaryList s WHERE s.idSubsidiary = :idSubsidiary AND e.profile.idProfile = :idProfile ORDER BY e.firstName ASC") })
public class Employee implements Serializable {

	public static final String FIND_ALL_LESS_MYSELF = "FIND_ALL_LESS_MYSELF";
	public static final String FIND_BY_FIRSTNAME = "FIND_BY_FIRSTNAME";
	public static final String FIND_BY_CPF = "FIND_BY_CPF";
	public static final String FIND_ID_PROFILE_BY_LOGIN = "FIND_ID_PROFILE_BY_LOGIN";
	public static final String FIND_BY_SUBSIDIARY_AND_PROFILE = "FIND_BY_SUBSIDIARY_AND_PROFILE";

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idEmployee", nullable = false)
	@JsonProperty("id")
	private Short idEmployee;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 60)
	@Column(name = "firstName", nullable = false, length = 60)
	private String firstName;
	@Size(min = 1, max = 60)
	@Column(name = "lastName", length = 60)
	private String lastName;
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid e-mail")
	@Size(max = 100)
	@Column(name = "email", length = 100)
	private String email;
	@Size(max = 14)
	@Column(name = "phone", length = 14)
	private String phone;
	@Basic(optional = false)
	@NotNull
	@Size(min = 14, max = 15)
	@Column(name = "cellphone", nullable = false, length = 14)
	private String cellphone;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 14)
	@Column(name = "cpf", nullable = false, length = 14)
	private String cpf;
	@Size(max = 45)
	@Column(name = "rg", length = 45)
	private String rg;
	@Basic(optional = false)
	@NotNull
	@Column(name = "active", nullable = false)
	private boolean active;
	@Basic(optional = false)
	@Column(name = "sex")
	private boolean sex;
	@Column(name = "lastLogin")
	private Date lastLogin;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 64)
	@Column(name = "password", nullable = false, length = 64)
	private String password;
	@Column(name = "photo")
	private String photo = "default/profile96.png";
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idEmployee", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Chat> chatList;
	@JoinColumn(name = "idAddress", referencedColumnName = "idAddress", nullable = true)
	@ManyToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Address idAddress;
	@JoinColumn(name = "idProfile", referencedColumnName = "idProfile", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Profile profile;
	@JoinTable(name = "Subsidiary_has_Employee", joinColumns = { @JoinColumn(name = "idEmployee", referencedColumnName = "idEmployee", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Subsidiary> subsidiaryList;

	public Employee() {
	}

	public Employee(Short idEmployee) {
		this.idEmployee = idEmployee;
	}

	public Employee(String firstName, String lastName, String email, String phone, String cellphone, String cpf,
			String rg, boolean active, boolean sex, String password, Address idAddress, Profile idProfile) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.cellphone = cellphone;
		this.cpf = cpf;
		this.rg = rg;
		this.active = active;
		this.sex = sex;
		this.password = password;
		this.idAddress = idAddress;
		profile = idProfile;
	}

	public Employee(String firstName, String lastName, String email, String phone, String cellphone, String cpf,
			String rg, boolean active, boolean sex, String password, String photo, Address idAddress, Profile idProfile) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.cellphone = cellphone;
		this.cpf = cpf;
		this.rg = rg;
		this.active = active;
		this.sex = sex;
		this.password = password;
		this.photo = photo;
		this.idAddress = idAddress;
		profile = idProfile;
	}

	public Employee(String firstName, String cellphone, String cpf, String password, Address idAddress,
			Profile idProfile) {
		super();
		this.firstName = firstName;
		this.cellphone = cellphone;
		this.cpf = cpf;
		this.password = password;
		this.idAddress = idAddress;
		profile = idProfile;
	}

	public Employee(Short idEmployee, String name, String cellphone, String cpf, boolean active, String login,
			String password) {
		this.idEmployee = idEmployee;
		firstName = name;
		this.cellphone = cellphone;
		this.cpf = cpf;
		this.active = active;
		this.password = password;
	}

	@JsonProperty("id")
	public Short getId() {
		return idEmployee;
	}

	@JsonProperty("id")
	public void setId(Short idEmployee) {
		this.idEmployee = idEmployee;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonIgnore
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	@XmlTransient
	@JsonIgnore
	public List<Chat> getChatList() {
		return chatList;
	}

	public void setChatList(List<Chat> chatList) {
		this.chatList = chatList;
	}

	@JsonProperty("address")
	public Address getAddress() {
		return idAddress;
	}

	@JsonProperty("address")
	public void setAddress(Address idAddress) {
		this.idAddress = idAddress;
	}

	@JsonProperty("profile")
	public Profile getProfile() {
		return profile;
	}

	@JsonProperty("profile")
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@JsonIgnore
	public List<Subsidiary> getSubsidiaryList() {
		return subsidiaryList;
	}

	public void setSubsidiaryList(List<Subsidiary> subsidiaryList) {
		this.subsidiaryList = subsidiaryList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idEmployee == null ? 0 : idEmployee.hashCode());
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
		Employee other = (Employee) obj;
		if (idEmployee == null) {
			if (other.idEmployee != null)
				return false;
		} else if (!idEmployee.equals(other.idEmployee))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Employee [idEmployee=" + idEmployee + ", name=" + firstName + ", email=" + email + ", phone=" + phone
				+ ", cellphone=" + cellphone + ", cpf=" + cpf + ", rg=" + rg + ", active=" + active + ", sex=" + sex
				+ ", password=" + password + "]";
	}

}
