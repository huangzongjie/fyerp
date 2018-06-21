package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_RECEIPT_LINE")
public class ReceiptLine extends ADUpdatable{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="RECEIPT_RRN")
	private Long receiptRrn;
	
	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name="LINE_NO")
	private Long lineNo;
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@ManyToOne
	@JoinColumn(name = "MATERIAL_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private Material material;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="QTY_RECEIPT")
	private BigDecimal qtyReceipt;
	
	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;

	@ManyToOne
	@JoinColumn(name = "PO_LINE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private PurchaseOrderLine poLine;
	
	@Column(name="IS_IQC")
	private String isIqc = "N";
	
	public Long getReceiptRrn() {
		return receiptRrn;
	}

	public void setReceiptRrn(Long receiptRrn) {
		this.receiptRrn = receiptRrn;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getReceiptId() {
		return receiptId;
	}
	
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

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public Long getPoLineRrn() {
		return poLineRrn;
	}

	public void setPoLineRrn(Long poLineRrn) {
		this.poLineRrn = poLineRrn;
	}

	public BigDecimal getQtyReceipt() {
		return qtyReceipt;
	}

	public void setQtyReceipt(BigDecimal qtyReceipt) {
		this.qtyReceipt = qtyReceipt;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}
	
	public String getMaterialName() {
		if (this.material != null) {
			return this.material.getName();
		}
		return "";
	}
	public String getMaterialId() {
		if (this.material != null) {
			return this.material.getMaterialId();
		}
		return "";
	}
	
	@Transient
	public Boolean getIsInspectionFree(){
//		return material.getIsInspectionFree();
		if(orgRrn==139420L){
			if(poLine != null){//是否免检从采购订单带过来，由祁椅维护
				return poLine.getIsInspectionFree();
			}
		}else{
			return material.getIsInspectionFree();
		}
		return false;
	}

	public PurchaseOrderLine getPoLine() {
		return poLine;
	}

	public void setPoLine(PurchaseOrderLine poLine) {
		this.poLine = poLine;
	}
	
	@Transient
	public String getUrgency(){
		if(poLine != null){
			return poLine.getUrgency();
		}
		return "";
	}
	
	public Boolean getIsIqc(){
		return "Y".equalsIgnoreCase(this.isIqc) ? true : false; 
	}

	public void setIsIqc(Boolean isIqc) {
		this.isIqc = isIqc ? "Y" : "N";
	}
	@Transient
	public String getPoId() {
		if (this.poLine != null) {
			return this.poLine.getPoId();
		}
		return "";
	}
	@Transient
	public BigDecimal getPoLineQty() {
		if (this.poLine != null) {
			return this.poLine.getQty();
		}
		return null;
	}
}