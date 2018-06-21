package com.graly.erp.ppm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.framework.activeentity.model.ADUpdatable;
@Entity
@Table(name="PPM_INTERNAL_ORDER_LINE")
public class InternalOrderLine extends ADUpdatable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String LINESTATUS_DRAFTED = "DRAFTED";
	public static final String LINESTATUS_COMPLETED = "COMPLETED";
	public static final String LINESTATUS_APPROVED = "APPROVED";
	public static final String LINESTATUS_CLOSED = "CLOSED";
	
	public static final String LINE_TYPE_PO= "采购";
	public static final String LINE_TYPE_PPM= "计划";
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
//	@ManyToOne
//	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
//	private Material material;

	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY")
	private BigDecimal qty = new BigDecimal("0.0");
	
	@Column(name="IO_RRN")
	private Long ioRrn;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="IO_RRN",insertable=false,updatable=false)
	private InternalOrder io;

	@Column(name="IO_ID")
	private String ioId;

	@Column(name="CUSTOMER_ID")
	private String customerId;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;
	
	
	@Column(name="LINE_TYPE")
	private String lineType;
	
	
	@Column(name="MATERIAL_ID")
	private String materialId;

	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="IS_PURCHASE")
	private String isPurchase;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="QTY_ORDER")
	private BigDecimal qtyOrder = BigDecimal.ZERO;
	
	@Column(name="QTY_WIP")
	private BigDecimal qtyWip = BigDecimal.ZERO;
	
	@Column(name="QTY_ONHAND")
	private BigDecimal qtyOnhand = BigDecimal.ZERO;
	
	@Column(name="DATE_DELIVERY")
	private Date dateDelivery;//交货期
	
	@Column(name="PO_ID")
	private String poId;//采购订单编号
	
	@Column(name="QTY_MIN")
	private BigDecimal qtyMin;//安全库存
	
	@Column(name="CUSTOMER_MANAGER")//客户经理
	private String customerManager;
	
	public Long getLineNo() {
		return lineNo;
	}

	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

//	public Material getMaterial() {
//		return material;
//	}
//
//	public void setMaterial(Material material) {
//		this.material = material;
//	}

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

	public String getIoId() {
		return ioId;
	}

	public void setIoId(String ioId) {
		this.ioId = ioId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Long getIoRrn() {
		return ioRrn;
	}

	public void setIoRrn(Long ioRrn) {
		this.ioRrn = ioRrn;
	}

	public InternalOrder getIo() {
		return io;
	}

	public void setIo(InternalOrder io) {
		this.io = io;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
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

	public String getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public Boolean getIsPurchase(){
		return "Y".equalsIgnoreCase(this.isPurchase) ? true : false; 
	}

	public void setIsPurchase(Boolean isPurchase) {
		this.isPurchase = isPurchase ? "Y" : "N";
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public BigDecimal getQtyOrder() {
		return qtyOrder;
	}

	public void setQtyOrder(BigDecimal qtyOrder) {
		this.qtyOrder = qtyOrder;
	}

	public BigDecimal getQtyWip() {
		return qtyWip;
	}

	public void setQtyWip(BigDecimal qtyWip) {
		this.qtyWip = qtyWip;
	}

	public BigDecimal getQtyOnhand() {
		return qtyOnhand;
	}

	public void setQtyOnhand(BigDecimal qtyOnhand) {
		this.qtyOnhand = qtyOnhand;
	}

	public Date getDateDelivery() {
		return dateDelivery;
	}

	public void setDateDelivery(Date dateDelivery) {
		this.dateDelivery = dateDelivery;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public BigDecimal getQtyMin() {
		return qtyMin;
	}

	public void setQtyMin(BigDecimal qtyMin) {
		this.qtyMin = qtyMin;
	}

	public String getCustomerManager() {
		return customerManager;
	}

	public void setCustomerManager(String customerManager) {
		this.customerManager = customerManager;
	}
}

