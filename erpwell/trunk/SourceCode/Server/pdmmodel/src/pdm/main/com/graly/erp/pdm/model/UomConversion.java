package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PDM_UOM_CONVERSION")
public class UomConversion extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="UOM_TO_ID")
	private String uomToId;

	@Column(name="MULTIPLY_RATE")
	private BigDecimal multiplyRate;

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomToId(String uomToId) {
		this.uomToId = uomToId;
	}

	public String getUomToId() {
		return uomToId;
	}

	public BigDecimal getMultiplyRate() {
		return multiplyRate;
	}

	public void setMultiplyRate(BigDecimal multiplyRate) {
		this.multiplyRate = multiplyRate;
	}


}
