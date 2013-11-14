package br.com.redefood.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "UserOrigin", schema = "RedeFood")
public class UserOrigin implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final Short FACEBOOK = 1;
    public static final Short SQUARE = 2;
    public static final Short STORE = 3;
    public static final Short GOOGLE = 4;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Short idUserOrigin;
    @Column(length = 60)
    private String name;
    @Column(length = 300)
    private String description;
    
    public UserOrigin() {
    }
    
    public UserOrigin(Short idUserOrigin) {
	this.idUserOrigin = idUserOrigin;
    }
    
    public Short getIdUserOrigin() {
	return idUserOrigin;
    }
    
    public void setIdUserOrigin(Short idOrigin) {
	idUserOrigin = idOrigin;
    }
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public String getDescription() {
	return description;
    }
    
    public void setDescription(String description) {
	this.description = description;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (idUserOrigin == null ? 0 : idUserOrigin.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
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
	UserOrigin other = (UserOrigin) obj;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (idUserOrigin == null) {
	    if (other.idUserOrigin != null)
		return false;
	} else if (!idUserOrigin.equals(other.idUserOrigin))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "Origin [idOrigin=" + idUserOrigin + ", name=" + name + ", description=" + description + "]";
    }
    
}
