package com.graly.erp.inv.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Documentation;

@Entity
@Table(name="INV_IQC")
public class Iqc  extends Documentation{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="RECEIPT_RRN")
	private Long receiptRrn;
	
//	@OneToOne
//	@JoinColumn(name="RECEIPT_RRN", referencedColumnName="OBJECT_RRN", insertable=false, updatable=false)
//	private Receipt receipt;
	
	@Column(name="RECIPT_ID")
	private String receiptId;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="PO_RRN")
	private Long poRrn;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="IS_IN")
	private String isIn = "N";
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	@JoinColumn(name = "IQC_RRN", referencedColumnName = "OBJECT_RRN" ,insertable = false, updatable = false)
	private List<IqcLine> iqcLines;

	@Column(name="PO_COMMENTS")
	private String poComments;//仓库贠向刚希望质检单看到采购订单备注
	
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

	public Long getReceiptRrn() {
		return receiptRrn;
	}

	public void setReceiptRrn(Long receiptRrn) {
		this.receiptRrn = receiptRrn;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
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

	public Boolean getIsIn(){
		return "Y".equalsIgnoreCase(this.isIn) ? true : false; 
	}

	public void setIsIn(Boolean isIn) {
		this.isIn = isIn ? "Y" : "N";
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
	
	public void setIqcLines(List<IqcLine> iqcLines) {
		this.iqcLines = iqcLines;
	}

	public List<IqcLine> getIqcLines() {
		return iqcLines;
	}

	public String getPoComments() {
		return poComments;
	}

	public void setPoComments(String poComments) {
		this.poComments = poComments;
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

//	public Receipt getReceipt() {
//		return receipt;
//	}
//
//	public void setReceipt(Receipt receipt) {
//		this.receipt = receipt;
//	}
//
//	@Transient
//	public String getUrgency(){
//		if(receipt != null){
//			return receipt.getUrgency();
//		}
//		return "";
//	}
	
	public String getWmsWarehouse() {
		return wmsWarehouse;
	}

	public void setWmsWarehouse(String wmsWarehouse) {
		this.wmsWarehouse = wmsWarehouse;
	}
	
}
