package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PPM_MPS_LINE_DELIVERY")
public class MpsLineDelivery extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	public static final String DOC_STATUS_APPROVED = "APPROVED";
	public static final String DOC_STATUS_COMPLETED = "COMPLETED";
	
	@Column(name="MPS_ID")
	private String mpsId;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY")
	private BigDecimal qty = BigDecimal.ZERO;
	  
	@Column(name="DATE_DELIVERY")
	private Date dateDelivery;
	
	@Column(name="CUSTOMER")
	private String customer;
	
	@Column(name="ORDER_ID")
	private String orderId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name = "DOC_STATUS")
	private String docStatus = "APPROVED";
	
	@Column(name="QTY_RECEIVED")
	private BigDecimal qtyReceived = BigDecimal.ZERO;//接收数量
	
	
	@Column(name = "LOT_ID")
	private String lotId;
	
	@Column(name = "MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name = "MO_ID")
	private String moId;

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
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

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public Date getDateDelivery() {
		return dateDelivery;
	}

	public void setDateDelivery(Date dateDelivery) {
		this.dateDelivery = dateDelivery;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public BigDecimal getQtyReceived() {
		return qtyReceived;
	}

	public void setQtyReceived(BigDecimal qtyReceived) {
		this.qtyReceived = qtyReceived;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}
	
	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}
}
