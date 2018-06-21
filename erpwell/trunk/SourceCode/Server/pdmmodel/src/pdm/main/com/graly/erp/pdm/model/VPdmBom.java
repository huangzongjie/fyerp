package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADBase;


@Entity
@Table(name="V_PDM_BOM")
public class VPdmBom extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="MATERIAL_PARENT_RRN")
	private Long materialParentRrn;

	@Column(name="MATERIAL_PARENT_ID")
	private String materialParentId;

	@Column(name="MATERIAL_PARENT_NAME")
	private String materialParentName;

	@Column(name="MATERIAL_CHILD_RRN")
	private Long materialChildRrn;

	@Column(name="MATERIAL_CHILD_ID")
	private String materialChildId;

	@Column(name="MATERIAL_CHILD_NAME")
	private String materialChildName;

	@Column(name="SEQ_NO")
	private Long seqNo;

	@Column(name="QTY_UNIT")
	private BigDecimal qtyUnit;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="STATUS")
	private String status;
	
	@Column(name = "QTY_ONHAND1")
	private BigDecimal qtyOnhand1;//环保-良品 库存
	
	@Column(name = "QTY_ONHAND2")
	private BigDecimal qtyOnhand2;//制造车间良品 库存
	
	@Transient
	private BigDecimal level;//BOM树结构中的level,只在部分场合用到

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

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getQtyUnit() {
		return qtyUnit;
	}

	public void setQtyUnit(BigDecimal qtyUnit) {
		this.qtyUnit = qtyUnit;
	}
	
	public BigDecimal getQtyOnhand1(){
		return qtyOnhand1;
	}
	
	public BigDecimal getQtyOnhand2(){
		return qtyOnhand2;
	}

	public void setQtyOnhand1(BigDecimal qtyOnhand1) {
		this.qtyOnhand1 = qtyOnhand1;
	}

	public void setQtyOnhand2(BigDecimal qtyOnhand2) {
		this.qtyOnhand2 = qtyOnhand2;
	}

	public BigDecimal getLevel() {
		return level;
	}

	public void setLevel(BigDecimal level) {
		this.level = level;
	}

}
