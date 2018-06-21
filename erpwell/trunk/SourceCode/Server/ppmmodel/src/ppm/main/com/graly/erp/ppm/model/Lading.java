package com.graly.erp.ppm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "PPM_LADING")
public class Lading extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;

	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	@Column(name = "QTY_LADING")
	private Long qtyLading;
	
	@Column(name = "MPS_ID")
	private String mpsId;
	
	@Column(name = "UOM_ID")
	private String uomId;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		if (material != null) {
			return material.getMaterialId();
		}
		return "";
	}
	
	public String getMaterialName() {
		if (material != null) {
			return material.getName();
		}
		return "";
	}
	
	public String getInventoryUom() {
		if (material != null) {
			return material.getInventoryUom();
		}
		return "";
	}
	
	public Long getQtyLading() {
		return qtyLading;
	}

	public void setQtyLading(Long qtyLading) {
		this.qtyLading = qtyLading;
	}

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	
	
}
