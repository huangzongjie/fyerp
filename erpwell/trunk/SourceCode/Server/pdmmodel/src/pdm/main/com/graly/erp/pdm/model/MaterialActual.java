package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="PDM_MATERIAL_ACTUAL")
public class MaterialActual extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;
	
	@Column(name="MATERILA_ACTUAL_RRN")
	private Long actualRrn;
	
	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="DESCRIPTION")
	private String description;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setChildRrn(Long childRrn) {
		this.childRrn = childRrn;
	}

	public Long getChildRrn() {
		return childRrn;
	}
	
	public Long getActualRrn() {
		return actualRrn;
	}

	public void setActualRrn(Long actualRrn) {
		this.actualRrn = actualRrn;
	}

	public BigDecimal getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
