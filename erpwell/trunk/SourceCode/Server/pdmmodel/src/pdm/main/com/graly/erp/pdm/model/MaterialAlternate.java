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
@Table(name="PDM_MATERIAL_ALTERNATE")
public class MaterialAlternate extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="PATH")
	private String path;
	
	@Column(name="MATERIAL_CHILD_RRN")
	private Long childRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_CHILD_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material childMaterial;

	@Column(name="MATERIAL_ALTERNATE_RRN")
	private Long alternateRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_ALTERNATE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material alternateMaterial;
	
	@Column(name="QTY_UNIT")
	private BigDecimal unitQty;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Transient
	private BigDecimal qtyMin = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal qtyOnHand = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal qtyAllocation = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal qtyTransit = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal qtyMoLineWip = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal qtySo = BigDecimal.ZERO;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	public Long getChildRrn() {
		return childRrn;
	}

	public void setChildRrn(Long childRrn) {
		this.childRrn = childRrn;
	}
	
	public String getChildId() {
		return this.childMaterial.getMaterialId();
	}
	
	public String getChildName() {
		return this.childMaterial.getName();
	}
	
	public Long getAlternateRrn() {
		return alternateRrn;
	}

	public void setAlternateRrn(Long alternateRrn) {
		this.alternateRrn = alternateRrn;
	}
	
	public String getAlternateId() {
		return this.alternateMaterial.getMaterialId();
	}
	
	public String getAlternateName() {
		return this.alternateMaterial.getName();
	}
	
	public void setAlternateMaterial(Material alternateMaterial) {
		this.alternateMaterial = alternateMaterial;
	}

	public Material getAlternateMaterial() {
		return alternateMaterial;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Material getChildMaterial() {
		return childMaterial;
	}

	public void setChildMaterial(Material childMaterial) {
		this.childMaterial = childMaterial;
	}

	public BigDecimal getUnitQty() {
		return unitQty;
	}

	public void setUnitQty(BigDecimal unitQty) {
		this.unitQty = unitQty;
	}
	
	@Transient//ø…∑÷≈‰ ˝
	public BigDecimal getQtyAssignable() {
		if(getQtyOnHand() != null && getQtyAllocation() != null) {
			return getQtyOnHand().subtract(getQtyAllocation());			
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public BigDecimal getQtyOnHand() {
		return qtyOnHand;
	}

	public void setQtyOnHand(BigDecimal qtyOnHand) {
		this.qtyOnHand = qtyOnHand;
	}

	public BigDecimal getQtyAllocation() {
		return qtyAllocation;
	}

	public void setQtyAllocation(BigDecimal qtyAllocation) {
		this.qtyAllocation = qtyAllocation;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyMoLineWip() {
		return qtyMoLineWip;
	}

	public void setQtyMoLineWip(BigDecimal qtyMoLineWip) {
		this.qtyMoLineWip = qtyMoLineWip;
	}

	public BigDecimal getQtySo() {
		return qtySo;
	}

	public void setQtySo(BigDecimal qtySo) {
		this.qtySo = qtySo;
	}
}
