package com.graly.erp.inv.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_OOU_PRICE_SUM")
public class VOouPriceSum extends ADBase {
	private static final long serialVersionUID = 1L;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;

	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="TOTAL_QTY")
	private BigDecimal totalQty;
	
	@Column(name="TOTLE_PRICE")
	private BigDecimal totalPrice;

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(BigDecimal totalQty) {
		this.totalQty = totalQty;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Date getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}
}
