package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.base.model.Material;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.vdm.model.Vendor;
import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="INV_IQC_LINE")
public class IqcLine extends ADUpdatable{
	private static final long serialVersionUID = 1L;
	
	private static String CONDITION_CONCESSION="CONCESSION";
	private static String CONDITION_NORMAL="NORMAL";
	
	@Column(name="IQC_RRN")
	private Long iqcRrn;
	
	@Column(name="IQC_ID")
	private String iqcId;
	
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
	
	@ManyToOne
	@JoinColumn(name = "PO_LINE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private PurchaseOrderLine poLine;
	
	@Column(name="RECEIPT_LINE_RRN")
	private Long receiptLineRrn;
	
	@ManyToOne
	@JoinColumn(name = "RECEIPT_LINE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private ReceiptLine receiptLine;
	
	@Column(name="QTY_IQC")
	private BigDecimal qtyIqc;
	
	@Column(name="QTY_QUALIFIED")
	private BigDecimal qtyQualified;
	
	@Column(name="QTY_IN")
	private BigDecimal qtyIn = BigDecimal.ZERO;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LINE_STATUS")
	private String lineStatus;

	@Column(name="IS_GENERATE_LOT")
	private String isGenerateLot;
	
	@Column(name="ERCEIVE_CONDITION")
	private String receiveCondition;
	
	@Transient
	private BigDecimal qtyPass;//合格数
	
	@Transient
	private BigDecimal qtyFailed;//不合格数
	
	@Transient
	private BigDecimal qtyConcession;//让步接收数
	
	@Transient
	private BigDecimal qtyReceipt;//接收数量
	
	public Long getIqcRrn() {
		return iqcRrn;
	}

	public void setIqcRrn(Long iqcRrn) {
		this.iqcRrn = iqcRrn;
	}

	public String getIqcId() {
		return iqcId;
	}

	public void setIqcId(String iqcId) {
		this.iqcId = iqcId;
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

	public BigDecimal getQtyIqc() {
		return qtyIqc;
	}

	public void setQtyIqc(BigDecimal qtyIqc) {
		this.qtyIqc = qtyIqc;
	}

	public BigDecimal getQtyQualified() {
		return qtyQualified;
	}

	public void setQtyQualified(BigDecimal qtyQualified) {
		this.qtyQualified = qtyQualified;
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

	public void setReceiptLineRrn(Long receiptLineRrn) {
		this.receiptLineRrn = receiptLineRrn;
	}

	public Long getReceiptLineRrn() {
		return receiptLineRrn;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() {
		return material;
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
	
	public String getMaterialUomId() {
		if (this.material != null) {
			return this.material.getInventoryUom();
		}
		return "";
	}

	public String getLotType() {
		if(material != null) {
			return material.getLotType();
		}
		return "";
	}

	public BigDecimal getReceiptLineQtyReceipt() {
		if(qtyReceipt != null)
			return qtyReceipt;
		
		if (this.receiptLine != null) {
			return this.receiptLine.getQtyReceipt();
		}
		return BigDecimal.ZERO;
	}
	
	public String getPoId() {
		if (this.poLine != null) {
			return this.poLine.getPoId();
		}
		return "";
	}

	public Boolean getIsGenerateLot(){
		return "Y".equalsIgnoreCase(this.isGenerateLot) ? true : false; 
	}

	public void setIsGenerateLot(Boolean isGenerateLot) {
		this.isGenerateLot = isGenerateLot ? "Y" : "N";
	}
	
	public String getIsGenerateLot_zh(){
		return getIsGenerateLot() ? "YES":"NO"; 
	}

	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
	}

	public BigDecimal getQtyIn() {
		return qtyIn;
	}

	public String getReceiveCondition() {
		return receiveCondition;
	}

	public void setReceiveCondition(String receiveCondition) {
		this.receiveCondition = receiveCondition;
	}
	
	@Transient
	public Boolean getIsInspectionFree(){
//		if(material != null){
//			return material.getIsInspectionFree();
//		}else{
//			return false;
//		}
		if(orgRrn==139420L){//开能区域
			if (poLine != null) {//是否免检从采购订单带过来，由祁椅维护
				return poLine.getIsInspectionFree();
			} else {
				return false;
			}
		}else{
			if (material != null) {//其他区域
				return material.getIsInspectionFree();
			} else {
				return false;
			}
		}

	}
	
	@Transient
	public String getUrgency(){
		if(poLine != null){
			return poLine.getUrgency();
		}
		return "";
	}
	
	@Transient
	public String getVendorId(){
		if(poLine != null){
			PurchaseOrder po = poLine.getPo();
			if(po != null){
				Vendor vendor = poLine.getPo().getVendor();
				if(vendor != null){
					return (vendor.getVendorId() == null ? "" : vendor.getVendorId());
				}
			}
		}
		return "";
	}
	
	@Transient
	public String getVendorName(){
		if(poLine != null){
			PurchaseOrder po = poLine.getPo();
			if(po != null){
				Vendor vendor = poLine.getPo().getVendor();
				if(vendor != null){
					return (vendor.getCompanyName() == null ? "" : vendor.getCompanyName());
				}
			}
		}
		return "";
	}

	public PurchaseOrderLine getPoLine() {
		return poLine;
	}

	public void setPoLine(PurchaseOrderLine poLine) {
		this.poLine = poLine;
	}

	public ReceiptLine getReceiptLine() {
		return receiptLine;
	}

	public void setReceiptLine(ReceiptLine receiptLine) {
		this.receiptLine = receiptLine;
	}

	public BigDecimal getQtyPass() {
		if(qtyPass != null){
			return qtyPass;
		}
		
		if(receiveCondition == null || receiveCondition.equals(CONDITION_NORMAL)){
			qtyPass = qtyQualified;
		}else{
			//让步接受
			qtyPass =  BigDecimal.ZERO;
		}
		return qtyPass;
	}

	public void setQtyPass(BigDecimal qtyPass) {
		this.qtyPass = qtyPass;
	}

	public BigDecimal getQtyFailed() {
		if(qtyFailed != null)
			return qtyFailed;
		
		qtyFailed = qtyIqc.subtract(qtyQualified);
		return qtyFailed;
	}

	public void setQtyFailed(BigDecimal qtyFailed) {
		this.qtyFailed = qtyFailed;
	}

	public BigDecimal getQtyConcession() {
		if(qtyConcession != null)
			return qtyConcession;
		
		if(receiveCondition != null && receiveCondition.equals(CONDITION_CONCESSION)){
			//让步接受
			qtyConcession = qtyQualified;
		}else{
			qtyConcession = BigDecimal.ZERO;
		}
		return qtyConcession;
	}

	public void setQtyConcession(BigDecimal qtyConcession) {
		this.qtyConcession = qtyConcession;
	}

	public BigDecimal getQtyReceipt() {
		return qtyReceipt;
	}

	public void setQtyReceipt(BigDecimal qtyReceipt) {
		this.qtyReceipt = qtyReceipt;
	}
	
	@Transient
	public BigDecimal getPoLineQty(){
		if(poLine != null){
			return poLine.getQty();
		}
		return null;
	}
}
