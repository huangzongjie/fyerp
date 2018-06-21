package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.po.model.PurchaseOrder;

@Entity
@Table(name="INV_RECEIPT")
public class Receipt extends Documentation {
	private static final long serialVersionUID = 1L;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="PO_RRN")
	private Long poRrn;
	
//	@ManyToOne
//	@JoinColumn(name="PO_RRN", referencedColumnName="OBJECT_RRN", insertable=false, updatable=false)
//	private PurchaseOrder po;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="TOTAL")
	private BigDecimal total;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;

	@Column(name="IS_IQC")
	private String isIqc = "N";
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	@JoinColumn(name = "RECEIPT_RRN", referencedColumnName = "OBJECT_RRN",insertable = false, updatable = false)
	//这里是“一对多”，指定总表的object_rrn和明细表的receipt_rrn关联
	private List<ReceiptLine> receiptLines;
	
	@Column(name="BEING_TRANSFER")
	private String beginTransfer;//是否调拨
	
	@Column(name="VENDOR_NAME")
	private String vendorName;//供应商名称
	
	@Column(name="VENDOR_ID")
	private String vendorId;//供应商ID
	
	@Column(name="PURCHASER")
	private String purchaser;//采购员
	
	@Column(name = "PI_ID")
	private String piId;//pi编号
	
	@Column(name = "INTERNAL_ORDER_ID")
	private String internalOrderId;//内部订单编号
	
	@Column(name = "WMS_WAREHOUSE")
	private String wmsWarehouse;//收货仓库

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(Long totalLines) {
		this.totalLines = totalLines;
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

	public Boolean getIsIqc(){
		return "Y".equalsIgnoreCase(this.isIqc) ? true : false; 
	}

	public void setIsIqc(Boolean isIqc) {
		this.isIqc = isIqc ? "Y" : "N";
	}
	
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Date getDateApproved() {
		return dateApproved;
	}
	
	public void setReceiptLines(List<ReceiptLine> receiptLines) {
		this.receiptLines = receiptLines;
	}

	public List<ReceiptLine> getReceiptLines() {
		return receiptLines;
	}

	public String getBeginTransfer() {
		return beginTransfer;
	}

	public void setBeginTransfer(String beginTransfer) {
		this.beginTransfer = beginTransfer;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public String getPiId() {
		return piId;
	}

	public void setPiId(String piId) {
		this.piId = piId;
	}

	public String getInternalOrderId() {
		return internalOrderId;
	}

	public void setInternalOrderId(String internalOrderId) {
		this.internalOrderId = internalOrderId;
	}

	public String getWmsWarehouse() {
		return wmsWarehouse;
	}

	public void setWmsWarehouse(String wmsWarehouse) {
		this.wmsWarehouse = wmsWarehouse;
	}

//	public PurchaseOrder getPo() {
//		return po;
//	}
//
//	public void setPo(PurchaseOrder po) {
//		this.po = po;
//	}
//	
//	@Transient
//	public String getUrgency(){
//		if(po != null){
//			return po.getUrgency();
//		}
//		return "";
//	}

}