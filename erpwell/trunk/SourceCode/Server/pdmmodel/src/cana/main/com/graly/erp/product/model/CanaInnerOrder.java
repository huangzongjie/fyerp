package com.graly.erp.product.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CANA_INNER_ORDER")
public class CanaInnerOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="SERIAL_NUMBER")
	private String serialNumber;//
	
	@Column(name="MATERIAL_ID")
	private String materialId;//
	
	@Column(name="MATERIAL_NAME")
	private String materialName;//
	
	@Column(name="QTY")
	private BigDecimal qty;//
	
//	@Column(name="KIND")
//	private String kind;//销售类型
 
//	
//	@Column(name="SELLER")
//	private String seller;//业务员
 
//	
	@Column(name="PI_NO")
	private String piNo;//
	
	@Column(name="CUSTOM_ID")
	private String customId;//
	
	@Column(name="CUSTOM_NAME")
	private String customName;//
	
	@Column(name="SELF_FIELD1")
	private String selfField1;//交货期
	
	@Column(name="SELF_FIELD12")
	private String selfField12;//同意or不同意
	
	@Column(name="SELF_FIELD23")
	private String selfField23;//订单编号HW1410**
	
	@Column(name="SELF_FIELD30")
	private String selfField30;//客户经理
	
	@Column(name="SELF_FIELD29")
	private String selfField29;//ERP设置是否操作该P
	
	@Column(name="STATUS")
	private String status;//状态
	
	@Column(name="SELF_FIELD2")
	private String selfField2;
	
	@Column(name="SELLER_NAME")
	private String sellerName;
	
	@Column(name="SELLER")
	private String seller;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public String getPiNo() {
		return piNo;
	}

	public void setPiNo(String piNo) {
		this.piNo = piNo;
	}

	public String getSelfField30() {
		return selfField30;
	}

	public void setSelfField30(String selfField30) {
		this.selfField30 = selfField30;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSelfField1() {
		return selfField1;
	}

	public void setSelfField1(String selfField1) {
		this.selfField1 = selfField1;
	}

	public String getSelfField12() {
		return selfField12;
	}

	public void setSelfField12(String selfField12) {
		this.selfField12 = selfField12;
	}

	public String getSelfField23() {
		return selfField23;
	}

	public void setSelfField23(String selfField23) {
		this.selfField23 = selfField23;
	}

	public String getSelfField2() {
		return selfField2;
	}

	public void setSelfField2(String selfField2) {
		this.selfField2 = selfField2;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getSelfField29() {
		return selfField29;
	}

	public void setSelfField29(String selfField29) {
		this.selfField29 = selfField29;
	}
}