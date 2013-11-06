/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Chat", schema = "RedeFood")
public class Chat implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idChat", nullable = false)
    private Integer idChat;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "log", nullable = false, length = 65535)
    private String log;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dateAndTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAndTime;
    @JoinColumn(name = "idUser", referencedColumnName = "idUser", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User idUser;
    @JoinColumn(name = "idEmployee", referencedColumnName = "idEmployee", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Employee idEmployee;
    
    public Chat() {
    }
    
    public Chat(Integer idChat) {
	this.idChat = idChat;
    }
    
    public Chat(Integer idChat, String log, Date datetime) {
	this.idChat = idChat;
	this.log = log;
	dateAndTime = datetime;
    }
    
    public Integer getIdChat() {
	return idChat;
    }
    
    public void setIdChat(Integer idChat) {
	this.idChat = idChat;
    }
    
    public String getLog() {
	return log;
    }
    
    public void setLog(String log) {
	this.log = log;
    }
    
    public Date getDateAndTime() {
	return dateAndTime;
    }
    
    public void setDateAndTime(Date dateAndTime) {
	this.dateAndTime = dateAndTime;
    }
    
    public User getIdUser() {
	return idUser;
    }
    
    public void setIdUser(User idUser) {
	this.idUser = idUser;
    }
    
    public Employee getIdEmployee() {
	return idEmployee;
    }
    
    public void setIdEmployee(Employee idEmployee) {
	this.idEmployee = idEmployee;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (dateAndTime == null ? 0 : dateAndTime.hashCode());
	result = prime * result + (idEmployee == null ? 0 : idEmployee.hashCode());
	result = prime * result + (idUser == null ? 0 : idUser.hashCode());
	result = prime * result + (log == null ? 0 : log.hashCode());
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
	Chat other = (Chat) obj;
	if (dateAndTime == null) {
	    if (other.dateAndTime != null)
		return false;
	} else if (!dateAndTime.equals(other.dateAndTime))
	    return false;
	if (idEmployee == null) {
	    if (other.idEmployee != null)
		return false;
	} else if (!idEmployee.equals(other.idEmployee))
	    return false;
	if (idUser == null) {
	    if (other.idUser != null)
		return false;
	} else if (!idUser.equals(other.idUser))
	    return false;
	if (log == null) {
	    if (other.log != null)
		return false;
	} else if (!log.equals(other.log))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "Chat [idChat=" + idChat + ", log=" + log + ", datetime=" + dateAndTime + ", idUser=" + idUser
		+ ", idEmployee=" + idEmployee + "]";
    }
    
}
