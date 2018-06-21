package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="PDM_MATERIAL_OPTIONAL")
public class MaterialOptional extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_CHILD_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material childMaterial;

	@Column(name="MATERIAL_OPTION_RRN")
	private Long optionRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_OPTION_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material optionMaterial;
	
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

	public Long getChildRrn() {
		return childRrn;
	}

	public void setChildRrn(Long childRrn) {
		this.childRrn = childRrn;
	}
	
	public String getChildId() {
		return this.childMaterial.getMaterialId();
	}
	
	public String getChildName() {
		return this.childMaterial.getName();
	}
	
	public Long getOptionRrn() {
		return optionRrn;
	}

	public void setOptionRrn(Long optionRrn) {
		this.optionRrn = optionRrn;
	}
	
	public String getOptionId() {
		return this.optionMaterial.getMaterialId();
	}
	
	public String getOptionName() {
		return this.optionMaterial.getName();
	}
	
	public void setOptionMaterial(Material optionMaterial) {
		this.optionMaterial = optionMaterial;
	}

	public Material getOptionMaterial() {
		return optionMaterial;
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
