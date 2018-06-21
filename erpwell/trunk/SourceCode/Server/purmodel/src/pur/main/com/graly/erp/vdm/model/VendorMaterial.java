package com.graly.erp.vdm.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="VDM_VENDOR_MATERIAL")
public class VendorMaterial extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="FEED_TYPE")
	private String feedType;
	
	@Column(name="ASSESSMENT_MARK")
	private Double assessmentMark;
	
	@Column(name="REFERENCED_PRICE")
	private BigDecimal referencedPrice;
	
	@Column(name="HIGHEST_PRICE")
	private BigDecimal highestPrice;
	
	@Column(name="LAST_PRICE")
	private BigDecimal lastPrice;
	
	@Column(name="LOWEST_PRICE")
	private BigDecimal lowestPrice;
	
	@Column(name="AVERAGE_PRICE")
	private BigDecimal averagePrice;
	
	@Column(name="LEAD_TIME")
	private Long leadTime;
	
	@Column(name="FEED_MATERIAL")
	private String feedMaterial;
	
	@Column(name="LEAST_QUANTITY")
	private BigDecimal leastQuantity;
	
	@Column(name="INCREASE_QUANTITY")
	private BigDecimal increaseQuantity;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="IS_PRIMARY")
	private String isPrimary;
	
	@Column(name="PURCHASER")
	private String purchaser;
	
	@Column(name="ADVANCE_RATIO")
	private BigDecimal advanceRatio;
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	@ManyToOne
	@JoinColumn(name = "VENDOR_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Vendor vendor;
	
	@Column(name="PACKAGE_SPEC")//包装规格
	private String packageSpec;
	
	@Column(name="PRODUCT_NO")//原能产品货号
	private String productNo;
	
	@Column(name="BRAND")//yn pingpai
	private String brand;
	
	public String getPackageSpec() {
		return packageSpec;
	}

	public void setPackageSpec(String packageSpec) {
		this.packageSpec = packageSpec;
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

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public Double getAssessmentMark() {
		return assessmentMark;
	}

	public void setAssessmentMark(Double assessmentMark) {
		this.assessmentMark = assessmentMark;
	}

	public BigDecimal getReferencedPrice() {
		return referencedPrice;
	}

	public void setReferencedPrice(BigDecimal referencedPrice) {
		this.referencedPrice = referencedPrice;
	}

	public BigDecimal getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(BigDecimal highestPrice) {
		this.highestPrice = highestPrice;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public Long getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(Long leadTime) {
		this.leadTime = leadTime;
	}

	public String getFeedMaterial() {
		return feedMaterial;
	}

	public void setFeedMaterial(String feedMaterial) {
		this.feedMaterial = feedMaterial;
	}

	public BigDecimal getLeastQuantity() {
		return leastQuantity;
	}

	public void setLeastQuantity(BigDecimal leastQuantity) {
		this.leastQuantity = leastQuantity;
	}

	public BigDecimal getIncreaseQuantity() {
		return increaseQuantity;
	}

	public void setIncreaseQuantity(BigDecimal increaseQuantity) {
		this.increaseQuantity = increaseQuantity;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getMaterialId() {
		if (this.material != null) {
			return this.material.getMaterialId();
		}
		return "";
	}
	
	public String getMaterialName() {
		if (this.material != null) {
			return this.material.getName();
		}
		return "";
	}
	
	public String getVendorId() {
		if (this.vendor != null) {
			return this.vendor.getVendorId();
		}
		return "";
	}
	
	public String getVendorName() {
		if (this.vendor != null) {
			return this.vendor.getCompanyName();
		}
		return "";
	}

	public Boolean getIsPrimary() {
		return "Y".equalsIgnoreCase(this.isPrimary) ? true : false; 
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary ? "Y" : "N";
	}

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

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public BigDecimal getAdvanceRatio() {
		return advanceRatio;
	}

	public void setAdvanceRatio(BigDecimal advanceRatio) {
		this.advanceRatio = advanceRatio;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setLowestPrice(BigDecimal lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public BigDecimal getLowestPrice() {
		return lowestPrice;
	}

	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	
}
