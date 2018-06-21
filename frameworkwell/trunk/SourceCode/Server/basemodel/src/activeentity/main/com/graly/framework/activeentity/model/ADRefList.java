package com.graly.framework.activeentity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="AD_REFLIST")
public class ADRefList extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="REFERENCE_NAME")
	private String referenceName;
	
	@Column(name="KEY")
	private String key;
	
	@Column(name="VALUE")
	private String value;
	
	@Column(name="SEQ_NO")
	private Long seqNo;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getReferenceName() {
		return referenceName;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
