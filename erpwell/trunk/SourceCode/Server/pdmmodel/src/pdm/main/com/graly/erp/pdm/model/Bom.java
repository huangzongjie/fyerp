package com.graly.erp.pdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PDM_BOM")
public class Bom extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	
	public static final String CATEGORY_BOM = "Bom";
	public static final String CATEGORY_OPTIONAL = "Optional";
	public static final String CATEGORY_ALTERNATE = "Alternate";
	
	@Column(name="MATERIAL_PARENT_RRN")
	private Long parentRrn;
	
	@ManyToOne
	@JoinColumn(name="MATERIAL_PARENT_RRN", referencedColumnName = "OBJECT_RRN",insertable = false, updatable = false)
	private Material parentMaterial;
	
	@Column(name="MATERIAL_PARENT_VERSION")
	private Long parentVersion;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_CHILD_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material childMaterial;
	
	@Column(name="SEQ_NO")
	private Long seqNo;
	
	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="DBA_MARK")
	private String dbaMark;
	
	@Column(name="IS_PREPARE_BOM_PURCHASE")
	private String isPrepareBomPurchase;//外购件和自制件是否是预处理(由于该字段第一次需求只处理外购，而且小组成员也已经采用该字段)

	@Transient
	private String category = CATEGORY_BOM;
	
	@Transient
	private String path;
	
	@Transient
	private BigDecimal qtyBom;
	
	@Transient
	private Long bomTypeChildRrn;
	
	@Transient
	private String userName;
	
	public Long getParentRrn() {
		return parentRrn;
	}

	public void setParentRrn(Long parentRrn) {
		this.parentRrn = parentRrn;
	}

	public Long getParentVersion() {
		return parentVersion;
	}

	public void setParentVersion(Long parentVersion) {
		this.parentVersion = parentVersion;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	public void setQtyBom(BigDecimal qtyBom) {
		this.qtyBom = qtyBom;
	}

	public BigDecimal getQtyBom() {
		return qtyBom;
	}

	public Long getBomTypeChildRrn() {
		return bomTypeChildRrn;
	}
	
	public Material getParentMaterial() {
		return parentMaterial;
	}

	public void setParentMaterial(Material parentMaterial) {
		this.parentMaterial = parentMaterial;
	}
	
	@Transient
	public String getParentMaterialId(){
		String materialId = "";
		if(parentMaterial != null){
			materialId = parentMaterial.getMaterialId();
		}
		return materialId;
	}

	@Transient
	public String getParentMaterialName(){
		String materialName = "";
		if(parentMaterial != null){
			materialName = parentMaterial.getName();
		}
		return materialName;
	}
	
	@Transient
	public String getChildMaterialId(){
		String materialId = "";
		if(childMaterial != null){
			materialId = childMaterial.getMaterialId();
		}
		return materialId;
	}
	
	@Transient
	public String getChildMaterialName(){
		String materialName = "";
		if(childMaterial != null){
			materialName = childMaterial.getName();
		}
		return materialName;
	}
	
	@Transient
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setBomTypeChildRrn(Long bomTypeChildRrn) {
		this.bomTypeChildRrn = bomTypeChildRrn;
	}

	
	public String getDbaMark() {
		return dbaMark;
	}

	public void setDbaMark(String dbaMark) {
		this.dbaMark = dbaMark;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Boolean getIsPrepareBomPurchase() {
		return "Y".equalsIgnoreCase(this.isPrepareBomPurchase) ? true : false;
	}

	public void setIsPrepareBomPurchase(Boolean isPrepareBomPurchase) {
		this.isPrepareBomPurchase = isPrepareBomPurchase ? "Y" : "N";
	}
	
}
