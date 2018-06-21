package com.graly.erp.pdm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;
 
@Entity
@Table(name="T_SUPPLIER")
public class WmsVendor extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="SUPPLIER_CODE")
	private String supplierCode;
	
	@Column(name="SUPPLIER_NAME")
	private String supplierName;
	
  
	@Column(name="ERP_WRITE")
	private BigDecimal erpWrite;
	
	@Column(name="ERP_WRITE_TIME")
	private Date erpWriteTime;
	
	@Column(name="WMS_READ")
	private BigDecimal wmsRead;
	
	@Column(name="WMS_READ_TIME")
	private Date wmsReadTime;
	
	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public BigDecimal getErpWrite() {
		return erpWrite;
	}

	public void setErpWrite(BigDecimal erpWrite) {
		this.erpWrite = erpWrite;
	}
 
	public Date getErpWriteTime() {
		return erpWriteTime;
	}

	public void setErpWriteTime(Date erpWriteTime) {
		this.erpWriteTime = erpWriteTime;
	}

	public BigDecimal getWmsRead() {
		return wmsRead;
	}

	public void setWmsRead(BigDecimal wmsRead) {
		this.wmsRead = wmsRead;
	}

	public Date getWmsReadTime() {
		return wmsReadTime;
	}

	public void setWmsReadTime(Date wmsReadTime) {
		this.wmsReadTime = wmsReadTime;
	}
}	


	