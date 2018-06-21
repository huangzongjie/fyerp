package com.graly.erp.vdm.model;

import java.math.BigDecimal;
import java.util.Date;

public class VendorAssessment implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long vendorRrn;
	private String verndorId;
	private String vendorName;
	private Long materialRrn;
	private String materialId;
	private String materialName;
	private BigDecimal passPercent ;
	private BigDecimal inTimePercent;
	private BigDecimal qtyIn;
	private Date dateStart;
	private Date dateEnd;
	private String purchaser;
	
	public Long getVendorRrn() {
		return vendorRrn;
	}
	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}
	public String getVerndorId() {
		return verndorId;
	}
	public void setVerndorId(String verndorId) {
		this.verndorId = verndorId;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
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
	public BigDecimal getPassPercent() {
		return passPercent;
	}
	public void setPassPercent(BigDecimal passPercent) {
		this.passPercent = passPercent;
	}
	public BigDecimal getInTimePercent() {
		return inTimePercent;
	}
	public void setInTimePercent(BigDecimal inTimePercent) {
		this.inTimePercent = inTimePercent;
	}
	public BigDecimal getQtyIn() {
		return qtyIn;
	}
	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}
	public Date getDateStart() {
		return dateStart;
	}
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}
	public Date getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	public String getPurchaser() {
		return purchaser;
	}
	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}
}
