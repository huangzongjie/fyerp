package com.graly.erp.wip.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="WIP_MOLINE_LOT")
@SequenceGenerator(name="SEQ_WIP_UID", sequenceName="WIP_SEQ", allocationSize=1)
public class ManufactureOrderLineLot implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_WIP_UID")
	@Column(name="OBJECT_RRN")
	private Long objectRrn;
	
	@Column(name="MOLINE_UID")
	private Long moLineUid;
	
	@ManyToOne
	@JoinColumn(name="LOT_RRN",referencedColumnName="OBJECT_RRN")
	private Lot lot;
	
	@Column(name="PRD_LOT_ID")
	private String lotId;
	
	@Column(name="QTY_CURRENT")
	private BigDecimal qtyCurrent;

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public Long getMoLineUid() {
		return moLineUid;
	}

	public void setMoLineUid(Long moLineUid) {
		this.moLineUid = moLineUid;
	}

	public BigDecimal getQtyCurrent() {
		return qtyCurrent;
	}

	public void setQtyCurrent(BigDecimal qtyCurrent) {
		this.qtyCurrent = qtyCurrent;
	}
}