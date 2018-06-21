package com.graly.erp.pur.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_VDM_VENDOR_MATERIAL")
public class VVendorMaterial extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;	
	
	@Column(name="NAME")
	private String materialName;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="COMPANY_NAME")
	private String companyName;
	
	@Column(name="IS_PRIMARY")
	private String isPrimary;
	
	@Column(name="FEED_TYPE")
	private String feedType;
	
	@Column(name="REFERENCED_PRICE")
	private BigDecimal referencedPrice;
	
	@Column(name="LEAD_TIME")
	private Long leadTime;
	
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public BigDecimal getReferencedPrice() {
		return referencedPrice;
	}

	public void setReferencedPrice(BigDecimal referencedPrice) {
		this.referencedPrice = referencedPrice;
	}

	public boolean getIsPrimary() {
		return "Y".equalsIgnoreCase(isPrimary) ? true : false;
	}

	public void setIsPrimary(boolean isPrimary) {
		this.isPrimary = (isPrimary ? "Y" : "N");
	}

}
