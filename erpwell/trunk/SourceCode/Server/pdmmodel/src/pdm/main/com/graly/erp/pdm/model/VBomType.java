package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_PDM_BOMTYPE")
public class VBomType extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_ACTIVE = "Active";
	public static final String STATUS_INACTIVE = "InActive";
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="BOM_RRN")
	private Long bomRrn;
	
	@Column(name="BOM_ID")
	private String bomId;
	
	@Column(name="IS_LOT_CONTROL")
	private String isLotControl;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getBomRrn() {
		return bomRrn;
	}

	public void setBomRrn(Long bomRrn) {
		this.bomRrn = bomRrn;
	}

	public void setBomId(String bomId) {
		this.bomId = bomId;
	}

	public String getBomId() {
		return bomId;
	}

	public String getIsLotControl() {
		return isLotControl;
	}

	public void setIsLotControl(String isLotControl) {
		this.isLotControl = isLotControl;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}
}
