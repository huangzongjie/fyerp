package com.graly.erp.js.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="V_MATERIAL_QUERY")
public class JSMaterialQtyQuery implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PRODUCT_ID")
	private Long productId;
	
	@Column(name="SERIAL_NUMBER")
	private String serialNumber;
	
	@Column(name="NAME")
	private String name;

	@Column(name="ARRIVE_DATE")
	private Date arriveDate;
	
	@Column(name="L2")
	private BigDecimal l2;//在途
	
	@Column(name="L3")
	private BigDecimal l3;//库存
	
	@Column(name="L4")
	private BigDecimal l4;//工作令需求
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public BigDecimal getL2() {
		return l2;
	}

	public void setL2(BigDecimal l2) {
		this.l2 = l2;
	}

	public BigDecimal getL3() {
		return l3;
	}

	public void setL3(BigDecimal l3) {
		this.l3 = l3;
	}

	public BigDecimal getL4() {
		return l4;
	}

	public void setL4(BigDecimal l4) {
		this.l4 = l4;
	}
	
	
	
	
	
}
