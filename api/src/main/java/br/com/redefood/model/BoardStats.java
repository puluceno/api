package br.com.redefood.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "BoardStats", schema = "RedeFood")
public class BoardStats implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idBoardStats", nullable = false)
	private Integer idBoardStats;
	@Column(name = "boardNumber")
	private Short boardNumber;
	@Column(name = "openTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date openTime;
	@Column(name = "closeTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closeTime;
	@Column(name = "peopleNumber")
	private Short peopleNumber;
	@Max(value = 999999)
	@Min(value = 0)
	@Column(name = "totalBill", precision = 8, scale = 2)
	private BigDecimal totalBill;
	@JoinColumn(name = "idBoard", referencedColumnName = "idBoard", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Board board;

	public BoardStats() {
	}

	public BoardStats(Short boardNumber, Date openTime, Date closeTime, Short peopleNumber, BigDecimal totalBill,
			Board board) {
		this.boardNumber = boardNumber;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.peopleNumber = peopleNumber;
		this.totalBill = totalBill;
		this.board = board;
	}

	public BoardStats(Integer idBoardStats) {
		this.idBoardStats = idBoardStats;
	}

	public Integer getIdBoardStats() {
		return idBoardStats;
	}

	public void setIdBoardStats(Integer idBoardStats) {
		this.idBoardStats = idBoardStats;
	}

	public Short getBoardNumber() {
		return boardNumber;
	}

	public void setBoardNumber(Short boardNumber) {
		this.boardNumber = boardNumber;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public Short getPeopleNumber() {
		return peopleNumber;
	}

	public void setPeopleNumber(Short peopleNumber) {
		this.peopleNumber = peopleNumber;
	}

	public BigDecimal getTotalBill() {
		return totalBill;
	}

	public void setTotalBill(BigDecimal totalBill) {
		this.totalBill = totalBill;
	}

	@JsonIgnore
	public Board getIdBoard() {
		return board;
	}

	@JsonIgnore
	public void setIdBoard(Board idBoard) {
		board = idBoard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boardNumber == null) ? 0 : boardNumber.hashCode());
		result = prime * result + ((closeTime == null) ? 0 : closeTime.hashCode());
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + ((idBoardStats == null) ? 0 : idBoardStats.hashCode());
		result = prime * result + ((openTime == null) ? 0 : openTime.hashCode());
		result = prime * result + ((peopleNumber == null) ? 0 : peopleNumber.hashCode());
		result = prime * result + ((totalBill == null) ? 0 : totalBill.hashCode());
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
		BoardStats other = (BoardStats) obj;
		if (boardNumber == null) {
			if (other.boardNumber != null)
				return false;
		} else if (!boardNumber.equals(other.boardNumber))
			return false;
		if (closeTime == null) {
			if (other.closeTime != null)
				return false;
		} else if (!closeTime.equals(other.closeTime))
			return false;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (idBoardStats == null) {
			if (other.idBoardStats != null)
				return false;
		} else if (!idBoardStats.equals(other.idBoardStats))
			return false;
		if (openTime == null) {
			if (other.openTime != null)
				return false;
		} else if (!openTime.equals(other.openTime))
			return false;
		if (peopleNumber == null) {
			if (other.peopleNumber != null)
				return false;
		} else if (!peopleNumber.equals(other.peopleNumber))
			return false;
		if (totalBill == null) {
			if (other.totalBill != null)
				return false;
		} else if (!totalBill.equals(other.totalBill))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BoardStats [idBoardStats=" + idBoardStats + ", boardNumber=" + boardNumber + ", openTime=" + openTime
				+ ", closeTime=" + closeTime + ", peopleNumber=" + peopleNumber + ", totalBill=" + totalBill
				+ ", idBoard=" + board + "]";
	}

}