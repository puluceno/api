package br.com.redefood.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Configuration", schema = "RedeFood")
public class Configuration implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idConfiguration")
	private Integer idConfiguration;
	@Column(name = "showOrderSent")
	private Boolean showOrderSent;
	@Column(name = "showPreparing")
	private Boolean showPreparing;
	@Column(name = "showDelivering")
	private Boolean showDelivering;
	@Column(name = "showWaiting")
	private Boolean showWaiting;
	@Column(name = "showDelivered")
	private Boolean showDelivered;
	@Column(name = "showCanceled")
	private Boolean showCanceled;
	@Column(name = "showNotDelivered")
	private Boolean showNotDelivered;
	@Column(name = "fetchTime")
	private Short fetchTime = new Short("6");
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Invalid e-mail")
	@Column(name = "receiveOrdersMailAddress", length = 100)
	private String receiveOrdersMailAddress;
	@Column(name = "receiveOrdersByMail")
	private boolean receiveOrdersByMail;
	@OneToMany(mappedBy = "configuration", fetch = FetchType.LAZY)
	private List<Subsidiary> subsidiaries;
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "configuration", fetch = FetchType.EAGER)
	private List<Printer> printers;

	public Configuration() {
	}

	public Configuration(Boolean showOrderSent, Boolean showPreparing, Boolean showDelivering, Boolean showDelivered,
			Boolean showCanceled, Boolean showNotDelivered) {
		this.showOrderSent = showOrderSent;
		this.showPreparing = showPreparing;
		this.showDelivering = showDelivering;
		this.showDelivered = showDelivered;
		this.showCanceled = showCanceled;
		this.showNotDelivered = showNotDelivered;
	}

	public Configuration(Integer idConfiguration) {
		this.idConfiguration = idConfiguration;
	}

	public Integer getId() {
		return idConfiguration;
	}

	public void setId(Integer idConfiguration) {
		this.idConfiguration = idConfiguration;
	}

	@JsonProperty("showStatus")
	public Map<String, Boolean> getShowStatus() {
		LinkedHashMap<String, Boolean> showOrderStatus = new LinkedHashMap<String, Boolean>();
		showOrderStatus.put("ORDER_SENT", getOrderSent());
		showOrderStatus.put("PREPARING", getPreparing());
		showOrderStatus.put("DELIVERING", getDelivered());
		showOrderStatus.put("DELIVERED", getDelivered());
		showOrderStatus.put("CANCELED", getCanceled());
		showOrderStatus.put("NOT_DELIVERED", getNotDelivered());
		showOrderStatus.put("WAITING_PICKUP", getShowWaiting());

		return showOrderStatus;
	}

	@JsonProperty("showStatus")
	public void setShowStatus(LinkedHashMap<String, Boolean> show) {
		setOrderSent(show.get("ORDER_SENT"));
		setPreparing(show.get("PREPARING"));
		setDelivering(show.get("DELIVERING"));
		setDelivered(show.get("DELIVERED"));
		setCanceled(show.get("CANCELED"));
		setNotDelivered(show.get("NOT_DELIVERED"));
		setShowWaiting(show.get("WAITING_PICKUP"));
	}

	@JsonIgnore
	public Boolean getOrderSent() {
		return showOrderSent;
	}

	@JsonIgnore
	public void setOrderSent(Boolean showOrderSent) {
		this.showOrderSent = showOrderSent;
	}

	@JsonIgnore
	public Boolean getPreparing() {
		return showPreparing;
	}

	@JsonIgnore
	public void setPreparing(Boolean showPreparing) {
		this.showPreparing = showPreparing;
	}

	@JsonIgnore
	public Boolean getDelivering() {
		return showDelivering;
	}

	@JsonIgnore
	public void setDelivering(Boolean showDelivering) {
		this.showDelivering = showDelivering;
	}

	public Boolean getShowWaiting() {
		return showWaiting;
	}

	public void setShowWaiting(Boolean showWaiting) {
		this.showWaiting = showWaiting;
	}

	@JsonIgnore
	public Boolean getDelivered() {
		return showDelivered;
	}

	@JsonIgnore
	public void setDelivered(Boolean showDelivered) {
		this.showDelivered = showDelivered;
	}

	@JsonIgnore
	public Boolean getCanceled() {
		return showCanceled;
	}

	@JsonIgnore
	public void setCanceled(Boolean showCanceled) {
		this.showCanceled = showCanceled;
	}

	@JsonIgnore
	public Boolean getNotDelivered() {
		return showNotDelivered;
	}

	@JsonIgnore
	public void setNotDelivered(Boolean showNotDelivered) {
		this.showNotDelivered = showNotDelivered;
	}

	public Short getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(Short fetchTime) {
		this.fetchTime = fetchTime;
	}

	public String getReceiveOrdersMailAddress() {
		return receiveOrdersMailAddress;
	}

	public void setReceiveOrdersMailAddress(String receiveOrdersMailAddress) {
		this.receiveOrdersMailAddress = receiveOrdersMailAddress;
	}

	public boolean isReceiveOrdersByMail() {
		return receiveOrdersByMail;
	}

	public void setReceiveOrdersByMail(boolean receiveOrdersByMail) {
		this.receiveOrdersByMail = receiveOrdersByMail;
	}

	@JsonIgnore
	public List<Subsidiary> getSubsidiaries() {
		return subsidiaries;
	}

	public void setSubsidiaryList(List<Subsidiary> subsidiaryList) {
		subsidiaries = subsidiaryList;
	}

	public List<Printer> getPrinters() {
		return printers;
	}

	public void setPrinters(List<Printer> printerList) {
		printers = printerList;
	}

}
