package com.graly.erp.pdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PDM_UOM")
public class Uom extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static String CONTYPE_PUR2INV = "PUR2INV"; 
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="STD_PRECISION")
	private String stdPrecision;

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStdPrecision() {
		return stdPrecision;
	}

	public void setStdPrecision(String stdPrecision) {
		this.stdPrecision = stdPrecision;
	}
}
