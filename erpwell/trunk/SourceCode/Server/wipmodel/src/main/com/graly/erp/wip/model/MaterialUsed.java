package com.graly.erp.wip.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MaterialUsed implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long moRrn;
	private String moId;
	private String moStatus;
	
	private Long materialParentRrn;
	private String materialParentId;
	private String materialParentName;
	private Long bomSeqNo;
	private String bomDescription;
	private String bomStatus;
	private String bomUomId;
	
	private Long materialRrn;
	private String materialId;
	private String materialName;

	private BigDecimal qtyUsed;
	private BigDecimal qtyMoProduct;
	private BigDecimal qtyMoReceive;//已接受数
	
	private Date dateEnd;//工作令中的完成日期

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

	public String getMoStatus() {
		return moStatus;
	}

	public void setMoStatus(String moStatus) {
		this.moStatus = moStatus;
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

	public Long getBomSeqNo() {
		return bomSeqNo;
	}

	public void setBomSeqNo(Long bomSeqNo) {
		this.bomSeqNo = bomSeqNo;
	}

	public String getBomDescription() {
		return bomDescription;
	}

	public void setBomDescription(String bomDescription) {
		this.bomDescription = bomDescription;
	}

	public String getBomStatus() {
		return bomStatus;
	}

	public void setBomStatus(String bomStatus) {
		this.bomStatus = bomStatus;
	}

	public String getBomUomId() {
		return bomUomId;
	}

	public void setBomUomId(String bomUomId) {
		this.bomUomId = bomUomId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public BigDecimal getQtyUsed() {
		return qtyUsed;
	}

	public void setQtyUsed(BigDecimal qtyUsed) {
		this.qtyUsed = qtyUsed;
	}

	public BigDecimal getQtyMoProduct() {
		return qtyMoProduct;
	}

	public void setQtyMoProduct(BigDecimal qtyMoProduct) {
		this.qtyMoProduct = qtyMoProduct;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public BigDecimal getQtyMoReceive() {
		return qtyMoReceive;
	}

	public void setQtyMoReceive(BigDecimal qtyMoReceive) {
		this.qtyMoReceive = qtyMoReceive;
	}
	
}
