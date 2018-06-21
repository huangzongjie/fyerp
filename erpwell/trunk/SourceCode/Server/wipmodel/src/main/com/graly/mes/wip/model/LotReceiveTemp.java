package com.graly.mes.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="WIP_LOT_RECEIVE_TEMP")
public class LotReceiveTemp extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
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
	
	@Column(name ="LOT_TYPE")
	private String lotType;

	@Column(name="USER_QC")
	private String userQc;
	
	@Column(name="MOLD_ID")
	private String moldId;
	
	@Column(name="EQUIPMENT_ID")
	protected String equipmentId;
	
	@Column(name="REVERSE_FIELD1")
	private String reverseField1;
	
	@Column(name="REVERSE_FIELD2")
	private String reverseField2;
	
	@Column(name="REVERSE_FIELD3")
	private String reverseField3;
	
	@Column(name="REVERSE_FIELD4")
	private String reverseField4;
	
	@Column(name="REVERSE_FIELD5")
	private String reverseField5;
	
	@Column(name="LOT_COMMENT")
	private String lotComment;
	
	
	
	
	
	
	
	
	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

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

	public BigDecimal getMainQty() {
		return mainQty;
	}

	public void setMainQty(BigDecimal mainQty) {
		this.mainQty = mainQty;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public String getUserQc() {
		return userQc;
	}

	public void setUserQc(String userQc) {
		this.userQc = userQc;
	}

	public String getMoldId() {
		return moldId;
	}

	public void setMoldId(String moldId) {
		this.moldId = moldId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getReverseField1() {
		return reverseField1;
	}

	public void setReverseField1(String reverseField1) {
		this.reverseField1 = reverseField1;
	}

	public String getReverseField2() {
		return reverseField2;
	}

	public void setReverseField2(String reverseField2) {
		this.reverseField2 = reverseField2;
	}

	public String getReverseField3() {
		return reverseField3;
	}

	public void setReverseField3(String reverseField3) {
		this.reverseField3 = reverseField3;
	}

	public String getReverseField4() {
		return reverseField4;
	}

	public void setReverseField4(String reverseField4) {
		this.reverseField4 = reverseField4;
	}

	public String getReverseField5() {
		return reverseField5;
	}

	public void setReverseField5(String reverseField5) {
		this.reverseField5 = reverseField5;
	}

	public String getLotComment() {
		return lotComment;
	}

	public void setLotComment(String lotComment) {
		this.lotComment = lotComment;
	}
	
	
	
	
	
	
}
