package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
 
@Entity
@Table(name="PDM_TEMP_FIRSTBOM")
public class TempFirstBom extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_PARENT_ID")
	private String materialParentId;
	
	@Column(name="MATERIAL_PARENT_NAME")
	private String materialParentName;
	
	@Column(name="MATERIAL_CHILD_ID")
	private String materialChildId;
	
	@Column(name="MATERIAL_CHILD_NAME")
	private String materialChildName;
	
	@Column(name="PARENT_QTY")
	private BigDecimal parentQty;
	
	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="QTY")
	private BigDecimal qty;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long parentRrn;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;

	public String getMaterialParentId() {
		return materialParentId;
	}

	public void setMaterialParentId(String materialParentId) {
		this.materialParentId = materialParentId;
	}

	public String getMaterialParentName() {
		return materialParentName;
	}

	public void setMaterialParentName(String materialParentName) {
		this.materialParentName = materialParentName;
	}

	public String getMaterialChildId() {
		return materialChildId;
	}

	public void setMaterialChildId(String materialChildId) {
		this.materialChildId = materialChildId;
	}

	public String getMaterialChildName() {
		return materialChildName;
	}

	public void setMaterialChildName(String materialChildName) {
		this.materialChildName = materialChildName;
	}

	public BigDecimal getParentQty() {
		return parentQty;
	}

	public void setParentQty(BigDecimal parentQty) {
		this.parentQty = parentQty;
	}

	public BigDecimal getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public Long getParentRrn() {
		return parentRrn;
	}

	public void setParentRrn(Long parentRrn) {
		this.parentRrn = parentRrn;
	}

	public Long getChildRrn() {
		return childRrn;
	}

	public void setChildRrn(Long childRrn) {
		this.childRrn = childRrn;
	}
}	


	