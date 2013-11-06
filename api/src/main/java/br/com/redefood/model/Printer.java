package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Printer", schema = "RedeFood")
public class Printer implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idPrinter", nullable = false)
	private Short idPrinter;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;
	@Column(name = "ip", length = 15)
	private String ip;
	@JoinColumn(name = "idConfiguration", referencedColumnName = "idConfiguration", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Configuration configuration;
	@Column(name = "printOrder", nullable = false)
	private Boolean printOrder = false;
	@Column(name = "printOrderClient", nullable = false)
	private Boolean printOrderClient = false;
	@Column(name = "printOrderNumber", nullable = false)
	private Boolean printOrderNumber = false;
	@Column(name = "printDelivery", nullable = false)
	private Boolean printDelivery = false;

	public Printer() {
	}

	public Printer(Short idPrinter) {
		this.idPrinter = idPrinter;
	}

	public Printer(Short idPrinter, String name) {
		this.idPrinter = idPrinter;
		this.name = name;
	}

	public Short getId() {
		return idPrinter;
	}

	public void setId(Short idPrinter) {
		this.idPrinter = idPrinter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@JsonIgnore
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Boolean getPrintOrder() {
		return printOrder;
	}

	public void setPrintOrder(Boolean printOrder) {
		this.printOrder = printOrder;
	}

	public Boolean getPrintOrderClient() {
		return printOrderClient;
	}

	public void setPrintOrderClient(Boolean printOrderClient) {
		this.printOrderClient = printOrderClient;
	}

	public Boolean getPrintOrderNumber() {
		return printOrderNumber;
	}

	public void setPrintOrderNumber(Boolean printOrderNumber) {
		this.printOrderNumber = printOrderNumber;
	}

	public Boolean getPrintDelivery() {
		return printDelivery;
	}

	public void setPrintDelivery(Boolean printDelivery) {
		this.printDelivery = printDelivery;
	}

}
