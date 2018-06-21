package com.graly.mes.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="WIP_LOT_TEMP")
public class LotTemp extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	@Column(name = "MASTER_MO_ID")
	private String masterMoId;
	
	@Column(name = "MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name = "LOT_ID")
	private String lotID;
	
	@Column(name = "MATERIAL_ID")
	private String materialId;
	
	@Column(name = "MATERIAL_NAME")
	private String materialName;
	
	@Column(name = "MAIN_QTY")
	private BigDecimal mainQty;
	
	

	public String getLotID() {
		return lotID;
	}
	public void setLotID(String lotID) {
		this.lotID = lotID;
	}
	public String getMaterialId() {
		return materialId;
	}
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}
	public String getMaterialName() {
		return materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	public String getMasterMoId() {
		return masterMoId;
	}
	public void setMasterMoId(String masterMoId) {
		this.masterMoId = masterMoId;
	}
	public BigDecimal getMainQty() {
		return mainQty;
	}
	public void setMainQty(BigDecimal mainQty) {
		this.mainQty = mainQty;
	}
	public Long getMoLineRrn() {
		return moLineRrn;
	}
	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}
	

}
