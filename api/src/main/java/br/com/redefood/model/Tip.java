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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Tip", schema = "RedeFood")
@NamedQueries({ @NamedQuery(name = Tip.FIND_ALL_TIPS, query = "SELECT t FROM Tip t") })
public class Tip implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_TIPS = "FIND_ALL_TIPS";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idTip", nullable = false)
	private Short idTip;
	@Column(name = "title", length = 100)
	private String title;
	@Lob
	@Column(name = "content", length = 65535)
	private String content;
	@JoinColumn(name = "idFaq", referencedColumnName = "idFaq", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Faq idFaq;

	public Tip() {
	}

	public Tip(Short idTip) {
		this.idTip = idTip;
	}

	@JsonProperty("id")
	public Short getIdTip() {
		return idTip;
	}

	@JsonProperty("id")
	public void setIdTip(Short idTip) {
		this.idTip = idTip;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Faq getIdFaq() {
		return idFaq;
	}

	public void setIdFaq(Faq idFaq) {
		this.idFaq = idFaq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((idFaq == null) ? 0 : idFaq.hashCode());
		result = prime * result + ((idTip == null) ? 0 : idTip.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Tip other = (Tip) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (idFaq == null) {
			if (other.idFaq != null)
				return false;
		} else if (!idFaq.equals(other.idFaq))
			return false;
		if (idTip == null) {
			if (other.idTip != null)
				return false;
		} else if (!idTip.equals(other.idTip))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tip [idTip=" + idTip + ", title=" + title + ", content=" + content + ", idFaq=" + idFaq + "]";
	}

}
