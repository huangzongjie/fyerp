package com.graly.erp.vdm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "VDM_YEAR_TARGET")
public class VendorYearTarget extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name = "VENDOR_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Vendor vendor;

	@Column(name = "VENDOR_ID")
	private String vendorId;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;

	@Column(name = "PO_YEAR_TARGET")
	private BigDecimal poYearTarget;
	
	@Column(name = "TARGET_YEAR")
	private String targetYear;
	
	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public String getVendorId() {
		if (this.vendor != null) {
			return this.vendor.getVendorId();
		}
		return "";
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	
	public String getCompanyName() {
		if (this.vendor != null) {
			return this.vendor.getCompanyName();
		}
		return "";
	}
	
	public String getShortName() {
		if (this.vendor != null) {
			return this.vendor.getShortName();
		}
		return "";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPoYearTarget() {
		return poYearTarget;
	}

	public void setPoYearTarget(BigDecimal poYearTarget) {
		this.poYearTarget = poYearTarget;
	}

	public String getTargetYear() {
		return targetYear;
	}

	public void setTargetYear(String targetYear) {
		this.targetYear = targetYear;
	}
	
	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
}