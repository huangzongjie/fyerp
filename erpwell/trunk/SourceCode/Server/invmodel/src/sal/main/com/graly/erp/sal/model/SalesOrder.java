package com.graly.erp.sal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CANA_ORDER")
public class SalesOrder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="ORDERID")
	private String orderId;
	
	@Column(name="SERIAL_NUMBER")
	private String serialNumber;

	@Column(name="CUSTOMID")
	private String customerId;

	@Column(name="NAME")
	private String customerName;

	@Column(name="SELLER")
	private String seller;
	
	@Column(name="CUSTOM_KIND")
	private String customerKind;
	
	@Column(name="LINKMAN")
	private String linkman;
	
	@Column(name="LINKMAN_GENRE")
	private String linkmanType;
	
	@Column(name="LINKMAN_JOBTEL")
	private String linkmanTel;

	@Column(name="LINKMAN_MOBILE")
	private String linkmanMobile;

	@Column(name="LINKMAN_DEPARTMENT")
	private String linkmanDepartment;

	@Column(name="LINKMAN_JOB")
	private String linkmanJob;
	
	@Column(name="LINKMAN_KIND")
	private String linkmanKind;
	
	@Column(name="PAYMENT")
	private String payment;
	
	@Column(name="KIND")
	private String kind;
	
	@Column(name="OUTSTORAGE_SERIAL_NUMBER")
	private String outSerialNumber;
	
	@Column(name="STORAGE")
	private String storage;
	
	@Column(name="DELIVER_ADDRESS")
	private String deliverAddress;
	
	@Column(name="DELIVERDATE")
	private String deliverDate;
	
	@Column(name="NOTICESTORAGE")
	private String noticeStorage;
	
	@Column(name="PRICE_POLICY")
	private String pricePolicy;
	
	@Column(name="AUDITING")
	private String auditing;

	@Column(name="CEO_AUDITING")
	private String ceoAuditing;
	
	@Column(name="PRO_NAME")
	private String productName;

	@Column(name="PRO_BAR")
	private String productBar;
	
	@Column(name="PRO_SERIAL_NUMBER")
	private String productSerialNumber;
	
	@Column(name="PRO_UNIT")
	private String productUnit;
	
	@Column(name="PRO_NUM")
	private String productNumber;
	
	@Column(name="PRO_PRICE")
	private String productPrice;
	
	@Column(name="PRO_PRICE_FLOAT")
	private String productPriceFloat;
	
	@Column(name="PRO_TOTAL_PRICES")
	private String productTotalPrices;
	
	@Column(name="REMARK")
	private String remark;
	
	@Column(name="DISPOSAL")
	private String disposal;
	
	@Column(name="SELF_FIELD1")
	private String selfField1;
	
	@Column(name="SELF_FIELD2")
	private String selfField2;
	
	@Column(name="SELF_FIELD3")
	private String selfField3;
	
	@Column(name="SELF_FIELD4")
	private String selfField4;
	
	@Column(name="SELF_FIELD5")
	private String selfField5;
	
	@Column(name="SELF_FIELD6")
	private String selfField6;
	
	@Column(name="SELF_FIELD7")
	private String selfField7;
	
	@Column(name="SELF_FIELD8")
	private String selfField8;
	
	@Column(name="SELF_FIELD21")
	private String selfField21;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="RECORDER")
	private String recorder;
	
	@Column(name="RECORDER_TIME")
	private String recorderTime;
	
	@Column(name="UPDATER")
	private String updater;
	
	@Column(name="UPDATE_TIME")
	private String updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getCustomerKind() {
		return customerKind;
	}

	public void setCustomerKind(String customerKind) {
		this.customerKind = customerKind;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getLinkmanType() {
		return linkmanType;
	}

	public void setLinkmanType(String linkmanType) {
		this.linkmanType = linkmanType;
	}

	public String getLinkmanTel() {
		return linkmanTel;
	}

	public void setLinkmanTel(String linkmanTel) {
		this.linkmanTel = linkmanTel;
	}

	public String getLinkmanMobile() {
		return linkmanMobile;
	}

	public void setLinkmanMobile(String linkmanMobile) {
		this.linkmanMobile = linkmanMobile;
	}

	public String getLinkmanDepartment() {
		return linkmanDepartment;
	}

	public void setLinkmanDepartment(String linkmanDepartment) {
		this.linkmanDepartment = linkmanDepartment;
	}

	public String getLinkmanJob() {
		return linkmanJob;
	}

	public void setLinkmanJob(String linkmanJob) {
		this.linkmanJob = linkmanJob;
	}

	public String getLinkmanKind() {
		return linkmanKind;
	}

	public void setLinkmanKind(String linkmanKind) {
		this.linkmanKind = linkmanKind;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getOutSerialNumber() {
		return outSerialNumber;
	}

	public void setOutSerialNumber(String outSerialNumber) {
		this.outSerialNumber = outSerialNumber;
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getDeliverAddress() {
		return deliverAddress;
	}

	public void setDeliverAddress(String deliverAddress) {
		this.deliverAddress = deliverAddress;
	}

	public String getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(String deliverDate) {
		this.deliverDate = deliverDate;
	}

	public String getNoticeStorage() {
		return noticeStorage;
	}

	public void setNoticeStorage(String noticeStorage) {
		this.noticeStorage = noticeStorage;
	}

	public String getPricePolicy() {
		return pricePolicy;
	}

	public void setPricePolicy(String pricePolicy) {
		this.pricePolicy = pricePolicy;
	}

	public String getAuditing() {
		return auditing;
	}

	public void setAuditing(String auditing) {
		this.auditing = auditing;
	}

	public String getCeoAuditing() {
		return ceoAuditing;
	}

	public void setCeoAuditing(String ceoAuditing) {
		this.ceoAuditing = ceoAuditing;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductBar() {
		return productBar;
	}

	public void setProductBar(String productBar) {
		this.productBar = productBar;
	}

	public String getProductSerialNumber() {
		return productSerialNumber;
	}

	public void setProductSerialNumber(String productSerialNumber) {
		this.productSerialNumber = productSerialNumber;
	}

	public String getProductUnit() {
		return productUnit;
	}

	public void setProductUnit(String productUnit) {
		this.productUnit = productUnit;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductPriceFloat() {
		return productPriceFloat;
	}

	public void setProductPriceFloat(String productPriceFloat) {
		this.productPriceFloat = productPriceFloat;
	}

	public String getProductTotalPrices() {
		return productTotalPrices;
	}

	public void setProductTotalPrices(String productTotalPrices) {
		this.productTotalPrices = productTotalPrices;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDisposal() {
		return disposal;
	}

	public void setDisposal(String disposal) {
		this.disposal = disposal;
	}

	public String getSelfField1() {
		return selfField1;
	}

	public void setSelfField1(String selfField1) {
		this.selfField1 = selfField1;
	}

	public String getSelfField2() {
		return selfField2;
	}

	public void setSelfField2(String selfField2) {
		this.selfField2 = selfField2;
	}

	public String getSelfField3() {
		return selfField3;
	}

	public void setSelfField3(String selfField3) {
		this.selfField3 = selfField3;
	}

	public String getSelfField4() {
		return selfField4;
	}

	public void setSelfField4(String selfField4) {
		this.selfField4 = selfField4;
	}

	public String getSelfField5() {
		return selfField5;
	}

	public void setSelfField5(String selfField5) {
		this.selfField5 = selfField5;
	}

	public String getSelfField6() {
		return selfField6;
	}

	public void setSelfField6(String selfField6) {
		this.selfField6 = selfField6;
	}

	public String getSelfField7() {
		return selfField7;
	}

	public void setSelfField7(String selfField7) {
		this.selfField7 = selfField7;
	}

	public String getSelfField8() {
		return selfField8;
	}

	public void setSelfField8(String selfField8) {
		this.selfField8 = selfField8;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRecorder() {
		return recorder;
	}

	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}

	public String getRecorderTime() {
		return recorderTime;
	}

	public void setRecorderTime(String recorderTime) {
		this.recorderTime = recorderTime;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getSelfField21() {
		return selfField21;
	}

	public void setSelfField21(String selfField21) {
		this.selfField21 = selfField21;
	}

}
