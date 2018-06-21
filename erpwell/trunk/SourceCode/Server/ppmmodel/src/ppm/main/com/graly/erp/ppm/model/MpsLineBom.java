package com.graly.erp.ppm.model;

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

import com.graly.erp.base.model.Material;

@Entity
@Table(name="PPM_MPS_LINE_BOM")
public class MpsLineBom implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "OBJECT_RRN", sequenceName="OBJECT_RRN", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBJECT_RRN")
	@Column(name="OBJECT_RRN")
	protected Long objectRrn;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MPS_LINE_RRN")
	private Long mpsLineRrn;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;

	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	@Column(name="UOM_ID")
	private String uomId;
	
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
	
	@Column(name="DESCRIPTION")
	private String description;

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public Long getMpsLineRrn() {
		return mpsLineRrn;
	}

	public void setMpsLineRrn(Long mpsLineRrn) {
		this.mpsLineRrn = mpsLineRrn;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getMaterialId();
		}
		return "";
	}
	
	public String getMaterialName() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getName();
		}
		return "";
	}
	
	public String getUomId() {
		if (this.getMaterial() != null) {
			return this.getMaterial().getInventoryUom();
		}
		return "";
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		MpsLineBom mpsBom = (MpsLineBom)super.clone();
		mpsBom.setObjectRrn(null);
		return mpsBom;
	}	
}
