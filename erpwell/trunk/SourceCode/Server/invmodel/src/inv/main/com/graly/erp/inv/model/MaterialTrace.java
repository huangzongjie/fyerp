package com.graly.erp.inv.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ʵ�� ����ȥ��
 * �鿴�������ĵ������ȥ��
 * @author Denny
 *
 */
public class MaterialTrace implements Serializable{
	private static final long	serialVersionUID	= 1L;
	private Long materialRrn;
	private String materialId;
	private String materialName;
	/*
	 * ͬʱ�ı���ͺ�����
	 */
	private BigDecimal qtyPin;//�ɹ����
	private BigDecimal qtyWin;//�������
	private BigDecimal qtyRin;//�˿�
	private BigDecimal qtyOin;//�������
	private BigDecimal qtyAouIn;//����������
	
	private BigDecimal qtySou;//���۳���
	private BigDecimal qtyOou;//��������
	
	private BigDecimal qtyMwo;//�ֹ�����

	private BigDecimal qtyAouOu;//����������
	
	
	/*
	 * ֻ�ı��治�ı����
	 */
	private BigDecimal qtyMo;//��������
	private BigDecimal qtyConsume;//��������
	private BigDecimal qtyAdIn;//Ӫ��������
	private BigDecimal qtyAdOu;//Ӫ�˳������
	private BigDecimal qtyDisassembleIn;//����루�����ϻ�ԭ��
	private BigDecimal qtyDisassembleOu;//��֣������ϲ�֣�
	
	
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
	public BigDecimal getQtyPin() {
		return qtyPin;
	}
	public void setQtyPin(BigDecimal qtyPin) {
		this.qtyPin = qtyPin;
	}
	public BigDecimal getQtyWin() {
		return qtyWin;
	}
	public void setQtyWin(BigDecimal qtyWin) {
		this.qtyWin = qtyWin;
	}
	public BigDecimal getQtyRin() {
		return qtyRin;
	}
	public void setQtyRin(BigDecimal qtyRin) {
		this.qtyRin = qtyRin;
	}
	public BigDecimal getQtyOin() {
		return qtyOin;
	}
	public void setQtyOin(BigDecimal qtyOin) {
		this.qtyOin = qtyOin;
	}
	public BigDecimal getQtyAouIn() {
		return qtyAouIn;
	}
	public void setQtyAouIn(BigDecimal qtyAouIn) {
		this.qtyAouIn = qtyAouIn;
	}
	public BigDecimal getQtySou() {
		return qtySou;
	}
	public void setQtySou(BigDecimal qtySou) {
		this.qtySou = qtySou;
	}
	public BigDecimal getQtyOou() {
		return qtyOou;
	}
	public void setQtyOou(BigDecimal qtyOou) {
		this.qtyOou = qtyOou;
	}
	public BigDecimal getQtyMwo() {
		return qtyMwo;
	}
	public void setQtyMwo(BigDecimal qtyMwo) {
		this.qtyMwo = qtyMwo;
	}
	public BigDecimal getQtyAouOu() {
		return qtyAouOu;
	}
	public void setQtyAouOu(BigDecimal qtyAouOu) {
		this.qtyAouOu = qtyAouOu;
	}
	public BigDecimal getQtyAdIn() {
		return qtyAdIn;
	}
	public void setQtyAdIn(BigDecimal qtyAdIn) {
		this.qtyAdIn = qtyAdIn;
	}
	public BigDecimal getQtyAdOu() {
		return qtyAdOu;
	}
	public void setQtyAdOu(BigDecimal qtyAdOu) {
		this.qtyAdOu = qtyAdOu;
	}
	public BigDecimal getQtyDisassembleIn() {
		return qtyDisassembleIn;
	}
	public void setQtyDisassembleIn(BigDecimal qtyDisassembleIn) {
		this.qtyDisassembleIn = qtyDisassembleIn;
	}
	public BigDecimal getQtyDisassembleOu() {
		return qtyDisassembleOu;
	}
	public void setQtyDisassembleOu(BigDecimal qtyDisassembleOu) {
		this.qtyDisassembleOu = qtyDisassembleOu;
	}
	public BigDecimal getQtyMo() {
		return qtyMo;
	}
	public void setQtyMo(BigDecimal qtyMo) {
		this.qtyMo = qtyMo;
	}
	public BigDecimal getQtyConsume() {
		return qtyConsume;
	}
	public void setQtyConsume(BigDecimal qtyConsume) {
		this.qtyConsume = qtyConsume;
	}
}
