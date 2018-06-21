package com.graly.erp.inv.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 实体 物料去向
 * 查看物料消耗的情况即去向
 * @author Denny
 *
 */
public class MaterialTrace implements Serializable{
	private static final long	serialVersionUID	= 1L;
	private Long materialRrn;
	private String materialId;
	private String materialName;
	/*
	 * 同时改变库存和核销数
	 */
	private BigDecimal qtyPin;//采购入库
	private BigDecimal qtyWin;//生产入库
	private BigDecimal qtyRin;//退库
	private BigDecimal qtyOin;//其他入库
	private BigDecimal qtyAouIn;//财务入库调整
	
	private BigDecimal qtySou;//销售出库
	private BigDecimal qtyOou;//其他出库
	
	private BigDecimal qtyMwo;//手工核销

	private BigDecimal qtyAouOu;//财务出库调整
	
	
	/*
	 * 只改变库存不改变核销
	 */
	private BigDecimal qtyMo;//生产接受
	private BigDecimal qtyConsume;//生产消耗
	private BigDecimal qtyAdIn;//营运入库调整
	private BigDecimal qtyAdOu;//营运出库调整
	private BigDecimal qtyDisassembleIn;//拆分入（子物料还原）
	private BigDecimal qtyDisassembleOu;//拆分（父物料拆分）
	
	
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
