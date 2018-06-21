package com.graly.erp.inv.model;

import java.math.BigDecimal;

public class ConditionItem implements java.io.Serializable {
	
	private Long materialRrn;
	
	private BigDecimal number;
	
	private Long batchNumber;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public Long getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(Long batchNumber) {
		this.batchNumber = batchNumber;
	}
}
