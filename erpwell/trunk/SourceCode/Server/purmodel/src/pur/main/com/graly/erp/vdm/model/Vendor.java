package com.graly.erp.vdm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "VDM_VENDOR")
public class Vendor extends ADUpdatable {
	private static final long serialVersionUID = 1L;

	@Column(name = "VENDOR_ID")
	private String vendorId;

	@Column(name = "VENDOR_TYPE")
	private String vendorType;
	
	@Column(name = "SHIPMENT_CODE")
	private String shipmentCode;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "COMPANY_NAME")
	private String companyName;
	
	@Column(name = "COMPANY_NAME_EN")
	private String companyNameEn;
	
	public String getCompanyNameEn() {
		return companyNameEn;
	}

	public void setCompanyNameEn(String companyNameEn) {
		this.companyNameEn = companyNameEn;
	}

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CONTACT")
	private String contact;

	@Column(name = "PHONE1")
	private String phone1;

	@Column(name = "PHONE2")
	private String phone2;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "URL")
	private String url;

	@Column(name = "FAX")
	private String fax;

	@Column(name = "ZIP_CODE")
	private String zipCode;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "AREA")
	private String area;

	@Column(name = "TERMS_CODE")
	private String termsCode;
	
	@Column(name = "CONTRACT_LIFE")
	private String contractLife;
	
	@Column(name = "BANK_NAME")
	private String bankName;

	@Column(name = "ACCOUNT_ID")
	private String accountId;

	@Column(name = "COMMENTS")
	private String comments;

	@Column(name = "CONTRACT_DOC")
	private String contractDoc;

	@Column(name = "CONTRACT_START")
	private Date contractStart;

	@Column(name = "CONTRACT_END")
	private Date contractEnd;
	
	@Column(name = "IS_ISSUE_INVOICE")
	private String isIssueInvoice;

	@Column(name = "INVOICE_TYPE")
	private String invoiceType;

	@Column(name = "VAT_RATE")
	private BigDecimal vatRate;
	
	@Column(name="SHORT_NAME")
	private String shortName;//简称
	
	//记录来自po的信息供新建po时自动带出上一次po的信息
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
	private String paymentRule11 = "Y"; //是否开具发票

	@Column(name="PAYMENT_RULE12")
	private String paymentRule12;   //Plain Invoice
	
	@Column(name="PAYMENT_RULE13")
	private String paymentRule13;
	
	@Column(name="PAYMENT_RULE14")
	private String paymentRule14;
	
	@Column(name="PAYMENT_RULE15")
	private String paymentRule15;
	
	@Column(name = "IS_ISSUE_INVOICE2")
	private String isIssueInvoice2;

	@Column(name = "INVOICE_TYPE2")
	private String invoiceType2;

	@Column(name = "VAT_RATE2")
	private BigDecimal vatRate2;
	
	@Column(name="PAYMENT_RULE16")
	private String paymentRule16;
	
	@Column(name="PAYMENT_RULE17")
	private String paymentRule17;
	
	@Column(name="EMAIL")
	private String email;

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorType() {
		return vendorType;
	}

	public void setVendorType(String vendorType) {
		this.vendorType = vendorType;
	}

	public String getShipmentCode() {
		return shipmentCode;
	}

	public void setShipmentCode(String shipmentCode) {
		this.shipmentCode = shipmentCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTermsCode() {
		return termsCode;
	}

	public void setTermsCode(String termsCode) {
		this.termsCode = termsCode;
	}

	public String getContractLife() {
		return contractLife;
	}

	public void setContractLife(String contractLife) {
		this.contractLife = contractLife;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getContractDoc() {
		return contractDoc;
	}

	public void setContractDoc(String contractDoc) {
		this.contractDoc = contractDoc;
	}

	public Date getContractStart() {
		return contractStart;
	}

	public void setContractStart(Date contractStart) {
		this.contractStart = contractStart;
	}

	public Date getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(Date contractEnd) {
		this.contractEnd = contractEnd;
	}

	public Boolean getIsIssueInvoice() {
		return "Y".equalsIgnoreCase(isIssueInvoice) ? true : false;
	}

	public void setIsIssueInvoice(Boolean isIssueInvoice) {
		this.isIssueInvoice = isIssueInvoice ? "Y" : "N";
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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

	public String getIsIssueInvoice2() {
		return isIssueInvoice2;
	}

	public void setIsIssueInvoice2(String isIssueInvoice2) {
		this.isIssueInvoice2 = isIssueInvoice2;
	}

	public String getInvoiceType2() {
		return invoiceType2;
	}

	public void setInvoiceType2(String invoiceType2) {
		this.invoiceType2 = invoiceType2;
	}

	public BigDecimal getVatRate2() {
		return vatRate2;
	}

	public void setVatRate2(BigDecimal vatRate2) {
		this.vatRate2 = vatRate2;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
