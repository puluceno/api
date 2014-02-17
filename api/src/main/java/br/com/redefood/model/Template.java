package br.com.redefood.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Template", schema = "RedeFood")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = Template.FIND_ALL_TEMPLATES, query = "SELECT DISTINCT t FROM Template t LEFT JOIN FETCH t.themes"),
		@NamedQuery(name = Template.FIND_TEMPLATE_BY_NAME, query = "SELECT t FROM Template t WHERE t.name = :name"),
		@NamedQuery(name = Template.FIND_TEMPLATE_BY_MOBILE, query = "SELECT t FROM Template t WHERE t.mobile = :mobile") })
public class Template implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_TEMPLATES = "FIND_ALL_TEMPLATES";
	public static final String FIND_TEMPLATE_BY_NAME = "FIND_TEMPLATE_BY_NAME";
	public static final String FIND_TEMPLATE_BY_MOBILE = "FIND_TEMPLATE_BY_MOBILE";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idTemplate", nullable = false)
	private Short idTemplate;
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 60)
	private String name;
	@Basic(optional = false)
	@Column(name = "mobile", nullable = false)
	private boolean mobile;
	@Column(name = "description", length = 300)
	private String description;
	@Column(name = "preview1", length = 200)
	private String preview1;
	@Column(name = "preview2", length = 200)
	private String preview2;
	@Column(name = "preview3", length = 200)
	private String preview3;
	@Column(name = "preview4", length = 200)
	private String preview4;
	@Column(name = "demo", length = 100)
	private String demo;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "template", fetch = FetchType.LAZY)
	private List<Theme> themes;

	public Template() {
	}

	public Template(Short idTemplate) {
		this.idTemplate = idTemplate;
	}

	public Template(Short idTemplate, String name, boolean mobile) {
		this.idTemplate = idTemplate;
		this.name = name;
		this.mobile = mobile;
	}

	@JsonProperty("id")
	public Short getIdTemplate() {
		return idTemplate;
	}

	public void setIdTemplate(Short idTemplate) {
		this.idTemplate = idTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getMobile() {
		return mobile;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPreview1() {
		return preview1;
	}

	public void setPreview1(String preview1) {
		this.preview1 = preview1;
	}

	public String getPreview2() {
		return preview2;
	}

	public void setPreview2(String preview2) {
		this.preview2 = preview2;
	}

	public String getPreview3() {
		return preview3;
	}

	public void setPreview3(String preview3) {
		this.preview3 = preview3;
	}

	public String getPreview4() {
		return preview4;
	}

	public void setPreview4(String preview4) {
		this.preview4 = preview4;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public List<Theme> getThemes() {
		return themes;
	}

	public void setThemes(List<Theme> themes) {
		this.themes = themes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (idTemplate == null ? 0 : idTemplate.hashCode());
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
		Template other = (Template) obj;
		if (idTemplate == null) {
			if (other.idTemplate != null)
				return false;
		} else if (!idTemplate.equals(other.idTemplate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
