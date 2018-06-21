package com.graly.erp.wip.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="BAS_ERROR_LOG")
public class ErrorLog extends ADBase {

	private static final long serialVersionUID = 1L;
	
	public static String PASTYPE_MPS = "MPS";
	public static String PASTYPE_PP = "PP";
	
	@Column(name="PAS_TYPE")
	private String pasType;

	@Column(name="MPS_ID")
	private String mpsId;

	@Column(name="MPS_LINE_RRN")
	private Long mpsLineRrn;
		
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="ERR_MESSAGE")
	private String errMessage;
	
	@Column(name="ERR_DATE")
	private Date errDate;
	
	public Long getMaterialRrn() {
		return materialRrn;
	}
	
	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}
	
	public String getMaterialId() {
		return materialId;
	}
	
	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getPasType() {
		return pasType;
	}

	public void setPasType(String pasType) {
		this.pasType = pasType;
	}

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public Long getMpsLineRrn() {
		return mpsLineRrn;
	}

	public void setMpsLineRrn(Long mpsLineRrn) {
		this.mpsLineRrn = mpsLineRrn;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public Date getErrDate() {
		return errDate;
	}

	public void setErrDate(Date errDate) {
		this.errDate = errDate;
	}
	
}
