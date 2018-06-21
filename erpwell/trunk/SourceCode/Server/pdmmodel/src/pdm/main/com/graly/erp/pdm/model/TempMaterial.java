package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
 
@Entity
@Table(name="PDM_TEMP_MATERIAL")
public class TempMaterial extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="QTY")
	private BigDecimal qty;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
}	


	