package com.graly.erp.wip.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="REP_SCHE_BOM_LINE")
@SequenceGenerator(name="SEQ_REP_SCHE_RRN", sequenceName="REP_SCHE_RRN", allocationSize=1)
public class RepScheBomLine implements Serializable {//车间排程统计外购件
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_REP_SCHE_RRN")
	@Column(name="OBJECT_RRN")
	private Long objectRrn;
	
//	@Column(name="MO_RRN")
//	private Long moRrn;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long materialParentRrn;

	@Column(name="PATH")
	private String path;
	
	@Column(name="PATH_LEVEL")
	private Long pathLevel;
	
	@Column(name="QTY_BOM")
	private BigDecimal qtyBom;

	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="QTY_NEED")
	private BigDecimal qtyNeed;
	
	@Column(name="REAL_PATH")
	private String realPath;
	
	@Column(name="REAL_PATH_LEVEL")
	private Long realPathLevel;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="QTY")
	private BigDecimal qty = new BigDecimal("0.0");

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getMaterialParentRrn() {
		return materialParentRrn;
	}

	public void setMaterialParentRrn(Long materialParentRrn) {
		this.materialParentRrn = materialParentRrn;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getPathLevel() {
		return pathLevel;
	}

	public void setPathLevel(Long pathLevel) {
		this.pathLevel = pathLevel;
	}

	public BigDecimal getQtyBom() {
		return qtyBom;
	}

	public void setQtyBom(BigDecimal qtyBom) {
		this.qtyBom = qtyBom;
	}

	public BigDecimal getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}

	public String getRealPath() {
		return realPath;
	}

	public void setRealPath(String realPath) {
		this.realPath = realPath;
	}

	public Long getRealPathLevel() {
		return realPathLevel;
	}

	public void setRealPathLevel(Long realPathLevel) {
		this.realPathLevel = realPathLevel;
	}

	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	
	public RepScheBomLine(){
	}
	public RepScheBomLine(ManufactureOrderBom moBom){
		if(moBom!=null){
			this.setMaterialParentRrn(moBom.getMaterialParentRrn());
			this.setPath(moBom.getPath());
			this.setPathLevel(moBom.getPathLevel());
			this.setPathLevel(moBom.getPathLevel());
			this.setQtyBom(moBom.getQtyBom());
			this.setUnitQty(moBom.getUnitQty());
			this.setRealPath(moBom.getRealPath());	
			this.setRealPathLevel(moBom.getRealPathLevel());
			this.setLineNo(moBom.getLineNo());
			this.setMaterialRrn(moBom.getMaterialRrn());
			this.setQty(moBom.getQty());
		}
	}

	public BigDecimal getQtyNeed() {
		return qtyNeed;
	}

	public void setQtyNeed(BigDecimal qtyNeed) {
		this.qtyNeed = qtyNeed;
	}
	
}
