package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("T")
public class MovementTransfer extends Movement{

	private static final long serialVersionUID = 1L;
	public static final String DBA_MARK_TRANSFER_CANA = "TRANSFER_TO_CANA";
	@Column(name="TARGET_WAREHOUSE_RRN")
	private Long targetWarehouseRrn;
	
	@Column(name="TARGET_WAREHOUSE_ID")
	private String targetWarehouseId;
	
	@Column(name="TRS_TYPE")
	private String trsType;

	public void setTargetWarehouseRrn(Long targetWarehouseRrn) {
		this.targetWarehouseRrn = targetWarehouseRrn;
	}

	public Long getTargetWarehouseRrn() {
		return targetWarehouseRrn;
	}

	public void setTargetWarehouseId(String targetWarehouseId) {
		this.targetWarehouseId = targetWarehouseId;
	}

	public String getTargetWarehouseId() {
		return targetWarehouseId;
	}

	public String getTrsType() {
		return trsType;
	}

	public void setTrsType(String trsType) {
		this.trsType = trsType;
	}
}
