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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Theme", schema = "RedeFood")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = Theme.FIND_ALL_THEMES, query = "SELECT t FROM Theme t"),
	@NamedQuery(name = Theme.FIND_THEME_BY_NAME, query = "SELECT t FROM Theme t WHERE t.name LIKE :name"),
	@NamedQuery(name = Theme.FIND_THEMES_BY_TEMPLATE, query = "SELECT t FROM Theme t WHERE t.template.idTemplate = :idTemplate") })
public class Theme implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_THEMES = "FIND_ALL_THEMES";
	public static final String FIND_THEME_BY_NAME = "FIND_THEME_BY_NAME";
	public static final String FIND_THEMES_BY_TEMPLATE = "FIND_THEMES_BY_TEMPLATE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idTheme", nullable = false)
	private Short idTheme;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 60)
	private String name;
	@Column(name = "description", length = 300)
	private String description;
	@JoinColumn(name = "idTemplate", referencedColumnName = "idTemplate", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Template template;

	public Theme() {
	}

	public Theme(Short idTheme) {
		this.idTheme = idTheme;
	}

	public Theme(Short idTheme, String name) {
		this.idTheme = idTheme;
		this.name = name;
	}

	@JsonProperty("id")
	public Short getIdTheme() {
		return idTheme;
	}

	public void setIdTheme(Short idTheme) {
		this.idTheme = idTheme;
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

	@JsonIgnore
	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

}
