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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "User", schema = "RedeFood", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
@NamedQueries({
	@NamedQuery(name = User.FIND_USER_MIN_ID, query = "SELECT MIN(u.id) FROM User u"),
	@NamedQuery(name = User.FIND_USER_BY_ORDER_AT_SUBSIDIARY, query = "SELECT DISTINCT u FROM User u INNER JOIN u.orders o WHERE o.subsidiary.idSubsidiary = :idSubsidiary"),
	@NamedQuery(name = User.FIND_USER_BY_ID, query = "SELECT u FROM User u WHERE u.idUser = :idUser"),
	@NamedQuery(name = User.FIND_USER_BY_FIRSTNAME, query = "SELECT u FROM User u WHERE u.firstName LIKE :firstName"),
	@NamedQuery(name = User.FIND_USER_BY_EMAIL, query = "SELECT u FROM User u WHERE u.email = :email"),
	@NamedQuery(name = User.FIND_USER_BY_CELLPHONE, query = "SELECT u FROM User u WHERE u.cellphone = :cellphone"),
	@NamedQuery(name = User.FIND_USER_BY_CPF, query = "SELECT u FROM User u WHERE u.cpf = :cpf"),
	@NamedQuery(name = User.FIND_USER_BY_BIRTHDATE, query = "SELECT u FROM User u WHERE u.birthdate = :birthdate"),
	@NamedQuery(name = User.FIND_USER_BY_ACTIVATION_CODE, query = "SELECT u FROM User u WHERE u.activationCode = :activationCode"),
	@NamedQuery(name = User.FIND_USER_BY_LAST_LOGIN, query = "SELECT u FROM User u WHERE u.lastLogin = :lastLogin"),
	@NamedQuery(name = User.FIND_USER_BY_NUMBER_OF_LOGINS, query = "SELECT u FROM User u WHERE u.numberOfLogins = :numberOfLogins"),
	@NamedQuery(name = User.FIND_USER_BY_CELLPHONE_OR_PHONE, query = "SELECT DISTINCT u FROM User u JOIN FETCH u.addresses WHERE u.cellphone = :phone OR u.phone = :phone") })
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_USER_MIN_ID = "FIND_USER_MIN_ID";
	public static final String FIND_USER_BY_ORDER_AT_SUBSIDIARY = "FIND_USER_BY_ORDER_AT_SUBSIDIARY";
	public static final String FIND_USER_BY_ID = "FIND_USER_BY_ID";
	public static final String FIND_USER_BY_FIRSTNAME = "FIND_USER_BY_FIRSTNAME";
	public static final String FIND_USER_BY_EMAIL = "FIND_USER_BY_EMAIL";
	public static final String FIND_USER_BY_CELLPHONE = "FIND_USER_BY_CELLPHONE";
	public static final String FIND_USER_BY_CPF = "FIND_USER_BY_CPF";
	public static final String FIND_USER_BY_ACTIVATION_CODE = "FIND_USER_BY_ACTIVATION_CODE";
	public static final String FIND_USER_BY_LAST_LOGIN = "FIND_USER_BY_LAST_LOGIN";
	public static final String FIND_USER_BY_NUMBER_OF_LOGINS = "FIND_USER_BY_NUMBER_OF_LOGINS";
	public static final String FIND_USER_BY_BIRTHDATE = "FIND_USER_BY_BIRTHDATE";
	public static final String FIND_USER_BY_CELLPHONE_OR_PHONE = "FIND_USER_BY_CELLPHONE_OR_PHONE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idUser", nullable = false)
	private Integer idUser;
	@Basic(optional = false)
	@NotNull
	@Column(name = "firstName", nullable = false, length = 60)
	private String firstName;
	@Basic(optional = false)
	@Column(name = "lastName", nullable = false, length = 60)
	private String lastName;
	@Basic(optional = false)
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid e-mail")
	@Column(name = "email", nullable = false, length = 100)
	private String email;
	@Column(name = "phone", length = 14)
	private String phone;
	@Column(name = "cellphone", length = 14)
	private String cellphone;
	@Column(name = "cpf", length = 14)
	private String cpf;
	@Column(name = "birthdate", length = 10)
	private String birthdate;
	@Column(name = "sex")
	private Boolean sex;
	@Column(name = "photo")
	private String photo = "default/profile96.png";
	@Column(name = "image")
	private String image;
	@Basic(optional = false)
	@Column(name = "emailActive", nullable = false)
	private boolean emailActive;
	@Basic(optional = false)
	@Column(name = "cellphoneActive", nullable = false)
	private boolean cellphoneActive = false;
	@Basic(optional = false)
	@Column(name = "password", nullable = false, length = 64)
	private String password;
	@Column(name = "activationCode", length = 6)
	private String activationCode;
	@Column(name = "lastLogin")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	@Basic(optional = false)
	@Column(name = "numberOfLogins", nullable = false)
	private int numberOfLogins;
	@Column(name = "bonusPoints")
	private Integer bonusPoints;
	@Column(name = "originURL")
	private String originURL;
	@Column(name = "dateRegistered")
	private Date dateRegistered;
	@JoinTable(name = "User_has_DiscountCoupon", joinColumns = { @JoinColumn(name = "idUser", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "idDiscountCoupon", nullable = false) })
	@ManyToMany(fetch = FetchType.LAZY)
	private List<DiscountCoupon> discountCouponList;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "user", fetch = FetchType.LAZY)
	private List<Orders> orders;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "idUser", fetch = FetchType.LAZY)
	private List<Chat> chats;
	@OneToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH }, mappedBy = "user", fetch = FetchType.LAZY)
	private List<Address> addresses;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
	private List<UserSchedule> userScheduleList;
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
	private Notifications notifications;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "user", fetch = FetchType.LAZY)
	private List<Rating> ratings;
	@JoinColumn(name = "idUserOrigin", referencedColumnName = "idUserOrigin")
	@ManyToOne(fetch = FetchType.LAZY)
	private UserOrigin userOrigin;

	public User() {
	}

	public User(Integer idUser) {
		this.idUser = idUser;
	}

	public User(String firstName, String lastName, String email, String phone, String cellphone, String cpf,
			String birthdate, Boolean sex, boolean active, String password, List<Address> addressList) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.cellphone = cellphone;
		this.cpf = cpf;
		this.birthdate = birthdate;
		this.sex = sex;
		emailActive = active;
		this.password = password;
		addresses = addressList;
	}

	public User(Integer idUser, String name, String email, boolean active, String password, int numberOfLogins) {
		this.idUser = idUser;
		firstName = name;
		this.email = email;
		emailActive = active;
		this.password = password;
		this.numberOfLogins = numberOfLogins;
	}

	@JsonProperty("id")
	public Integer getId() {
		return idUser;
	}

	@JsonProperty("id")
	public void setId(Integer idUser) {
		this.idUser = idUser;
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

	public boolean isCellphoneActive() {
		return cellphoneActive;
	}

	public void setCellphoneActive(boolean cellphoneActive) {
		this.cellphoneActive = cellphoneActive;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean getEmailActive() {
		return emailActive;
	}

	public void setEmailActive(boolean active) {
		emailActive = active;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public int getNumberOfLogins() {
		return numberOfLogins;
	}

	public void setNumberOfLogins(int numberOfLogins) {
		this.numberOfLogins = numberOfLogins;
	}

	public Integer getBonusPoints() {
		return bonusPoints;
	}

	public void setBonusPoints(Integer bonusPoints) {
		this.bonusPoints = bonusPoints;
	}

	@JsonIgnore
	public String getOriginURL() {
		return originURL;
	}

	public void setOriginURL(String originURL) {
		this.originURL = originURL;
	}

	public Date getDateRegistered() {
		return dateRegistered;
	}

	public void setDateRegistered(Date dateRegistered) {
		this.dateRegistered = dateRegistered;
	}

	@XmlTransient
	@JsonIgnore
	public List<DiscountCoupon> getDiscountCoupons() {
		return discountCouponList;
	}

	public void setDiscountCoupons(List<DiscountCoupon> discountCouponList) {
		this.discountCouponList = discountCouponList;
	}

	@JsonIgnore
	public List<Orders> getOrders() {
		return orders;
	}

	@JsonIgnore
	public void setOrders(List<Orders> ordersList) {
		orders = ordersList;
	}

	@XmlTransient
	@JsonIgnore
	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chatList) {
		chats = chatList;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addressList) {
		addresses = addressList;
	}

	public List<UserSchedule> getUserSchedules() {
		return userScheduleList;
	}

	public Notifications getNotifications() {
		return notifications;
	}

	public void setNotifications(Notifications notifications) {
		this.notifications = notifications;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(List<Rating> ratings) {
		this.ratings = ratings;
	}

	public UserOrigin getUserOrigin() {
		return userOrigin;
	}

	public void setUserOrigin(UserOrigin userOrigin) {
		this.userOrigin = userOrigin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (activationCode == null ? 0 : activationCode.hashCode());
		result = prime * result + (addresses == null ? 0 : addresses.hashCode());
		result = prime * result + (birthdate == null ? 0 : birthdate.hashCode());
		result = prime * result + (bonusPoints == null ? 0 : bonusPoints.hashCode());
		result = prime * result + (cellphone == null ? 0 : cellphone.hashCode());
		result = prime * result + (cellphoneActive ? 1231 : 1237);
		result = prime * result + (chats == null ? 0 : chats.hashCode());
		result = prime * result + (cpf == null ? 0 : cpf.hashCode());
		result = prime * result + (dateRegistered == null ? 0 : dateRegistered.hashCode());
		result = prime * result + (discountCouponList == null ? 0 : discountCouponList.hashCode());
		result = prime * result + (email == null ? 0 : email.hashCode());
		result = prime * result + (emailActive ? 1231 : 1237);
		result = prime * result + (firstName == null ? 0 : firstName.hashCode());
		result = prime * result + (idUser == null ? 0 : idUser.hashCode());
		result = prime * result + (image == null ? 0 : image.hashCode());
		result = prime * result + (lastLogin == null ? 0 : lastLogin.hashCode());
		result = prime * result + (lastName == null ? 0 : lastName.hashCode());
		result = prime * result + (notifications == null ? 0 : notifications.hashCode());
		result = prime * result + numberOfLogins;
		result = prime * result + (orders == null ? 0 : orders.hashCode());
		result = prime * result + (originURL == null ? 0 : originURL.hashCode());
		result = prime * result + (password == null ? 0 : password.hashCode());
		result = prime * result + (phone == null ? 0 : phone.hashCode());
		result = prime * result + (photo == null ? 0 : photo.hashCode());
		result = prime * result + (ratings == null ? 0 : ratings.hashCode());
		result = prime * result + (sex == null ? 0 : sex.hashCode());
		result = prime * result + (userScheduleList == null ? 0 : userScheduleList.hashCode());
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
		User other = (User) obj;
		if (activationCode == null) {
			if (other.activationCode != null)
				return false;
		} else if (!activationCode.equals(other.activationCode))
			return false;
		if (addresses == null) {
			if (other.addresses != null)
				return false;
		} else if (!addresses.equals(other.addresses))
			return false;
		if (birthdate == null) {
			if (other.birthdate != null)
				return false;
		} else if (!birthdate.equals(other.birthdate))
			return false;
		if (bonusPoints == null) {
			if (other.bonusPoints != null)
				return false;
		} else if (!bonusPoints.equals(other.bonusPoints))
			return false;
		if (cellphone == null) {
			if (other.cellphone != null)
				return false;
		} else if (!cellphone.equals(other.cellphone))
			return false;
		if (cellphoneActive != other.cellphoneActive)
			return false;
		if (chats == null) {
			if (other.chats != null)
				return false;
		} else if (!chats.equals(other.chats))
			return false;
		if (cpf == null) {
			if (other.cpf != null)
				return false;
		} else if (!cpf.equals(other.cpf))
			return false;
		if (dateRegistered == null) {
			if (other.dateRegistered != null)
				return false;
		} else if (!dateRegistered.equals(other.dateRegistered))
			return false;
		if (discountCouponList == null) {
			if (other.discountCouponList != null)
				return false;
		} else if (!discountCouponList.equals(other.discountCouponList))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (emailActive != other.emailActive)
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (idUser == null) {
			if (other.idUser != null)
				return false;
		} else if (!idUser.equals(other.idUser))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (lastLogin == null) {
			if (other.lastLogin != null)
				return false;
		} else if (!lastLogin.equals(other.lastLogin))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (notifications == null) {
			if (other.notifications != null)
				return false;
		} else if (!notifications.equals(other.notifications))
			return false;
		if (numberOfLogins != other.numberOfLogins)
			return false;
		if (orders == null) {
			if (other.orders != null)
				return false;
		} else if (!orders.equals(other.orders))
			return false;
		if (originURL == null) {
			if (other.originURL != null)
				return false;
		} else if (!originURL.equals(other.originURL))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (photo == null) {
			if (other.photo != null)
				return false;
		} else if (!photo.equals(other.photo))
			return false;
		if (ratings == null) {
			if (other.ratings != null)
				return false;
		} else if (!ratings.equals(other.ratings))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (userScheduleList == null) {
			if (other.userScheduleList != null)
				return false;
		} else if (!userScheduleList.equals(other.userScheduleList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [idUser=" + idUser + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", phone=" + phone + ", cellphone=" + cellphone + ", cpf=" + cpf + ", birthdate=" + birthdate
				+ ", sex=" + sex + ", photo=" + photo + ", image=" + image + ", emailActive=" + emailActive
				+ ", cellphoneActive=" + cellphoneActive + ", password=" + password + ", activationCode="
				+ activationCode + ", lastLogin=" + lastLogin + ", numberOfLogins=" + numberOfLogins + ", bonusPoints="
				+ bonusPoints + ", originURL=" + originURL + ", dateRegistered=" + dateRegistered
				+ ", discountCouponList=" + discountCouponList + ", orders=" + orders + ", chats=" + chats
				+ ", addresses=" + addresses + ", userScheduleList=" + userScheduleList + ", notifications="
				+ notifications + ", ratings=" + ratings + "]";
	}

}
