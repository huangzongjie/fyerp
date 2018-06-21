package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.graly.framework.activeentity.model.ADUpdatable;

//@Table(name="V_MOVEMENT_LINE_TEMP_ESTIMATE")
/*ÔÝ¹ÀÇåµ¥*/
@Entity
public class VMovementLineTempEstimate extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="DATE_APPROVED")
	protected Date dateApproved;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;
	
	@Column(name="ASSESS_LINE_TOTAL")
	private BigDecimal assessLineTotal;
	
	@Column(name="REVERSAL_TOTAL")
	private BigDecimal reversqlTotal;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="DATE_WRITE_OFF")
	protected Date dateWriteOff;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
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

	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getAssessLineTotal() {
		return assessLineTotal;
	}

	public void setAssessLineTotal(BigDecimal assessLineTotal) {
		this.assessLineTotal = assessLineTotal;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public Date getDateWriteOff() {
		return dateWriteOff;
	}

	public void setDateWriteOff(Date dateWriteOff) {
		this.dateWriteOff = dateWriteOff;
	}

	public BigDecimal getReversqlTotal() {
		return reversqlTotal;
	}

	public void setReversqlTotal(BigDecimal reversqlTotal) {
		this.reversqlTotal = reversqlTotal;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}
	
}
