package com.graly.mes.wiphis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIPHIS_SMC")
public class LotHisSMC extends ADUpdatable{

	@Column(name="WIPHIS_RRN")
	private Long wipHisRrn;
	
	@Column(name="TRANS_TYPE")
	private String transType;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="FROM_LOT_RRN")
	private Long fromLotRrn;
	
	@Column(name="FROM_LOT_ID")
	private String fromLotId;

	@Column(name="FROM_SEQ_NO")
	private Long fromSeqNo;

	@Column(name="FROM_PART_RRN")
	private Long fromPartRrn;

	@Column(name="FROM_MAIN_QTY")
	private Double fromMainQty;

	@Column(name="FROM_SUB_QTY")
	private Double fromSubQty;

	@Column(name="TO_LOT_RRN")
	private Long toLotRrn;
	
	@Column(name="TO_LOT_ID")
	private String toLotId;

	@Column(name="TO_SEQ_NO")
	private Long toSeqNo;

	@Column(name="TO_PART_RRN")
	private Long toPartRrn;

	@Column(name="TO_MAIN_QTY")
	private Double toMainQty;

	@Column(name="TO_SUB_QTY")
	private Double toSubQty;

	public void setWipHisRrn(Long wipHisRrn) {
		this.wipHisRrn = wipHisRrn;
	}

	public Long getWipHisRrn() {
		return wipHisRrn;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransType() {
		return transType;
	}

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}
	
	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getLotId() {
		return lotId;
	}

	public void setFromLotId(String fromLotId) {
		this.fromLotId = fromLotId;
	}

	public String getFromLotId() {
		return fromLotId;
	}

	public void setFromSeqNo(Long fromSeqNo) {
		this.fromSeqNo = fromSeqNo;
	}

	public Long getFromSeqNo() {
		return fromSeqNo;
	}

	public void setFromPartRrn(Long fromPartRrn) {
		this.fromPartRrn = fromPartRrn;
	}

	public Long getFromPartRrn() {
		return fromPartRrn;
	}

	public void setFromMainQty(Double fromMainQty) {
		this.fromMainQty = fromMainQty;
	}

	public Double getFromMainQty() {
		return fromMainQty;
	}

	public void setFromSubQty(Double fromSubQty) {
		this.fromSubQty = fromSubQty;
	}

	public Double getFromSubQty() {
		return fromSubQty;
	}

	public Long getFromLotRrn() {
		return fromLotRrn;
	}

	public void setFromLotRrn(Long fromLotRrn) {
		this.fromLotRrn = fromLotRrn;
	}
	
	public Long getToLotRrn() {
		return toLotRrn;
	}

	public void setToLotRrn(Long toLotRrn) {
		this.toLotRrn = toLotRrn;
	}
	
	public void setToLotId(String toLotId) {
		this.toLotId = toLotId;
	}

	public String getToLotId() {
		return toLotId;
	}

	public void setToSeqNo(Long toSeqNo) {
		this.toSeqNo = toSeqNo;
	}

	public Long getToSeqNo() {
		return toSeqNo;
	}

	public void setToPartRrn(Long toPartRrn) {
		this.toPartRrn = toPartRrn;
	}

	public Long getToPartRrn() {
		return toPartRrn;
	}

	public void setToMainQty(Double toMainQty) {
		this.toMainQty = toMainQty;
	}

	public Double getToMainQty() {
		return toMainQty;
	}

	public void setToSubQty(Double toSubQty) {
		this.toSubQty = toSubQty;
	}

	public Double getToSubQty() {
		return toSubQty;
	}
}
