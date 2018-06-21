package com.graly.erp.inv.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class MaterialTraceDetail implements Serializable {
	private static final long	serialVersionUID	= -458265833397203708L;
	
	public static final String DETAIL_TYPE_PIN = "PIN";//�ɹ����
	public static final String DETAIL_TYPE_WIN = "WIN";//�������
	public static final String DETAIL_TYPE_RIN = "RIN";//�˿�
	public static final String DETAIL_TYPE_OIN = "OIN";//�������
	public static final String DETAIL_TYPE_ADIN = "ADIN";//Ӫ�˵������
	
	public static final String DETAIL_TYPE_SOU = "SOU";//���۳���
	public static final String DETAIL_TYPE_OOU = "OOU";//��������
	public static final String DETAIL_TYPE_ADOU = "ADOU";//Ӫ�˵�������
	
	public static final String DETAIL_TYPE_AOUIN = "AOUIN";//����������
	public static final String DETAIL_TYPE_AOUOU = "AOUOU";//����������
	
	public static final String DETAIL_TYPE_MWO = "MWO";//�ֹ�����
	
	public static final String DETAIL_TYPE_DISASSEMBLEIN = "DISIN";//�����
	public static final String DETAIL_TYPE_DISASSEMBLEOU = "DISOU";//���

	private String movementId;//��ⵥ���
	private String poId;//�ɹ��������
	private String receiptId;//�ջ������
	
	private String moId;//��������
	
	private BigDecimal qty;//����
	
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
