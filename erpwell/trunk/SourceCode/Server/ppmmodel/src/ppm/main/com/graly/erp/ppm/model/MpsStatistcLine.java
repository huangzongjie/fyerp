package com.graly.erp.ppm.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;

@Entity
@Table(name="PPM_MPS_STATISTC_LINE")
public class MpsStatistcLine implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "OBJECT_RRN", sequenceName="OBJECT_RRN", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBJECT_RRN")
	@Column(name="OBJECT_RRN")
	private Long objectRrn;
	
	@Transient
	private String comments;
	
	@Column(name="MPS_LINE_RRN")
	private Long mpsLineRrn;
	
	@Column(name="MPS_ID")
	private String mpsId;

	@Column(name="QTY_SALE_PLAN")
	private BigDecimal qtySalePlan;
	
	@Column(name="QTY_AVAILABLE")
	private BigDecimal qtyAvailable;
	
	@Column(name="QTY_MPS")
	private BigDecimal qtyMps = BigDecimal.ZERO;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumns( @JoinColumn(name="MATERIAL_RRN", referencedColumnName="OBJECT_RRN", insertable=false, updatable=false ))
	private Material material;
	
	@Column(name="QTY_HAND_ON")
	private BigDecimal qtyHandOn = BigDecimal.ZERO;
	
	@Column(name="QTY_TRANSIT")
	private BigDecimal qtyTransit = BigDecimal.ZERO;
	
	@Column(name="QTY_LADING")
	private BigDecimal qtyLading = BigDecimal.ZERO;
	
	@Column(name="TEMPORARY_QTY")
	private BigDecimal temporaryQty = BigDecimal.ZERO;
	
	@Column(name="INTERNAL_ORDER_QTY")
	private BigDecimal internalOrderQty = BigDecimal.ZERO;
	
	@Column(name="QTY_FORMULA")
	private BigDecimal qtyFormula = BigDecimal.ZERO;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public BigDecimal getQtySalePlan() {
		return qtySalePlan;
	}

	public void setQtySalePlan(BigDecimal qtySalePlan) {
		this.qtySalePlan = qtySalePlan;
	}

	public BigDecimal getQtyAvailable() {
		return qtyAvailable;
	}

	public void setQtyAvailable(BigDecimal qtyAvailable) {
		this.qtyAvailable = qtyAvailable;
	}

	public BigDecimal getQtyMps() {
		return qtyMps;
	}

	public void setQtyMps(BigDecimal qtyMps) {
		this.qtyMps = qtyMps;
	}

	public Long getMaterialRrn() {
		if(material != null){
			return material.getObjectRrn();
		}
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public BigDecimal getQtyHandOn() {
		return qtyHandOn;
	}

	public void setQtyHandOn(BigDecimal qtyHandOn) {
		this.qtyHandOn = qtyHandOn;
	}

	public BigDecimal getQtyTransit() {
		return qtyTransit;
	}

	public void setQtyTransit(BigDecimal qtyTransit) {
		this.qtyTransit = qtyTransit;
	}

	public BigDecimal getQtyLading() {
		return qtyLading;
	}

	public void setQtyLading(BigDecimal qtyLading) {
		this.qtyLading = qtyLading;
	}

	public BigDecimal getTemporaryQty() {
		return temporaryQty;
	}

	public void setTemporaryQty(BigDecimal temporaryQty) {
		this.temporaryQty = temporaryQty;
	}

	public BigDecimal getInternalOrderQty() {
		return internalOrderQty;
	}

	public void setInternalOrderQty(BigDecimal internalOrderQty) {
		this.internalOrderQty = internalOrderQty;
	}
	
	@Transient
	public String getMaterialId() {
		if (material != null) {
			return material.getMaterialId();
		}
		return "";
	}

	@Transient
	public String getMaterialName() {
		if (material != null) {
			return material.getName();
		}
		return "";
	}
	
	@Transient
	public String getUomId() {
		if(material != null){
			return material.getInventoryUom();
		}
		return "";
	}

	public BigDecimal getQtyFormula() {
		return qtyFormula;
	}

	public void setQtyFormula(BigDecimal qtyFormula) {
		this.qtyFormula = qtyFormula;
	}

	public Long getObjectRrn() {
		return objectRrn;
	}

	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}

	public Long getMpsLineRrn() {
		return mpsLineRrn;
	}

	public void setMpsLineRrn(Long mpsLineRrn) {
		this.mpsLineRrn = mpsLineRrn;
	}
}
