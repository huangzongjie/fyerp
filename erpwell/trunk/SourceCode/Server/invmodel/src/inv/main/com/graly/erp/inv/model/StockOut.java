package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="T_STOCKOUT")
public class StockOut extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name="RECEIPT_TYPE")
	private String receiptType;
	
	@Column(name="RECEIPT_TIME")
	private Date receiptTime;
	
	@Column(name="USER_NAME")
	private String userName;
	
	@Column(name="ERP_WRITE")
	private Long erpWrite;
	
	@Column(name="ERP_WRITE_TIME")
	private Date erpWriteTime;

	@Column(name="WMS_READ")
	private Long wmsRead;
	
	@Column(name="WMS_READ_TIME")
	private Date wmsReadTime;
	
	@Column(name="WMS_WRITE_TIME")
	private Date wmsWriteTime;
	
	@Column(name="MATERIAL_CODE")
	private String materialCode;
	
	@Column(name="BATCH")
	private String batch;
	
	@Column(name="QUANTITY")
	private BigDecimal quality;
	
	@Column(name="SUPPLIERNAME")
	private String supplierName;
	
	@Column(name="NOTE1")
	private String note1;
	
	@Column(name="NOTE2")
	private String note2;
	
	@Column(name="NOTE3")
	private String note3;
 
	@Transient
	private String materialName;
	
	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}

	public Date getReceiptTime() {
		return receiptTime;
	}

	public void setReceiptTime(Date receiptTime) {
		this.receiptTime = receiptTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getErpWrite() {
		return erpWrite;
	}

	public void setErpWrite(Long erpWrite) {
		this.erpWrite = erpWrite;
	}

	public Date getErpWriteTime() {
		return erpWriteTime;
	}

	public void setErpWriteTime(Date erpWriteTime) {
		this.erpWriteTime = erpWriteTime;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	
	public BigDecimal getQuality() {
		return quality;
	}

	public void setQuality(BigDecimal quality) {
		this.quality = quality;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getNote1() {
		return note1;
	}

	public void setNote1(String note1) {
		this.note1 = note1;
	}

	public String getNote2() {
		return note2;
	}

	public void setNote2(String note2) {
		this.note2 = note2;
	}

	public String getNote3() {
		return note3;
	}

	public void setNote3(String note3) {
		this.note3 = note3;
	}

	public Long getWmsRead() {
		return wmsRead;
	}

	public void setWmsRead(Long wmsRead) {
		this.wmsRead = wmsRead;
	}

	public Date getWmsReadTime() {
		return wmsReadTime;
	}

	public void setWmsReadTime(Date wmsReadTime) {
		this.wmsReadTime = wmsReadTime;
	}

	public Date getWmsWriteTime() {
		return wmsWriteTime;
	}

	public void setWmsWriteTime(Date wmsWriteTime) {
		this.wmsWriteTime = wmsWriteTime;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
}
