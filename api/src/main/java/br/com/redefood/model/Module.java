/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.redefood.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import br.com.redefood.model.enumtype.ModuleType;
import br.com.redefood.model.enumtype.PaymentType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Module", schema = "RedeFood")
@NamedQueries({
    @NamedQuery(name = Module.FIND_ALL_MODULE, query = "SELECT m FROM Module m"),
    @NamedQuery(name = Module.FIND_ALL_BY_MODULE_TYPE, query = "SELECT m FROM Module m WHERE m.moduleType = :moduleType"),
    @NamedQuery(name = Module.FIND_AVAILABLE_MODULE_BY_SUBSIDIARY, query = "SELECT DISTINCT m FROM Module m WHERE m.idModule NOT IN (SELECT sm.module.idModule FROM SubsidiaryModule sm WHERE sm.subsidiary.idSubsidiary = :idSubsidiary AND sm.active = true) ORDER BY m.paymentType ASC"),
    @NamedQuery(name = Module.FIND_AVAILABLE_MODULE_BY_SUBSIDIARY_AND_MODULE_TYPE, query = "SELECT DISTINCT m FROM Module m WHERE m.idModule NOT IN (SELECT sm.module.idModule FROM SubsidiaryModule sm WHERE sm.subsidiary.idSubsidiary = :idSubsidiary AND sm.active = true) AND m.moduleType = :moduleType ORDER BY m.paymentType ASC") })
public class Module implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String FIND_ALL_MODULE = "FIND_ALL_MODULE";
    public static final String FIND_ALL_BY_MODULE_TYPE = "FIND_ALL_BY_MODULE_TYPE";
    public static final String FIND_AVAILABLE_MODULE_BY_SUBSIDIARY = "FIND_AVAILABLE_MODULE_BY_SUBSIDIARY";
    public static final String FIND_AVAILABLE_MODULE_BY_SUBSIDIARY_AND_MODULE_TYPE = "FIND_AVAILABLE_MODULE_BY_SUBSIDIARY_AND_MODULE_TYPE";
    
    /**
     * Default name for Local Module, code 1, Local.
     */
    public static final Integer MODULE_LOCAL = 1;
    
    /**
     * Default name for Online Module, code 2, Square.
     */
    public static final Integer MODULE_SQUARE = 2;
    
    /**
     * Default name for Square and Private Store Module, code 3, Site + Square
     */
    public static final Integer MODULE_SITE = 3;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idModule", nullable = false)
    private Short idModule;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 60)
    private String name;
    @Basic(optional = false)
    @Column(name = "description", nullable = false, length = 600)
    private String description;
    @Basic(optional = false)
    @Column(name = "valueType", nullable = false, length = 5)
    private String valueType;
    @Max(value = 9999)
    @Min(value = 0)
    @Basic(optional = false)
    @Column(name = "defaultPrice", nullable = false, precision = 6, scale = 2)
    private Double defaultPrice;
    @Basic(optional = false)
    @Column(name = "paymentType", nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @Basic(optional = false)
    @Column(name = "moduleType", nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "module", fetch = FetchType.LAZY)
    private List<SubsidiaryModule> subsidiaryModules;
    
    public Module() {
    }
    
    public Module(Short idModule) {
	this.idModule = idModule;
    }
    
    public Module(Short idModule, String name, String description, Double defaultPrice, PaymentType paymentType) {
	this.idModule = idModule;
	this.name = name;
	this.description = description;
	this.defaultPrice = defaultPrice;
	this.paymentType = paymentType;
    }
    
    public Short getId() {
	return idModule;
    }
    
    public void setId(Short idModule) {
	this.idModule = idModule;
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
    
    public Double getDefaultPrice() {
	return defaultPrice;
    }
    
    public void setDefaultPrice(Double defaultPrice) {
	this.defaultPrice = defaultPrice;
    }
    
    public PaymentType getPaymentType() {
	return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType) {
	this.paymentType = paymentType;
    }
    
    @JsonIgnore
    public List<SubsidiaryModule> getSubsidiaryModules() {
	return subsidiaryModules;
    }
    
    public void setSubsidiaryModules(List<SubsidiaryModule> subsidiaryModuleList) {
	subsidiaryModules = subsidiaryModuleList;
    }
    
    public String getValueType() {
	return valueType;
    }
    
    public void setValueType(String valueType) {
	this.valueType = valueType;
    }
    
    public ModuleType getModuleType() {
	return moduleType;
    }
    
    public void setModuleType(ModuleType moduleType) {
	this.moduleType = moduleType;
    }
    
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (defaultPrice == null ? 0 : defaultPrice.hashCode());
	result = prime * result + (description == null ? 0 : description.hashCode());
	result = prime * result + (idModule == null ? 0 : idModule.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (paymentType == null ? 0 : paymentType.hashCode());
	result = prime * result + (subsidiaryModules == null ? 0 : subsidiaryModules.hashCode());
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
	Module other = (Module) obj;
	if (defaultPrice == null) {
	    if (other.defaultPrice != null)
		return false;
	} else if (!defaultPrice.equals(other.defaultPrice))
	    return false;
	if (description == null) {
	    if (other.description != null)
		return false;
	} else if (!description.equals(other.description))
	    return false;
	if (idModule == null) {
	    if (other.idModule != null)
		return false;
	} else if (!idModule.equals(other.idModule))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (paymentType == null) {
	    if (other.paymentType != null)
		return false;
	} else if (!paymentType.equals(other.paymentType))
	    return false;
	if (subsidiaryModules == null) {
	    if (other.subsidiaryModules != null)
		return false;
	} else if (!subsidiaryModules.equals(other.subsidiaryModules))
	    return false;
	return true;
    }
    
    @Override
    public String toString() {
	return "Module [idModule=" + idModule + ", name=" + name + ", description=" + description + ", defaultPrice="
		+ defaultPrice + ", paymentType=" + paymentType + ", subsidiaryModuleList=" + subsidiaryModules + "]";
    }
    
}
