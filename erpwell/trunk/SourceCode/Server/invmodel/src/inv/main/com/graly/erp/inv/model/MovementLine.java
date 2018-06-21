package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="INV_MOVEMENT_LINE")
public class MovementLine extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="MOVEMENT_RRN")
	private Long movementRrn;
	
	@Column(name="MOVEMENT_ID")
	private String movementId;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="LOCATOR_RRN")
	private Long locatorRrn;
	
	@Column(name="LOCATOR_ID")
	private String locatorId;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_MOVEMENT")
	private BigDecimal qtyMovement;
	
	@Column(name="IQC_LINE_RRN")
	private Long iqcLineRrn;

	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;

	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal;
	
	@Column(name="VAT_RATE")
	private BigDecimal vatRate;
	
	@Column(name="ASSESS_UNIT_PRICE")
	private BigDecimal assessUnitPrice;
	
	@Column(name="ASSESS_LINE_TOTAL")
	private BigDecimal assessLineTotal;
	
	@Column(name="INVOICE_UNIT_PRICE")
	private BigDecimal invoiceUnitPrice;
	
	@Column(name="INVOICE_LINE_TOTAL")
	private BigDecimal invoiceLineTotal;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Transient
	private List<Lot> lots;
	
	@Transient
	private List<MovementLineLot> movementLots;
	
	@Column(name="EQUIPMENT_ID")
	private String equipmentId;//备件ERP系统设备ID
	
	@Column(name="EQUIPMENT_RRN")
	private Long equipmentRrn;//备件ERP系统设备rrn
	
	@Column(name="EQUIPMENT_NAME")
	private String equipmentName;//备件ERP系统名称
	
	@Column(name="XZ_USER_RRN")//行政用户objectRrn
	private String xzUserRrn;
	
	@Column(name="XZ_USER_NAME")//行政用户名
	private String xzUserName;
	
	@Column(name="XZ_DEPARTMENT")//行政部门
	private String xzDepartment;
	
	@Column(name="XZ_COMPANY")//行政公司
	private String xzCompany;
	
//	@ManyToOne
//	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
//	private Material material;
	
	@Column(name="PROJECT_NAME")//项目名称
	private String projectName;
	
	@Column(name="PROJECT_NUMBER")//项目编号
	private String projectNumber;
	
	public Long getMovementRrn() {
		return movementRrn;
	}

	public void setMovementRrn(Long movementRrn) {
		this.movementRrn = movementRrn;
	}

	public String getMovementId() {
		return movementId;
	}

	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}

	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public void setLocatorRrn(Long locatorRrn) {
		this.locatorRrn = locatorRrn;
	}

	public Long getLocatorRrn() {
		return locatorRrn;
	}

	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public String getLocatorId() {
		return locatorId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialId() {
		return materialId;
	}
	
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public String getUomId() {
		return uomId;
	}

	public BigDecimal getQtyMovement() {
		return qtyMovement;
	}

	public void setQtyMovement(BigDecimal qtyMovement) {
		this.qtyMovement = qtyMovement;
	}

	public void setIqcLineRrn(Long iqcLineRrn) {
		this.iqcLineRrn = iqcLineRrn;
	}


	public Long getIqcLineRrn() {
		return iqcLineRrn;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getLineStatus() {
		return lineStatus;
	}
	

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}
	
	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}

	public List<Lot> getLots() {
		return lots;
	}

	public void setMovementLots(List<MovementLineLot> movementLots) {
		this.movementLots = movementLots;
	}

	public List<MovementLineLot> getMovementLots() {
		return movementLots;
	}
	
	public void setPoLineRrn(Long poLineRrn) {
		this.poLineRrn = poLineRrn;
	}

	public Long getPoLineRrn() {
		return poLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setAssessUnitPrice(BigDecimal assessUnitPrice) {
		this.assessUnitPrice = assessUnitPrice;
	}

	public BigDecimal getAssessUnitPrice() {
		return assessUnitPrice;
	}

	public void setAssessLineTotal(BigDecimal assessLineTotal) {
		this.assessLineTotal = assessLineTotal;
	}

	public BigDecimal getAssessLineTotal() {
		return assessLineTotal;
	}

	public void setInvoiceUnitPrice(BigDecimal invoiceUnitPrice) {
		this.invoiceUnitPrice = invoiceUnitPrice;
	}

	public BigDecimal getInvoiceUnitPrice() {
		return invoiceUnitPrice;
	}

	public void setInvoiceLineTotal(BigDecimal invoiceLineTotal) {
		this.invoiceLineTotal = invoiceLineTotal;
	}

	public BigDecimal getInvoiceLineTotal() {
		return invoiceLineTotal;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public Long getEquipmentRrn() {
		return equipmentRrn;
	}

	public void setEquipmentRrn(Long equipmentRrn) {
		this.equipmentRrn = equipmentRrn;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public String getXzUserRrn() {
		return xzUserRrn;
	}

	public void setXzUserRrn(String xzUserRrn) {
		this.xzUserRrn = xzUserRrn;
	}

	public String getXzUserName() {
		return xzUserName;
	}

	public void setXzUserName(String xzUserName) {
		this.xzUserName = xzUserName;
	}

	public String getXzDepartment() {
		return xzDepartment;
	}

	public void setXzDepartment(String xzDepartment) {
		this.xzDepartment = xzDepartment;
	}

	public String getXzCompany() {
		return xzCompany;
	}

	public void setXzCompany(String xzCompany) {
		this.xzCompany = xzCompany;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectNumber() {
		return projectNumber;
	}

	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}

}
