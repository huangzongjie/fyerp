package com.graly.erp.wip.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="LARGE_WIP_LOT")
public class LargeWipLot extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="LARGE_LOT_RRN")
	private Long largeLotRrn;
	
	@Column(name="QTY")
	private BigDecimal qty;
	
	@ManyToOne
	@JoinColumn(name = "LOT_RRN", referencedColumnName = "OBJECT_RRN",  insertable = false, updatable = false)
	private Lot lot;
	

	public LargeWipLot() {
		super();
	}

	
	public LargeWipLot(Lot lot) {
		super();
		this.lot = lot;
		this.lotRrn = lot.getObjectRrn();
		this.materialRrn = lot.getMaterialRrn();
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public Long getLargeLotRrn() {
		return largeLotRrn;
	}

	public void setLargeLotRrn(Long largeLotRrn) {
		this.largeLotRrn = largeLotRrn;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	
	@Transient
	public String getLotId(){
		if(lot != null) return lot.getLotId();
		return "";
	}


	public Long getMaterialRrn() {
		return materialRrn;
	}


	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}
}
