package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="PPM_INTERNAL_ORDER")
public class InternalOrder extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_DRAFTED = "DRAFTED";
	public static final String STATUS_COMPLETED = "COMPLETED";
	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_CLOSED = "CLOSED";
 
	
	public static final String DOC_TYPE_PPM = "PPM";
	public static final String DOC_TYPE_PO = "PO";
	
	
	@Column(name="DOC_ID")
	private String docId;
	
	@Column(name="PI_NO")
	private String piNo;

	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name = "DOC_STATUS")
	private String docStatus = STATUS_DRAFTED;
	
	@Column(name="ORDER_ID")
	private String orderId;//开能内部订货单
	
	@Column(name = "DATE_DELIVERED")
	private Date dateDelivered;//交货期交货期将交货期放置LINE上面
	
	@Column(name="DOC_TYPE")
	private String docType;
	
	
	@Column(name="CUSTOM_ID")
	private String customId;
	
	@Column(name="CUSTOM_NAME")
	private String customName;
	
	@Column(name="SELF_FIELD2")
	private String selfField2;//
	
	@Column(name="SELLER_NAME")
	private String sellerName;
	
	@Column(name="SELLER")
	private String seller;//业务员
	
	public String getDocId() {
		return docId;
	}
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@JoinColumn(name = "IO_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private List<InternalOrderLine> ioLines;
	
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getDateDelivered() {
		return dateDelivered;
	}

	public void setDateDelivered(Date dateDelivered) {
		this.dateDelivered = dateDelivered;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPiNo() {
		return piNo;
	}

	public void setPiNo(String piNo) {
		this.piNo = piNo;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public List<InternalOrderLine> getIoLines() {
		return ioLines;
	}

	public void setIoLines(List<InternalOrderLine> ioLines) {
		this.ioLines = ioLines;
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
}
