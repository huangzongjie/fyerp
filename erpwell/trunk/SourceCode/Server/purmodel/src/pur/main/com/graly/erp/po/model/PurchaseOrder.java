package com.graly.erp.po.model;

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
import com.graly.erp.vdm.model.Vendor;

@Entity
@Table(name="PUR_PO")
public class PurchaseOrder extends Documentation {
	private static final long serialVersionUID = 1L;
	
	public static final String INVOICE_TYPE_VAT = "VAT";
	public static final String INVOICE_TYPE_REGULAR = "REGULAR";
	
	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="PRIORITY")
	private String priority;
	
	@Column(name="VENDOR_RRN")
	private Long vendorRrn;
	
	@Transient
	private String vendorId;
	
	@Transient
	private String vendorName;
	
	@ManyToOne
	@JoinColumn(name = "VENDOR_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Vendor vendor;
	
	@Column(name="TOTAL")
	private BigDecimal total = BigDecimal.ZERO;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="PURCHASER")
	private String purchaser;
	
	@Column(name="DELIVERY_RULE")
	private String deliveryRule;
	
	@Column(name="DELIVERY_ADDRESS")
	private String deliveryAddress;
	
	@Column(name="PAYMENT_RULE1")
	private String paymentRule1;    //Check

	@Column(name="PAYMENT_RULE2")
	private String paymentRule2;    //Cash
	
	@Column(name="PAYMENT_RULE3")
	private String paymentRule3;    //Remittance
	
	@Column(name="PAYMENT_RULE4")
	private String paymentRule4;    //Imprest
	
	@Column(name="PAYMENT_RULE5")
	private String paymentRule5;    //Monthly Statement 

	@Column(name="PAYMENT_RULE6")
	private String paymentRule6;    //After Check
	
	@Column(name="PAYMENT_RULE7")
	private String paymentRule7;    //DefrayByDay
	
	@Column(name="PAYMENT_RULE8")
	private String paymentRule8;    //QA Cash

	@Column(name="PAYMENT_RULE9")
	private String paymentRule9;    //%

	@Column(name="PAYMENT_RULE10")
	private String paymentRule10;   //DefrayAfterDay
	
	@Column(name="PAYMENT_RULE11")
	private String paymentRule11 = "Y";   //是否开具发票

	@Column(name="PAYMENT_RULE12")
	private String paymentRule12;   //Plain Invoice
	
	@Column(name="PAYMENT_RULE13")
	private String paymentRule13;
	
	@Column(name="PAYMENT_RULE14")
	private String paymentRule14;
	
	@Column(name="PAYMENT_RULE15")
	private String paymentRule15;
	
	@Column(name="PAYMENT_RULE16")
	private String paymentRule16;
	
	@Column(name="PAYMENT_RULE17")
	private String paymentRule17;
	
	@Column(name="QUALITY_RULE1")
	private String qualityRule1;    //Industry Standard
	
	@Column(name="QUALITY_RULE2")
	private String qualityRule2;    //Confirmed Technical File,Contract
	
	@Column(name="QUALITY_RULE3")
	private String qualityRule3;    //Sealed Sample
	
	@Column(name="QUALITY_RULE4")
	private String qualityRule4;    //By Customer Demand
	
	@Column(name="QUALITY_RULE5")
	private String qualityRule5;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="REQUISITION_RRN")
	private Long requisitionRrn;

	@Column(name="REQUISITION_ID")
	private String requisitionId;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	@JoinColumn(name = "PO_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private List<PurchaseOrderLine> poLines;
	
	@Column(name="INVOICE_TYPE")
	private String invoiceType;
	
	@Column(name="VAT_RATE")
	BigDecimal vatRate = new BigDecimal("0.17");//默认值是0.17
	
	@Column(name="IS_PREAPPROVED")
	private String isPreApproved = "N";//默认是false
	
	@Column(name="DATE_PREAPPROVED")
	private Date datePreApproved;
	
	@Column(name="PREAPPROVED")
	private String preApproved;
	
	@Column(name="FINANCIAL_NOTE")//财务备注
	private String financialNote;
	
	@Column(name="URGENCY")//紧急度
	private String urgency;
	
	@Column(name="PRINT_TIME")
	private Long printTime;//打印次数
	
	@Column(name="FORMER_VENDOR_NAME")
	private String formerVendorName;
	
	@Column(name="ISPAYMENT_FULL")
	private String ispaymentFull;
	
	@Column(name = "PI_ID")
	private String piId;//pi编号
	
	@Column(name = "INTERNAL_ORDER_ID")
	private String internalOrderId;//内部订单编号
	
	@Column(name = "PO_DEPARTMENT")
	private String poDepartment;//祁椅需求添加所属部门

	public String getFormerVendorName() {
		return ((formerVendorName!=null && formerVendorName.trim().length()==0) ? formerVendorName : (vendor != null ? vendor.getCompanyName() : ""));
	}

	public void setFormerVendorName(String formerVendorName) {
		this.formerVendorName = formerVendorName;
	}

	public Long getPrintTime() {
		return printTime;
	}

	public void setPrintTime(Long printTime) {
		this.printTime = printTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Long getVendorRrn() {
		return vendorRrn;
	}

	public void setVendorRrn(Long vendorRrn) {
		this.vendorRrn = vendorRrn;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
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

	public String getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public String getDeliveryRule() {
		return deliveryRule;
	}

	public void setDeliveryRule(String deliveryRule) {
		this.deliveryRule = deliveryRule;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getPaymentRule1() {
		return paymentRule1;
	}

	public void setPaymentRule1(String paymentRule1) {
		this.paymentRule1 = paymentRule1;
	}

	public String getPaymentRule2() {
		return paymentRule2;
	}

	public void setPaymentRule2(String paymentRule2) {
		this.paymentRule2 = paymentRule2;
	}

	public String getPaymentRule3() {
		return paymentRule3;
	}

	public void setPaymentRule3(String paymentRule3) {
		this.paymentRule3 = paymentRule3;
	}

	public String getPaymentRule4() {
		return paymentRule4;
	}

	public void setPaymentRule4(String paymentRule4) {
		this.paymentRule4 = paymentRule4;
	}

	public String getPaymentRule5() {
		return paymentRule5;
	}

	public void setPaymentRule5(String paymentRule5) {
		this.paymentRule5 = paymentRule5;
	}

	public String getPaymentRule6() {
		return paymentRule6;
	}

	public void setPaymentRule6(String paymentRule6) {
		this.paymentRule6 = paymentRule6;
	}

	public String getPaymentRule7() {
		return paymentRule7;
	}

	public void setPaymentRule7(String paymentRule7) {
		this.paymentRule7 = paymentRule7;
	}

	public String getPaymentRule8() {
		return paymentRule8;
	}

	public void setPaymentRule8(String paymentRule8) {
		this.paymentRule8 = paymentRule8;
	}

	public String getPaymentRule9() {
		return paymentRule9;
	}

	public void setPaymentRule9(String paymentRule9) {
		this.paymentRule9 = paymentRule9;
	}

	public String getPaymentRule10() {
		return paymentRule10;
	}

	public void setPaymentRule10(String paymentRule10) {
		this.paymentRule10 = paymentRule10;
	}

	public String getPaymentRule11() {
		return paymentRule11;
	}

	public void setPaymentRule11(String paymentRule11) {
		this.paymentRule11 = paymentRule11;
	}

	public String getPaymentRule12() {
		return paymentRule12;
	}

	public void setPaymentRule12(String paymentRule12) {
		this.paymentRule12 = paymentRule12;
	}

	public String getPaymentRule13() {
		return paymentRule13;
	}

	public void setPaymentRule13(String paymentRule13) {
		this.paymentRule13 = paymentRule13;
	}

	public String getPaymentRule14() {
		return paymentRule14;
	}

	public void setPaymentRule14(String paymentRule14) {
		this.paymentRule14 = paymentRule14;
	}

	public String getPaymentRule15() {
		return paymentRule15;
	}

	public void setPaymentRule15(String paymentRule15) {
		this.paymentRule15 = paymentRule15;
	}

	public String getQualityRule1() {
		return qualityRule1;
	}

	public void setQualityRule1(String qualityRule1) {
		this.qualityRule1 = qualityRule1;
	}

	public String getQualityRule2() {
		return qualityRule2;
	}

	public void setQualityRule2(String qualityRule2) {
		this.qualityRule2 = qualityRule2;
	}

	public String getQualityRule3() {
		return qualityRule3;
	}

	public void setQualityRule3(String qualityRule3) {
		this.qualityRule3 = qualityRule3;
	}

	public String getQualityRule4() {
		return qualityRule4;
	}

	public void setQualityRule4(String qualityRule4) {
		this.qualityRule4 = qualityRule4;
	}

	public String getQualityRule5() {
		return qualityRule5;
	}

	public void setQualityRule5(String qualityRule5) {
		this.qualityRule5 = qualityRule5;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getRequisitionRrn() {
		return requisitionRrn;
	}

	public void setRequisitionRrn(Long requisitionRrn) {
		this.requisitionRrn = requisitionRrn;
	}

	public void setRequisitionId(String requisitionId) {
		this.requisitionId = requisitionId;
	}

	public String getRequisitionId() {
		return requisitionId;
	}
	
	public List<PurchaseOrderLine> getPoLines() {
		return poLines;
	}

	public void setPoLines(List<PurchaseOrderLine> poLines) {
		this.poLines = poLines;
	}

	public String getVendorId() {
		if(this.vendor != null) {
			return vendor.getVendorId();
		}
		return "";
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		if(this.vendor != null) {
			return vendor.getCompanyName();
		}
		return "";
	}

	public void setVendorName(String vendorName) {
		this.formerVendorName = vendorName;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}
	
	public Boolean getIsPreApproved() {
		return "Y".equalsIgnoreCase(isPreApproved) ? true : false;
	}

	public void setIsPreApproved(Boolean isPreApproved) {
		this.isPreApproved = isPreApproved ? "Y" : "N";
	}

	public String getPreApproved() {
		return preApproved;
	}

	public void setPreApproved(String preApproved) {
		this.preApproved = preApproved;
	}

	public Date getDatePreApproved() {
		return datePreApproved;
	}

	public void setDatePreApproved(Date datePreApproved) {
		this.datePreApproved = datePreApproved;
	}

	public String getFinancialNote() {
		return financialNote;
	}

	public void setFinancialNote(String financialNote) {
		this.financialNote = financialNote;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public void setIspaymentFull(String ispaymentFull) {
		this.ispaymentFull = ispaymentFull;
	}

	public boolean getIspaymentFull() {
		return "Y".equals(ispaymentFull)?true:false;
	}

	public void setIspaymentFull(boolean ispaymentFull) {
		this.ispaymentFull = (ispaymentFull?"Y":"N");
	}

	public String getPaymentRule16() {
		return paymentRule16;
	}

	public void setPaymentRule16(String paymentRule16) {
		this.paymentRule16 = paymentRule16;
	}

	public String getPaymentRule17() {
		return paymentRule17;
	}

	public void setPaymentRule17(String paymentRule17) {
		this.paymentRule17 = paymentRule17;
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

	public String getPoDepartment() {
		return poDepartment;
	}

	public void setPoDepartment(String poDepartment) {
		this.poDepartment = poDepartment;
	}
}
