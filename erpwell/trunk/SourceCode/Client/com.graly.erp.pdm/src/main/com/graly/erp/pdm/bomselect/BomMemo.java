package com.graly.erp.pdm.bomselect;

import java.io.Serializable;
import java.math.BigDecimal;

import com.graly.erp.base.model.Material;

public class BomMemo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long moRrn;
	
	private BigDecimal unitQty;
	
	private String description;
	
	private String category;
	
	private Material childMaterial;

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Material getChildMaterial() {
		return childMaterial;
	}

	public void setChildMaterial(Material childMaterial) {
		this.childMaterial = childMaterial;
	}

	@Override
	public boolean equals(Object arg0) {
		if(!(arg0 instanceof BomMemo))
			return false;
		if(this.getMoRrn() == null)
			return super.equals(arg0);
		return this.getMoRrn().equals(((BomMemo)arg0).getMoRrn());
	}

	@Override
	public int hashCode() {
		int hashCode  = moRrn == null ? 100 : moRrn.intValue();
		return super.hashCode() + hashCode;
	}
}
