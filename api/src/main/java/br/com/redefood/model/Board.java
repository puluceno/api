package br.com.redefood.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Board", schema = "RedeFood")
@NamedQueries({
    @NamedQuery(name = Board.FIND_ALL_BY_SUBSIDIARY, query = "SELECT b FROM Board b WHERE b.subsidiary.idSubsidiary = :idSubsidiary"),
    @NamedQuery(name = Board.FIND_MAX_NUMBER_BY_SUBSIDIARY, query = "SELECT MAX(b.number) FROM Board b WHERE b.subsidiary.idSubsidiary = :idSubsidiary"),
    @NamedQuery(name = Board.FIND_OPEN_BILL_TO_SUBSIDIARY, query = "SELECT b FROM Board b LEFT JOIN b.orders o WHERE b.subsidiary.idSubsidiary = :idSubsidiary AND b.idBoard = :idBoard and coalesce(o.boardPayed, false)=false AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = Board.FIND_CLOSED_BILL_TO_SUBSIDIARY, query = "SELECT b FROM Board b LEFT JOIN FETCH b.orders o WHERE b.subsidiary.idSubsidiary = :idSubsidiary AND b.idBoard = :idBoard and coalesce(o.boardPayed, false)=true AND o.orderStatus <> 'CANCELED'"),
    @NamedQuery(name = Board.FIND_ORDERS_TO_PAY_BY_BOARD, query = "SELECT b FROM Board b INNER JOIN FETCH b.orders o WHERE b.subsidiary.idSubsidiary = :idSubsidiary AND b.idBoard = :idBoard AND o.boardPayed = false AND o.orderStatus <> 'CANCELED'") })
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL_BY_SUBSIDIARY = "FIND_ALL_BY_SUBSIDIARY";
    public static final String FIND_MAX_NUMBER_BY_SUBSIDIARY = "FIND_MAX_NUMBER_BY_SUBSIDIARY";
    public static final String FIND_OPEN_BILL_TO_SUBSIDIARY = "FIND_OPEN_BILL_TO_SUBSIDIARY";
    public static final String FIND_CLOSED_BILL_TO_SUBSIDIARY = "FIND_CLOSED_BILL_TO_SUBSIDIARY";
    public static final String FIND_ORDERS_TO_PAY_BY_BOARD = "FIND_ORDERS_TO_PAY_BY_BOARD";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idBoard", nullable = false)
    private Integer idBoard;
    @Basic(optional = false)
    @Column(name = "available", nullable = false)
    private Boolean available = true;
    @Column(name = "number")
    private Short number;
    @Column(name = "openTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date openTime;
    @Column(name = "peopleNumber")
    private Short peopleNumber;
    @Max(value = 999999)
    @Min(value = 0)
    @Column(name = "bill", precision = 8, scale = 2)
    private BigDecimal bill = new BigDecimal(0).setScale(2);
    @Column(name = "credit", precision = 8, scale = 2)
    private BigDecimal credit;
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Orders> orders;
    @JoinColumn(name = "idSubsidiary", referencedColumnName = "idSubsidiary", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Subsidiary subsidiary;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board", fetch = FetchType.LAZY)
    private List<BoardStats> boardStats;
    
    public Board() {
    }
    
    public Board(Short number, Subsidiary subsidiary) {
	this.number = number;
	this.subsidiary = subsidiary;
    }
    
    @JsonProperty("id")
    public Integer getId() {
	return idBoard;
    }
    
    @JsonProperty("id")
    public void setId(Integer idBoard) {
	this.idBoard = idBoard;
    }
    
    public Boolean getAvailable() {
	return available;
    }
    
    public void setAvailable(Boolean available) {
	this.available = available;
    }
    
    public Short getNumber() {
	return number;
    }
    
    public void setNumber(Short number) {
	this.number = number;
    }
    
    public Date getOpenTime() {
	return openTime;
    }
    
    public void setOpenTime(Date openTime) {
	this.openTime = openTime;
    }
    
    public Short getPeopleNumber() {
	return peopleNumber;
    }
    
    public void setPeopleNumber(Short peopleNumber) {
	this.peopleNumber = peopleNumber;
    }
    
    public BigDecimal getBill() {
	return bill;
    }
    
    public void setBill(BigDecimal bill) {
	this.bill = bill;
    }
    
    public BigDecimal getCredit() {
	return credit;
    }
    
    public void setCredit(BigDecimal credit) {
	this.credit = credit;
    }
    
    public List<Orders> getOrders() {
	return orders;
    }
    
    public void setOrders(List<Orders> orders) {
	this.orders = orders;
    }
    
    @JsonIgnore
    public Subsidiary getSubsidiary() {
	return subsidiary;
    }
    
    @JsonIgnore
    public void setSubsidiary(Subsidiary subsidiary) {
	this.subsidiary = subsidiary;
    }
    
    public List<BoardStats> getBoardStats() {
	return boardStats;
    }
    
    public void setBoardStats(List<BoardStats> boardStats) {
	this.boardStats = boardStats;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (available ? 1231 : 1237);
	result = prime * result + (bill == null ? 0 : bill.hashCode());
	result = prime * result + (boardStats == null ? 0 : boardStats.hashCode());
	result = prime * result + (credit == null ? 0 : credit.hashCode());
	result = prime * result + (idBoard == null ? 0 : idBoard.hashCode());
	result = prime * result + (number == null ? 0 : number.hashCode());
	result = prime * result + (openTime == null ? 0 : openTime.hashCode());
	result = prime * result + (orders == null ? 0 : orders.hashCode());
	result = prime * result + (peopleNumber == null ? 0 : peopleNumber.hashCode());
	result = prime * result + (subsidiary == null ? 0 : subsidiary.hashCode());
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
	Board other = (Board) obj;
	if (available != other.available)
	    return false;
	if (bill == null) {
	    if (other.bill != null)
		return false;
	} else if (!bill.equals(other.bill))
	    return false;
	if (boardStats == null) {
	    if (other.boardStats != null)
		return false;
	} else if (!boardStats.equals(other.boardStats))
	    return false;
	if (credit == null) {
	    if (other.credit != null)
		return false;
	} else if (!credit.equals(other.credit))
	    return false;
	if (idBoard == null) {
	    if (other.idBoard != null)
		return false;
	} else if (!idBoard.equals(other.idBoard))
	    return false;
	if (number == null) {
	    if (other.number != null)
		return false;
	} else if (!number.equals(other.number))
	    return false;
	if (openTime == null) {
	    if (other.openTime != null)
		return false;
	} else if (!openTime.equals(other.openTime))
	    return false;
	if (orders == null) {
	    if (other.orders != null)
		return false;
	} else if (!orders.equals(other.orders))
	    return false;
	if (peopleNumber == null) {
	    if (other.peopleNumber != null)
		return false;
	} else if (!peopleNumber.equals(other.peopleNumber))
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
	return "Board [idBoard=" + idBoard + ", available=" + available + ", number=" + number + ", openTime="
		+ openTime + ", peopleNumber=" + peopleNumber + ", bill=" + bill + ", credit=" + credit + ", orders="
		+ orders + ", subsidiary=" + subsidiary + ", boardStats=" + boardStats + "]";
    }
    
}