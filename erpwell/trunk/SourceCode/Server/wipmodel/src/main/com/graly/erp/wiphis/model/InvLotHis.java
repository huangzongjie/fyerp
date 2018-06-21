package com.graly.erp.wiphis.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import com.graly.erp.base.model.Material;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.mes.wiphis.model.ProcessUnitHis;

@MappedSuperclass
public class InvLotHis extends ProcessUnitHis {
	
	private static final long serialVersionUID = 1L;
	
	public static String TRANS_IQC = "IQC";
	public static String TRANS_GENERATE = "GENERATE";
	public static String TRANS_PIN = "PIN";
	public static String TRANS_OIN = "OIN";
	public static String TRANS_WIN = "WIN";
	public static String TRANS_RIN = "RIN";
	public static String TRANS_RECEIVE = "RECEIVE";
	public static String TRANS_USED = "USED";
	public static String TRANS_SOU = "SOU";
	public static String TRANS_OOU = "OOU";
	public static String TRANS_AOU = "AOU";
	public static String TRANS_DOU = "DOU";
	public static String TRANS_ADOU = "ADOU";
	public static String TRANS_ADIN = "ADIN";
	public static String TRANS_TRANSFER = "TRANSFER";
	public static String TRANS_SPLIT = "SPILT";
	public static String TRANS_SPLITOUT = "SPILTOUT";
	public static String TRANS_MERGE = "MERGE";
	public static String TRANS_MERGEOUT = "MERGEOUT";
	public static String TRANS_DISASSEMBLE = "DISASSEMBLE";
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="LOCATOR_RRN")
	private Long locatorRrn;
	
	@Column(name="LOCATOR_ID")
	private String locatorId;
	
	@Column(name="USED_LOT_RRN")
	private Long usedLotRrn;
	
	@Column(name="RECEIPT_RRN")
	private Long receiptRrn;

	@Column(name="RECEIPT_ID")
	private String receiptId;
	
	@Column(name="IQC_RRN")
	private Long iqcRrn;
	
	@Column(name="IQC_ID")
	private String iqcId;
	
	@Column(name="IQC_LINE_RRN")
	private Long iqcLineRrn;
	
	@Column(name="PO_RRN")
	private Long poRrn;
	
	@Column(name="PO_ID")
	private String poId;
	
	@Column(name="PO_LINE_RRN")
	private Long poLineRrn;
	
	@Column(name="IN_RRN")
	private Long inRrn;
	
	@Column(name="IN_ID")
	private String inId;
	
	@Column(name="IN_LINE_RRN")
	private Long inLineRrn;
	
	@Column(name="OUT_RRN")
	private Long outRrn;
	
	@Column(name="OUT_ID")
	private String outId;
	
	@Column(name="OUT_LINE_RRN")
	private Long outLineRrn;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="MO_LINE_RRN")
	private Long moLineRrn;
	
	@Column(name="QTY_INITIAL")
	private BigDecimal qtyInitial;
	
	@Column(name="QTY_CURRENT")
	private BigDecimal qtyCurrent;
	
	@Column(name="QTY_TRANSACTION")
	private BigDecimal qtyTransaction;
	
	@Column(name="IS_USED")
	private String isUsed = "N";
	
	@Column(name="USER_QC")
	private String userQc;
	
	@Column(name="DATE_IN")
	private Date dateIn;

	@Column(name="DATE_OUT")
	private Date dateOut;

	@Column(name="DATE_PRODUCT")
	private Date dateProduct;

	@Column(name="WORKCENTER_RRN")
	private Long workCenterRrn;
	
	@Column(name="WORKCENTER_ID")
	private String workCenterId;

	@Column(name="POSITION")
	private String position;
	
	@Column(name="QTY_USED")
	private BigDecimal qtyUsed;
	
	@Column(name="REVERSE_FIELD1")
	private String reverseField1;
	
	@Column(name="REVERSE_FIELD2")
	private String reverseField2;
	
	@Column(name="REVERSE_FIELD3")
	private String reverseField3;
	
	@Column(name="REVERSE_FIELD4")
	private String reverseField4;
	
	@Column(name="REVERSE_FIELD5")
	private String reverseField5;
	
	@Column(name="REVERSE_FIELD6")
	private String reverseField6;
	
	@Column(name="REVERSE_FIELD7")
	private String reverseField7;
	
	@Column(name="REVERSE_FIELD8")
	private String reverseField8;
	
	@Column(name="REVERSE_FIELD9")
	private String reverseField9;
	
	@Column(name="REVERSE_FIELD10")
	private String reverseField10;

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getMaterialName() {
		return materialName;
	}
	
	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Long getLocatorRrn() {
		return locatorRrn;
	}

	public void setLocatorRrn(Long locatorRrn) {
		this.locatorRrn = locatorRrn;
	}

	public String getLocatorId() {
		return locatorId;
	}

	public void setLocatorId(String locatorId) {
		this.locatorId = locatorId;
	}

	public void setUsedLotRrn(Long usedLotRrn) {
		this.usedLotRrn = usedLotRrn;
	}

	public Long getUsedLotRrn() {
		return usedLotRrn;
	}
	
	public Long getReceiptRrn() {
		return receiptRrn;
	}

	public void setReceiptRrn(Long receiptRrn) {
		this.receiptRrn = receiptRrn;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
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

	public Long getPoRrn() {
		return poRrn;
	}

	public void setPoRrn(Long poRrn) {
		this.poRrn = poRrn;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public Long getInRrn() {
		return inRrn;
	}

	public void setInRrn(Long inRrn) {
		this.inRrn = inRrn;
	}

	public String getInId() {
		return inId;
	}

	public void setInId(String inId) {
		this.inId = inId;
	}

	public Long getOutRrn() {
		return outRrn;
	}

	public void setOutRrn(Long outRrn) {
		this.outRrn = outRrn;
	}

	public String getOutId() {
		return outId;
	}

	public void setOutId(String outId) {
		this.outId = outId;
	}

	public BigDecimal getQtyInitial() {
		return qtyInitial;
	}

	public void setQtyInitial(BigDecimal qtyInitial) {
		this.qtyInitial = qtyInitial;
	}

	public Boolean getIsUsed(){
		return "Y".equalsIgnoreCase(this.isUsed) ? true : false; 
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed ? "Y" : "N";
	}
	
	public String getUserQc() {
		return userQc;
	}

	public void setUserQc(String userQc) {
		this.userQc = userQc;
	}

	public void setIqcLineRrn(Long iqcLineRrn) {
		this.iqcLineRrn = iqcLineRrn;
	}

	public Long getIqcLineRrn() {
		return iqcLineRrn;
	}

	public void setPoLineRrn(Long poLineRrn) {
		this.poLineRrn = poLineRrn;
	}

	public Long getPoLineRrn() {
		return poLineRrn;
	}

	public void setOutLineRrn(Long outLineRrn) {
		this.outLineRrn = outLineRrn;
	}

	public Long getOutLineRrn() {
		return outLineRrn;
	}

	public void setInLineRrn(Long inLineRrn) {
		this.inLineRrn = inLineRrn;
	}

	public Long getInLineRrn() {
		return inLineRrn;
	}

	public BigDecimal getQtyCurrent() {
		return qtyCurrent;
	}

	public void setQtyCurrent(BigDecimal qtyCurrent) {
		this.qtyCurrent = qtyCurrent;
	}

	public void setDateIn(Date dateIn) {
		this.dateIn = dateIn;
	}

	public Date getDateIn() {
		return dateIn;
	}

	public void setDateOut(Date dateOut) {
		this.dateOut = dateOut;
	}

	public Date getDateOut() {
		return dateOut;
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}
	
	public void setMoId(String moId) {
		this.moId = moId;
	}

	public String getMoId() {
		return moId;
	}

	public Long getMoLineRrn() {
		return moLineRrn;
	}

	public void setMoLineRrn(Long moLineRrn) {
		this.moLineRrn = moLineRrn;
	}

	public Long getWorkCenterRrn() {
		return workCenterRrn;
	}

	public void setWorkCenterRrn(Long workCenterRrn) {
		this.workCenterRrn = workCenterRrn;
	}
	
	public void setWorkCenterId(String workCenterId) {
		this.workCenterId = workCenterId;
	}

	public String getWorkCenterId() {
		return workCenterId;
	}

	public Date getDateProduct() {
		return dateProduct;
	}

	public void setDateProduct(Date dateProduct) {
		this.dateProduct = dateProduct;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

	public void setQtyUsed(BigDecimal qtyUsed) {
		this.qtyUsed = qtyUsed;
	}

	public BigDecimal getQtyUsed() {
		return qtyUsed;
	}

	public void setQtyTransaction(BigDecimal qtyTransaction) {
		this.qtyTransaction = qtyTransaction;
	}

	public BigDecimal getQtyTransaction() {
		return qtyTransaction;
	}

	public String getReverseField1() {
		return reverseField1;
	}

	public void setReverseField1(String reverseField1) {
		this.reverseField1 = reverseField1;
	}

	public String getReverseField2() {
		return reverseField2;
	}

	public void setReverseField2(String reverseField2) {
		this.reverseField2 = reverseField2;
	}

	public String getReverseField3() {
		return reverseField3;
	}

	public void setReverseField3(String reverseField3) {
		this.reverseField3 = reverseField3;
	}

	public String getReverseField4() {
		return reverseField4;
	}

	public void setReverseField4(String reverseField4) {
		this.reverseField4 = reverseField4;
	}

	public String getReverseField5() {
		return reverseField5;
	}

	public void setReverseField5(String reverseField5) {
		this.reverseField5 = reverseField5;
	}

	public String getReverseField6() {
		return reverseField6;
	}

	public void setReverseField6(String reverseField6) {
		this.reverseField6 = reverseField6;
	}

	public String getReverseField7() {
		return reverseField7;
	}

	public void setReverseField7(String reverseField7) {
		this.reverseField7 = reverseField7;
	}

	public String getReverseField8() {
		return reverseField8;
	}

	public void setReverseField8(String reverseField8) {
		this.reverseField8 = reverseField8;
	}

	public String getReverseField9() {
		return reverseField9;
	}

	public void setReverseField9(String reverseField9) {
		this.reverseField9 = reverseField9;
	}

	public String getReverseField10() {
		return reverseField10;
	}

	public void setReverseField10(String reverseField10) {
		this.reverseField10 = reverseField10;
	}
}
