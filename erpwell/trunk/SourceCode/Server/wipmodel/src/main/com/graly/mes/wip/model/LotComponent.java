package com.graly.mes.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_LOT_COMPONENT")
public class LotComponent extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
		
	@Column(name="LOT_PARENT_RRN")
	private Long lotParentRrn;
	
	@Column(name="LOT_PARENT_ID")
	private String lotParentId;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long materialParentRrn;
	
	@Column(name="MATERIAL_PARENT_ID")
	private String materialParentId;
	
	@Column(name="MATERIAL_PARENT_NAME")
	private String materialParentName;
	
	@Column(name="LOT_CHILD_RRN")
	private Long lotChildRrn;
	
	@Column(name="LOT_CHILD_ID")
	private String lotChildId;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long materialChildRrn;
	
	@Column(name="MATERIAL_CHILD_ID")
	private String materialChildId;
	
	@Column(name="MATERIAL_CHILD_NAME")
	private String materialChildName;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name="QTY_PRODUCT")
	private BigDecimal qtyProduct;
	
	@Column(name="QTY_CONSUME")
	private BigDecimal qtyConsume;

	@Column(name="SEQ_NO")
	private Long seqNo;
	
	public Long getLotParentRrn() {
		return lotParentRrn;
	}

	public void setLotParentRrn(Long lotParentRrn) {
		this.lotParentRrn = lotParentRrn;
	}

	public String getLotParentId() {
		return lotParentId;
	}

	public void setLotParentId(String lotParentId) {
		this.lotParentId = lotParentId;
	}

	public Long getMaterialParentRrn() {
		return materialParentRrn;
	}

	public void setMaterialParentRrn(Long materialParentRrn) {
		this.materialParentRrn = materialParentRrn;
	}

	public String getMaterialParentId() {
		return materialParentId;
	}

	public void setMaterialParentId(String materialParentId) {
		this.materialParentId = materialParentId;
	}

	public String getMaterialParentName() {
		return materialParentName;
	}

	public void setMaterialParentName(String materialParentName) {
		this.materialParentName = materialParentName;
	}

	public Long getLotChildRrn() {
		return lotChildRrn;
	}

	public void setLotChildRrn(Long lotChildRrn) {
		this.lotChildRrn = lotChildRrn;
	}

	public String getLotChildId() {
		return lotChildId;
	}

	public void setLotChildId(String lotChildId) {
		this.lotChildId = lotChildId;
	}

	public Long getMaterialChildRrn() {
		return materialChildRrn;
	}

	public void setMaterialChildRrn(Long materialChildRrn) {
		this.materialChildRrn = materialChildRrn;
	}

	public String getMaterialChildId() {
		return materialChildId;
	}

	public void setMaterialChildId(String materialChildId) {
		this.materialChildId = materialChildId;
	}

	public String getMaterialChildName() {
		return materialChildName;
	}

	public void setMaterialChildName(String materialChildName) {
		this.materialChildName = materialChildName;
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public BigDecimal getQtyProduct() {
		return qtyProduct;
	}

	public void setQtyProduct(BigDecimal qtyProduct) {
		this.qtyProduct = qtyProduct;
	}

	public BigDecimal getQtyConsume() {
		return qtyConsume;
	}

	public void setQtyConsume(BigDecimal qtyConsume) {
		this.qtyConsume = qtyConsume;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public Long getSeqNo() {
		return seqNo;
	}
	

}
