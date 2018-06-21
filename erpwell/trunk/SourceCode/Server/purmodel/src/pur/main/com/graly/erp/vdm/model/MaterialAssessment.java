package com.graly.erp.vdm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "VDM_MATERIAL_ASSESSMENT")
public class MaterialAssessment extends ADUpdatable {

	/**
	 * @author Jerry Wan
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "VENDOR_RRN")
	private Long vendorRrn;

	@Column(name = "MATERIAL_RRN")
	private Long materialRrn;

	@Column(name = "VENDOR_ID")
	private String vendorId;
	
	@Column(name = "MATERIAL_ASSESSMENT")
	private String materialAssessment;
	
	@Column(name = "MATERIAL_ID")
	private String materialId;
	
	@Column(name = "ASSESSMENT_USER_ID")
	private String assessmentUserId;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	@ManyToOne
	@JoinColumn(name = "VENDOR_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Vendor vendor;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialAssessment() {
		return materialAssessment;
	}

	public void setMaterialAssessment(String materialAssessment) {
		this.materialAssessment = materialAssessment;
	}

	public String getAssessmentUserId() {
		return assessmentUserId;
	}

	public void setAssessmentUserId(String assessmentUserId) {
		this.assessmentUserId = assessmentUserId;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	
	public String getMaterialName() {
		if (this.material != null) {
			return this.material.getName();
		}
		return "";
	}
	
	public String getCompanyName() {
		if (this.vendor != null) {
			return this.vendor.getCompanyName();
		}
		return "";
	}
}
