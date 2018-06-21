package com.graly.erp.pdm.model;

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
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="PDM_BOM_DETAIL")
public class BomDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "OBJECT_RRN", sequenceName="OBJECT_RRN", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBJECT_RRN")
	@Column(name="OBJECT_RRN")
	protected Long objectRrn;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_VERSION")
	private Long materialVersion;
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long parentRrn;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_CHILD_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material childMaterial;

	@Column(name="PATH")
	private String path;
	
	@Column(name="PATH_LEVEL")
	private Long pathLevel;

	@Column(name="SEQ_NO")
	private Long seqNo;
	
	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="IS_PREPARE_BOM_PURCHASE")
	private String isPrepareBomPurchase;//外购件是否是预处理

	@Transient
	private BigDecimal qtyBom;
	
	@Transient
	private String description;
	
	@Transient
	private Material parentMaterial;//父物料用于标准工时统计功能
	
	@Transient
	private String isVirtual;
	
	@Column(name="REAL_PATH")
	private String realPath;
	
	@Column(name="REAL_PATH_LEVEL")
	private Long realPathLevel;
	
	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public Long getMaterialVersion() {
		return materialVersion;
	}

	public void setMaterialVersion(Long materialVersion) {
		this.materialVersion = materialVersion;
	}
	
	public void setParentRrn(Long parentRrn) {
		this.parentRrn = parentRrn;
	}

	public Long getParentRrn() {
		return parentRrn;
	}
	
	public void setChildRrn(Long childRrn) {
		this.childRrn = childRrn;
	}

	public Long getChildRrn() {
		return childRrn;
	}
	
	public Material getChildMaterial() {
		return childMaterial;
	}

	public void setChildMaterial(Material childMaterial) {
		this.childMaterial = childMaterial;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}

	public BigDecimal getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPathLevel(Long pathLevel) {
		this.pathLevel = pathLevel;
	}

	public Long getPathLevel() {
		return pathLevel;
	}

	public void setQtyBom(BigDecimal qtyBom) {
		this.qtyBom = qtyBom;
	}

	public BigDecimal getQtyBom() {
		return qtyBom;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getIsVirtual(){
		return "Y".equalsIgnoreCase(this.isVirtual) ? true : false; 
	}

	public void setIsVirtual(Boolean isVirtual) {
		this.isVirtual = isVirtual ? "Y" : "N";
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
	
	public Boolean getIsPrepareBomPurchase() {
		return "Y".equalsIgnoreCase(this.isPrepareBomPurchase) ? true : false;
	}

	public void setIsPrepareBomPurchase(Boolean isPrepareBomPurchase) {
		this.isPrepareBomPurchase = isPrepareBomPurchase ? "Y" : "N";
	}

	public Material getParentMaterial() {
		return parentMaterial;
	}

	public void setParentMaterial(Material parentMaterial) {
		this.parentMaterial = parentMaterial;
	}
	
}
