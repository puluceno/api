package br.com.redefood.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "BankSlip", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = "BankSlip.findByGeneratedDate", query = "SELECT b FROM BankSlip b WHERE b.generatedDate LIKE :generatedDate") })
public class BankSlip implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_GENERATED_DATE = "BankSlip.findByGeneratedDate";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idBankSlip", nullable = false)
	private Integer idBankSlip;
	@Basic(optional = false)
	@Column(name = "generated_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date generatedDate;
	@Basic(optional = false)
	@Column(name = "deadline", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date deadline;
	// @Max(value=?)
	@Min(value = 3)
	@Basic(optional = false)
	@Column(name = "value", nullable = false, precision = 6, scale = 2)
	private Double value;
	@Column(name = "paymentDate")
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	@Basic(optional = false)
	@Column(name = "payed", nullable = false)
	private boolean payed;
	@JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Subsidiary idSubsidiary;

	public BankSlip() {
	}

	public BankSlip(Integer idBankSlip) {
		this.idBankSlip = idBankSlip;
	}

	public BankSlip(Integer idBankSlip, Date generatedDate, Date deadline, Double value, boolean payed) {
		this.idBankSlip = idBankSlip;
		this.generatedDate = generatedDate;
		this.deadline = deadline;
		this.value = value;
		this.payed = payed;
	}

	public Integer getIdBankSlip() {
		return idBankSlip;
	}

	public void setIdBankSlip(Integer idBankSlip) {
		this.idBankSlip = idBankSlip;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public boolean getPayed() {
		return payed;
	}

	public void setPayed(boolean payed) {
		this.payed = payed;
	}

	public Subsidiary getIdSubsidiary() {
		return idSubsidiary;
	}

	public void setIdSubsidiary(Subsidiary idSubsidiary) {
		this.idSubsidiary = idSubsidiary;
	}
}
