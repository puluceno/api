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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import br.com.redefood.model.enumtype.Section;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pulu
 */
@Entity
@Table(name = "Faq", schema = "RedeFood")
@NamedQueries({
		@NamedQuery(name = Faq.FIND_ALL_FAQ, query = "SELECT f FROM Faq f ORDER BY f.exhibitionOrder ASC"),
		@NamedQuery(name = Faq.FIND_FAQ_BY_SECTION, query = "SELECT f FROM Faq f WHERE f.section = :section ORDER BY f.exhibitionOrder ASC") })
public class Faq implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL_FAQ = "FIND_ALL_FAQ";
	public static final String FIND_FAQ_BY_SECTION = "FIND_FAQ_BY_SECTION";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "idFaq", nullable = false)
	private Short idFaq;
	@Column(name = "question", length = 300)
	private String question;
	@Lob
	@Column(name = "answer", length = 65535)
	private String answer;
	@Column(name = "exhibitionOrder")
	private Integer exhibitionOrder;
	@Column(name = "section", length = 11)
	@Enumerated(EnumType.STRING)
	private Section section;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "idFaq", fetch = FetchType.LAZY)
	private List<Tip> tipList;

	public Faq() {
	}

	public Faq(Short idFaq) {
		this.idFaq = idFaq;
	}

	@JsonProperty("id")
	public Short getIdFaq() {
		return idFaq;
	}

	@JsonProperty("id")
	public void setIdFaq(Short idFaq) {
		this.idFaq = idFaq;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getExhibitionOrder() {
		return exhibitionOrder;
	}

	public void setExhibitionOrder(Integer exhibitionOrder) {
		this.exhibitionOrder = exhibitionOrder;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	@XmlTransient
	public List<Tip> getTipList() {
		return tipList;
	}

	public void setTipList(List<Tip> tipList) {
		this.tipList = tipList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + ((exhibitionOrder == null) ? 0 : exhibitionOrder.hashCode());
		result = prime * result + ((idFaq == null) ? 0 : idFaq.hashCode());
		result = prime * result + ((question == null) ? 0 : question.hashCode());
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		result = prime * result + ((tipList == null) ? 0 : tipList.hashCode());
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
		Faq other = (Faq) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (exhibitionOrder == null) {
			if (other.exhibitionOrder != null)
				return false;
		} else if (!exhibitionOrder.equals(other.exhibitionOrder))
			return false;
		if (idFaq == null) {
			if (other.idFaq != null)
				return false;
		} else if (!idFaq.equals(other.idFaq))
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		if (tipList == null) {
			if (other.tipList != null)
				return false;
		} else if (!tipList.equals(other.tipList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Faq [idFaq=" + idFaq + ", question=" + question + ", answer=" + answer + ", exhibitionOrder="
				+ exhibitionOrder + ", section=" + section + ", tipList=" + tipList + "]";
	}

}
