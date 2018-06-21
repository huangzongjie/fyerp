package com.graly.erp.inv.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class MaterialTraceDetail implements Serializable {
	private static final long	serialVersionUID	= -458265833397203708L;
	
	public static final String DETAIL_TYPE_PIN = "PIN";//采购入库
	public static final String DETAIL_TYPE_WIN = "WIN";//生产入库
	public static final String DETAIL_TYPE_RIN = "RIN";//退库
	public static final String DETAIL_TYPE_OIN = "OIN";//其他入库
	public static final String DETAIL_TYPE_ADIN = "ADIN";//营运调整入库
	
	public static final String DETAIL_TYPE_SOU = "SOU";//销售出库
	public static final String DETAIL_TYPE_OOU = "OOU";//其他出库
	public static final String DETAIL_TYPE_ADOU = "ADOU";//营运调整出库
	
	public static final String DETAIL_TYPE_AOUIN = "AOUIN";//财务入库调整
	public static final String DETAIL_TYPE_AOUOU = "AOUOU";//财务出库调整
	
	public static final String DETAIL_TYPE_MWO = "MWO";//手工核销
	
	public static final String DETAIL_TYPE_DISASSEMBLEIN = "DISIN";//拆分入
	public static final String DETAIL_TYPE_DISASSEMBLEOU = "DISOU";//拆分

	private String movementId;//入库单编号
	private String poId;//采购订单编号
	private String receiptId;//收货单编号
	
	private String moId;//工作令编号
	
	private BigDecimal qty;//数量
	
	public String getMovementId() {
		return movementId;
	}
	public void setMovementId(String movementId) {
		this.movementId = movementId;
	}
	public String getPoId() {
		return poId;
	}
	public void setPoId(String poId) {
		this.poId = poId;
	}
	public String getMoId() {
		return moId;
	}
	public void setMoId(String moId) {
		this.moId = moId;
	}
	public String getReceiptId() {
		return receiptId;
	}
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
}
