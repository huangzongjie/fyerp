package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.graly.erp.vdm.model.Vendor;

@Entity
@DiscriminatorValue("I")
public class MovementIn extends Movement{
	
	public static String IN_TYPE_AMOUNT_ADJUST = "金额调整";
	
	public enum InType {
		PIN,	//采购 
		WIN,	//生产
		OIN,	//其它
		RIN,	//重新入库
		ADIN	//营运调整
	};
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Column(name="VENDOR_ID")
	private String vendorId;
	
	@Column(name="DATE_WRITE_OFF")
	private Date dateWriteOff;
	
	@Column(name="IN_TYPE")
	private String inType;
	
	@Column(name="IS_REF")
	private String isRef;
	
	@Column(name="KIND")
	private String kind;
	
	@Transient
	private Long locatorRrn;
	
	@Transient
	private BigDecimal length;
	
	@Transient
	private BigDecimal width;
	
	@Transient
	private BigDecimal height;
	
	@Transient
	private BigDecimal volume;
	
	@Transient
	private String weight;
	
	@Transient
	private String materialId;
	
	@Column(name="ACCESS_LINE_TOTAL")
	private BigDecimal accessLineTotal;
	
	@Column(name="INVOICE_LINE_TOTAL")
	private BigDecimal invoiceLineTotal;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="VENDOR_RRN", referencedColumnName="OBJECT_RRN", insertable=false, updatable=false)
	private Vendor vendor;
	

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorId() {
		return vendorId;
	}
	
	public void setDateWriteOff(Date dateWriteOff) {
		this.dateWriteOff = dateWriteOff;
	}

	public Date getDateWriteOff() {
		return dateWriteOff;
	}

	public String getInType() {
		return inType;
	}

	public void setInType(String inType) {
		this.inType = inType;
	}
	
	public Long getLocatorRrn() {
		return locatorRrn;
	}

	public void setLocatorRrn(Long locatorRrn) {
		this.locatorRrn = locatorRrn;
	}

	public Boolean getIsRef() {
		return "Y".equalsIgnoreCase(isRef) ? true : false;
	}

	public void setIsRef(Boolean isRef) {
		this.isRef = isRef ? "Y" : "N";
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public BigDecimal getLength() {
		if(getMo() == null){
			return BigDecimal.ZERO;
		}else if(getMo().getMaterial() == null){
			return BigDecimal.ZERO;
		}
		return getMo().getMaterial().getLength();
	}

	public void setLength(BigDecimal length) {
		this.length = length;
	}

	public BigDecimal getWidth() {
		if(getMo() == null){
			return BigDecimal.ZERO;
		}else if(getMo().getMaterial() == null){
			return BigDecimal.ZERO;
		}
		return getMo().getMaterial().getWidth();
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getHeight() {
		if(getMo() == null){
			return BigDecimal.ZERO;
		}else if(getMo().getMaterial() == null){
			return BigDecimal.ZERO;
		}
		return getMo().getMaterial().getHeight();
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getVolume() {
		if(getMo() == null){
			return BigDecimal.ZERO;
		}else if(getMo().getMaterial() == null){
			return BigDecimal.ZERO;
		}
		return getMo().getMaterial().getVolume();
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getWeight() {
		if(getMo() == null){
			return "";
		}else if(getMo().getMaterial() == null){
			return "";
		}
		return getMo().getMaterial().getWeight();
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getMaterialId() {
		if(getMo() == null){
			return "";
		}else if(getMo().getMaterial() == null){
			return "";
		}
		return getMo().getMaterialId();
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}


	public BigDecimal getInvoiceLineTotal() {
		return invoiceLineTotal;
	}

	public void setInvoiceLineTotal(BigDecimal invoiceLineTotal) {
		this.invoiceLineTotal = invoiceLineTotal;
	}

	public BigDecimal getAccessLineTotal() {
		return accessLineTotal;
	}

	public void setAccessLineTotal(BigDecimal accessLineTotal) {
		this.accessLineTotal = accessLineTotal;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public String getShortVendorName(){
		if(vendor != null){
			return vendor.getShortName();
		}
		return "";
	}
	
	public void setShortVendorName(String vendorName){}
}
