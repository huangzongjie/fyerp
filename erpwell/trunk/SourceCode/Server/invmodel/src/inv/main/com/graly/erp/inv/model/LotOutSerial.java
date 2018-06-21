package com.graly.erp.inv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_LOT_OUTSERIAL")
public class LotOutSerial extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;

	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="OUTSERIAL_ID")
	private String outSerialId;

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getOutSerialId() {
		return outSerialId;
	}

	public void setOutSerialId(String outSerialId) {
		this.outSerialId = outSerialId;
	}
}
