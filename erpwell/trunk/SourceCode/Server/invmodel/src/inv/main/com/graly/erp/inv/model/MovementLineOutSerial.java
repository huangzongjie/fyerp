package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_MOVEMENT_LINE_OUTSERIAL")
public class MovementLineOutSerial extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	
	@Column(name="MOVEMENT_LINE_RRN")
	private Long movementLineRrn;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="OUTSERIAL_ID")
	private String outSerialId;

	public Long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}

	public Long getMovementLineRrn() {
		return movementLineRrn;
	}

	public void setMovementLineRrn(Long movementLineRrn) {
		this.movementLineRrn = movementLineRrn;
	}

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public String getOutSerialId() {
		return outSerialId;
	}

	public void setOutSerialId(String outSerialId) {
		this.outSerialId = outSerialId;
	}
}
