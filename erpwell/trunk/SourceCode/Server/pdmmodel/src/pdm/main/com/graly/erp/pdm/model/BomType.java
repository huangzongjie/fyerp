package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="PDM_BOM_TYPE")
public class BomType extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long parentRrn;
	
	@Column(name="MATERIAL_PARENT_VERSION")
	private Long parentVersion;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;
	
	@Column(name="MATERIAL_CHILD_VERSION")
	private String childVersion;
	
	@Column(name="SEQ_NO")
	private Long seqNo;
	
	@Column(name="QUANTITY_PER")
	private Double quantityPer;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="DESCRIPTION")
	private String description;

	public Long getParentRrn() {
		return parentRrn;
	}

	public void setParentRrn(Long parentRrn) {
		this.parentRrn = parentRrn;
	}

	public Long getParentVersion() {
		return parentVersion;
	}

	public void setParentVersion(Long parentVersion) {
		this.parentVersion = parentVersion;
	}

	public Long getChildRrn() {
		return childRrn;
	}

	public void setChildRrn(Long childRrn) {
		this.childRrn = childRrn;
	}

	public String getChildVersion() {
		return childVersion;
	}

	public void setChildVersion(String childVersion) {
		this.childVersion = childVersion;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Double getQuantityPer() {
		return quantityPer;
	}

	public void setQuantityPer(Double quantityPer) {
		this.quantityPer = quantityPer;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
