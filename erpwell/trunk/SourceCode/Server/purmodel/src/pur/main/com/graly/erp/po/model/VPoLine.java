package com.graly.erp.po.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.vdm.model.Vendor;
import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_PO_DETAIL")
public class VPoLine extends ADBase {
	private static final long serialVersionUID = 1L;

	// po
	@Column(name="PO_RRN")
	private Long poRrn;

	@Column(name="PO_ID")
	private String poId;
	
	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_STATUS")
	private String docStatus = Documentation.STATUS_DRAFTED;
	
	@Column(name="TOTAL")
	private BigDecimal total = BigDecimal.ZERO;
	
	@Column(name="PO_CREATED")
	private Date poCreated;//po创建日期
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="PREAPPROVED")
	private String preApproved;
	
	@Column(name="DATE_PREAPPROVED")
	private Date datePreApproved;
	
	@Column(name="PURCHASER")
	private String purchaser;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Transient
	private String vendorId;
	
	@Transient
	private String vendorName;
	
	@ManyToOne
	@JoinColumn(name = "VENDOR_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Vendor vendor;
	
	// poLine
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;

	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY")
	private BigDecimal qty = new BigDecimal("0.0");
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice = new BigDecimal("0.0");
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal = new BigDecimal("0.0");

	@Column(name="QTY_DELIVERED")
	private BigDecimal qtyDelivered;
	
	@Column(name="QTY_TESTED")
	private BigDecimal qtyTested;
	
	@Column(name="QTY_IN")
	private BigDecimal qtyIn;
	
	@Column(name="WIP_STATUS")
	private String wipStatus;//在途状态(收货、质检、入库)
	
	@Column(name="LINE_STATUS")
	private String lineStatus;
	
	@Column(name="DATE_END")
	private Date dateEnd;
	
	@Column(name="DATE_IN")
	private Date dateIn;
	
//	@Column(name="VENDOR_RRN")
//	private Long vendorRrn;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="URGENCY")
	private String urgency;

	@Column(name="DATE_PROMISED")
	private Date datePromised;
	
	@Column(name="IS_INSPECTION_FREE")//是否免检，采购订单查询用
	private String isInspectionFree;
	
	@Column(name="PO_DEPARTMENT")//添加所属部门
	private String poDepartment;
	
	@Column(name="USER_DESCRIPTION")//预审人--财务郑凤荣要求
	private String userDescription;
	
	@Column(name="PRE_USER")//预审人--财务郑凤荣要求
	private String preUser;
	
	public Date getDatePromised() {
		return datePromised;
	}

	public void setDatePromised(Date datePromised) {
		this.datePromised = datePromised;
	}

	public Long getPoRrn() {
		return poRrn;
	}

	public void setPoRrn(Long poRrn) {
		this.poRrn = poRrn;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(String userApproved) {
		this.userApproved = userApproved;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
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

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public BigDecimal getQtyDelivered() {
		return qtyDelivered;
	}

	public void setQtyDelivered(BigDecimal qtyDelivered) {
		this.qtyDelivered = qtyDelivered;
	}

	public BigDecimal getQtyTested() {
		return qtyTested;
	}

	public void setQtyTested(BigDecimal qtyTested) {
		this.qtyTested = qtyTested;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}
	
	public String getVendorId() {
		if(this.vendor != null) {
			return vendor.getVendorId();
		}
		return "";
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		if(this.vendor != null) {
			return vendor.getCompanyName();
		}
		return "";
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public String getPreApproved() {
		return preApproved;
	}

	public void setPreApproved(String preApproved) {
		this.preApproved = preApproved;
	}

	public Date getDatePreApproved() {
		return datePreApproved;
	}

	public void setDatePreApproved(Date datePreApproved) {
		this.datePreApproved = datePreApproved;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getWipStatus() {
		return wipStatus;
	}

	public void setWipStatus(String wipStatus) {
		this.wipStatus = wipStatus;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

	public Date getPoCreated() {
		return poCreated;
	}

	public void setPoCreated(Date poCreated) {
		this.poCreated = poCreated;
	}
	
	public Boolean getIsInspectionFree() {
		return "Y".equalsIgnoreCase(isInspectionFree) ? true : false;
	}

	public void setIsInspectionFree(Boolean isInspectionFree) {
		this.isInspectionFree = isInspectionFree ? "Y" : "N";
	}

	public String getPoDepartment() {
		return poDepartment;
	}

	public void setPoDepartment(String poDepartment) {
		this.poDepartment = poDepartment;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public String getPreUser() {
		return preUser;
	}

	public void setPreUser(String preUser) {
		this.preUser = preUser;
	}
	
	
	
}
