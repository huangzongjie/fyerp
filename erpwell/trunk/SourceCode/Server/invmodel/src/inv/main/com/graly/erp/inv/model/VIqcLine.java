package com.graly.erp.inv.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="V_IQC_LINE")
public class VIqcLine extends ADUpdatable {
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
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="RECEIPT_LINE_RRN")
	private Long receiptLineRrn;
	
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
	
	
	@Column(name="IS_INSPECTION_FREE")//是否免检,默认否
	private String isInspectionFree;
	
	@Column(name="QTY_RECEIPT")
	private BigDecimal qtyReceipt;//接收数量
	
	@Column(name="VENDOR_ID")
	private String vendorId;

	@Column(name="VENDOR_NAME")
	private String vendorName;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name = "WMS_WAREHOUSE")
	private String wmsWarehouse;//收货仓库
	
	public BigDecimal getReceiptLineQtyReceipt() {
		if(qtyReceipt != null)
			return qtyReceipt;
		return BigDecimal.ZERO;
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

	public String getMaterialId() {
		return materialId;
	}


	public void setMaterialId(String materialId) {
		this.materialId = materialId;
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

	public Long getReceiptLineRrn() {
		return receiptLineRrn;
	}

	public void setReceiptLineRrn(Long receiptLineRrn) {
		this.receiptLineRrn = receiptLineRrn;
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

	public BigDecimal getQtyIn() {
		return qtyIn;
	}



	public void setQtyIn(BigDecimal qtyIn) {
		this.qtyIn = qtyIn;
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



	public String getIsGenerateLot() {
		return isGenerateLot;
	}



	public void setIsGenerateLot(String isGenerateLot) {
		this.isGenerateLot = isGenerateLot;
	}



	public String getReceiveCondition() {
		return receiveCondition;
	}



	public void setReceiveCondition(String receiveCondition) {
		this.receiveCondition = receiveCondition;
	}



	public BigDecimal getQtyReceipt() {
		return qtyReceipt;
	}



	public void setQtyReceipt(BigDecimal qtyReceipt) {
		this.qtyReceipt = qtyReceipt;
	}



	public String getVendorId() {
		return vendorId;
	}



	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}



	public String getVendorName() {
		return vendorName;
	}



	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}



	public String getIsInspectionFree() {
		return isInspectionFree;
	}



	public void setIsInspectionFree(String isInspectionFree) {
		this.isInspectionFree = isInspectionFree;
	}



	public String getPoId() {
		return poId;
	}



	public void setPoId(String poId) {
		this.poId = poId;
	}



	public String getMaterialName() {
		return materialName;
	}



	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}



	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getWmsWarehouse() {
		return wmsWarehouse;
	}

	public void setWmsWarehouse(String wmsWarehouse) {
		this.wmsWarehouse = wmsWarehouse;
	}
}
