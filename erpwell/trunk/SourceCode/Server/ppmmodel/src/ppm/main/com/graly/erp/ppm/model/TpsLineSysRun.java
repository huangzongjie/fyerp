package com.graly.erp.ppm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
//原能临时计划运算物料
@Entity
@Table(name = "PPM_TPS_LINE_SYS")
public class TpsLineSysRun extends ADUpdatable {

	private static final long serialVersionUID = 1L;

	@Column(name = "TPS_ID")
	private String tpsId;
 
	@Column(name = "MO_ID")
	private String moId;
	
	@Column(name = "RESULT")
	private String result;

	public void setTpsId(String tpsId) {
		this.tpsId = tpsId;
	}

	public String getTpsId() {
		return tpsId;
	}
	 
	public Boolean getResult() {
		return "Y".equalsIgnoreCase(this.result) ? true : false;
	}

	public void setResult(Boolean result) {
		this.result = result ? "Y" : "N";
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}
	
}
